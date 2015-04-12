package ca.ualberta.medroad.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ca.ammi.medlib.EmotionEcg;
import ca.ammi.medlib.ForaBpGlucose;
import ca.ammi.medlib.NoninOximeter;
import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.auxiliary.HttpRequestManager;
import ca.ualberta.medroad.auxiliary.handlers.EmotionEcgHandler;
import ca.ualberta.medroad.auxiliary.handlers.ForaBpGlucoseHandler;
import ca.ualberta.medroad.auxiliary.handlers.NoninOxometerHandler;
import ca.ualberta.medroad.model.raw_table_rows.DataRow;
import ca.ualberta.medroad.model.raw_table_rows.PatientRow;
import ca.ualberta.medroad.view.fragment.ConfigurationFragment;
import ca.ualberta.medroad.view.fragment.DiagnosticsFragment;
import ca.ualberta.medroad.view.fragment.PatientInfoFragment;
import ca.ualberta.medroad.view.fragment.PlaceholderFragment;
import ca.ualberta.medroad.view.list_adapters.MainMenuAdapter;

/**
 * The startup activity. This activity coordinates the view for the entire app. It's a little bit
 * of a god class but shit happens.
 */
public class MainActivity
		extends Activity
		implements EmotionEcgHandler.EcgHandlerCallbacks, ForaBpGlucoseHandler.BpGlucoseHandlerCallbacks, NoninOxometerHandler.OxometerHandlerCallbacks, ConfigurationFragment.ConfigurationCallbacks, PatientInfoFragment.FragmentCallbacks, DiagnosticsFragment.DiagnosticsCallbacks
{
	public static final String               LOG_TAG                     = "MedROAD";
	public static final int                  GRAPH_HORIZONTAL_RESOLUTION = 200;
	public static final int                  GRAPH_VERTICAL_RESOLUTION   = 500;
	public static final int                  REQUEST_ENABLE_BT           = 1;
	public static       int                  ECG_SIGNAL_RESOLUTION       = 0; // note that signal resolution is actually /1000
	public static       int                  ECG_HIGH_PASS_FILTER        = 0;
	public static       int                  ECG_SAMPLING_FREQUENCY      = 1;
	protected           ViewHolder           view                        = null;
	protected           MainMenuAdapter      menuAdapter                 = null;
	protected           FragmentManager      fragmentManager             = null;
	protected           BluetoothManager     bluetoothManager            = null;
	protected           BluetoothAdapter     mBluetoothAdapter           = null;
	protected           BluetoothDevice      rawEcgDevice                = null;
	protected           EmotionEcg           emotionEcg                  = null;
	protected           EmotionEcgHandler    ecgHandler                  = null;
	protected           BluetoothDevice      rawGlucoseBpDevice          = null;
	protected           ForaBpGlucose        foraBpGlucose               = null;
	protected           ForaBpGlucoseHandler bpGlucoseHandler            = null;
	protected           BluetoothDevice      rawNoninOxometer            = null;
	protected           NoninOximeter        noninOximeter               = null;
	protected           NoninOxometerHandler oxometerHandler             = null;
	protected           WorkerPool           workerPool                  = null;
	protected           DataRow              latestRow                   = null;
	protected           MockDataGenerator    mockDataGenerator           = new MockDataGenerator();
	protected           boolean              newData                     = false;
	private             int                  menuSelection               = -1;
	private             long                 ecgGraphCounter             = 0;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onCreate()" );

		// Housekeeping
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Initialize bluetooth
		initializeBtHandles();

		// Initialize the AppState
		AppState.initState( getApplicationContext() );
		AppState.getState().resetSession();

		// Initialize application worker threads and data.
		workerPool = new WorkerPool();
		latestRow = new DataRow();
		latestRow.session_id = AppState.getState().getCurrentSession().getId();

		fragmentManager = getFragmentManager();

		// Initialize the user-facing view.
		view = new ViewHolder( this );
		view.init();

		// Trigger the menu to go to the default page.
		onMainMenuSelect( 0 );
	}

	@Override
	protected void onStart()
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onStart()" );

		super.onStart();

		// Load the bluetooth devices
		view.ecgStatus.setLoading();
		view.bpStatus.setLoading();
		view.o2Status.setLoading();
		getPairedBtDevices();

		// Start a new session (if one hasn't already been created in onCreate)
		if ( AppState.getState().getCurrentSession() == null )
		{
			AppState.getState().resetSession();
		}
		AppState.getState().getFileManager().openSessionLog();

		// Start the HTTP worker, and signal the server that the current patient is live.
		workerPool.startHttpWorker();
		HttpRequestManager.sendPatient( new PatientRow( AppState.getState().getCurrentPatient(),
														true ) );
	}

	@Override
	protected void onResume()
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onResume()" );

		super.onResume();

		checkBtStatus();
		connectBtDevices();
	}

	@Override
	protected void onPause()
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onPause()" );

		super.onPause();
	}

	@Override
	protected void onStop()
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onStop()" );

		super.onStop();

		idleBtDevices();

		AppState.getState().getFileManager().closeSessionLog();
		AppState.getState().setCurrentSession( null );
		AppState.getState().saveState();

		mockDataGenerator.stop();

		workerPool.stopHttpWorker();
		workerPool.stopBpgWorker();
		HttpRequestManager.sendPatient( new PatientRow( AppState.getState().getCurrentPatient(),
														false ) );
	}

	@Override
	protected void onDestroy()
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onDestroy()" );

		super.onDestroy();

		disconnectAndCleanupBtDevices();
	}

	@Override
	protected void onSaveInstanceState( @NonNull Bundle outState )
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onSaveInstanceState()" );

		super.onSaveInstanceState( outState );

		AppState.getState().saveState();
	}

	@Override
	protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState )
	{
		Log.v( LOG_TAG, " [INFO] > MainActivity.onRestoreInstanceState()" );

		super.onRestoreInstanceState( savedInstanceState );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
		default:
			return super.onOptionsItemSelected( item );
		}
	}

	private void initializeBtHandles()
	{
		bluetoothManager = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
		mBluetoothAdapter = bluetoothManager.getAdapter();

		ecgHandler = new EmotionEcgHandler( this );
		bpGlucoseHandler = new ForaBpGlucoseHandler( this );
		oxometerHandler = new NoninOxometerHandler( this );
	}

	private void getPairedBtDevices()
	{
		/*
			This method is really clunky. Might want to refactor it later.
		 */

		Set< BluetoothDevice > devices = mBluetoothAdapter.getBondedDevices();

		/*
		 * Iterate through paired BT devices and find the ones we need.
		 */
		Pair< String, String > ecgInfo = AppState.getState().getEcgDevice();
		Pair< String, String > bpgInfo = AppState.getState().getBpgDevice();
		Pair< String, String > o2xInfo = AppState.getState().getO2xDevice();

		for ( BluetoothDevice device : devices )
		{
			if ( emotionEcg == null && device.getAddress().equals( ecgInfo.second ) )
			{
				rawEcgDevice = device;
				emotionEcg = new EmotionEcg( rawEcgDevice, new Handler( ecgHandler ) );
				continue;
			}

			if ( foraBpGlucose == null && device.getAddress().equals( bpgInfo.second ) )
			{
				rawGlucoseBpDevice = device;
				foraBpGlucose = new ForaBpGlucose( rawGlucoseBpDevice,
												   new Handler( bpGlucoseHandler ) );
				continue;
			}

			if ( noninOximeter == null && device.getAddress().equals( o2xInfo.second ) )
			{
				rawNoninOxometer = device;
				noninOximeter = new NoninOximeter( rawNoninOxometer,
												   new Handler( oxometerHandler ) );
			}
		}

		if ( emotionEcg == null )
		{
			view.ecgStatus.setBad();
		}

		if ( foraBpGlucose == null )
		{
			view.bpStatus.setBad();
		}

		if ( noninOximeter == null )
		{
			view.o2Status.setBad();
		}
	}

	private void connectBtDevices()
	{
		if ( emotionEcg != null )
		{
			emotionEcg.connect();
		}

		if ( foraBpGlucose != null )
		{
			foraBpGlucose.connect();
		}

		if ( noninOximeter != null )
		{
			noninOximeter.connect();
		}
	}

	private void idleBtDevices()
	{
		if ( emotionEcg != null )
		{
			emotionEcg.stopData();
			emotionEcg.stopAndIdle();
		}

		/*
		if ( foraBpGlucose != null )
		{
			// Noting to do here.
		}//*/

		if ( noninOximeter != null )
		{
			noninOximeter.stopData();
		}
	}

	private void disconnectAndCleanupBtDevices()
	{
		if ( emotionEcg != null )
		{
			emotionEcg.disconnect();
			emotionEcg.cleanup();
			emotionEcg = null;
			rawEcgDevice = null;
		}

		if ( foraBpGlucose != null )
		{
			foraBpGlucose.disconnect();
			foraBpGlucose.cleanup();
			foraBpGlucose = null;
			rawGlucoseBpDevice = null;
		}

		if ( noninOximeter != null )
		{
			noninOximeter.disconnect();
			noninOximeter.cleanup();
			noninOximeter = null;
			rawNoninOxometer = null;
		}
	}

	private void checkBtStatus()
	{
		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
		// fire an intent to display a dialog asking the user to grant permission to enable it.
		if ( !mBluetoothAdapter.isEnabled() )
		{
			Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
			startActivityForResult( enableBtIntent, REQUEST_ENABLE_BT );
		}
	}

	private void onMainMenuSelect( int pos )
	{
		switch ( (int) menuAdapter.getItemId( pos ) )
		{
		case -1:
			// Placeholder
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame, PlaceholderFragment.newInstance() )
						   .commit();
			break;

		case MainMenuAdapter.ID_PATIENT_INFO:
			if ( menuSelection != MainMenuAdapter.ID_PATIENT_INFO )
			{
				fragmentManager.beginTransaction()
							   .replace( R.id.main_frame, PatientInfoFragment.newInstance( this ) )
							   .commit();
				menuSelection = MainMenuAdapter.ID_PATIENT_INFO;
			}
			break;

		case MainMenuAdapter.ID_DIAGNOSTICS:
			if ( menuSelection != MainMenuAdapter.ID_DIAGNOSTICS )
			{
				fragmentManager.beginTransaction()
							   .replace( R.id.main_frame, DiagnosticsFragment.newInstance( this ) )
							   .commit();
				menuSelection = MainMenuAdapter.ID_DIAGNOSTICS;
			}
			break;

		case MainMenuAdapter.ID_ALARMS:
			if ( menuSelection != MainMenuAdapter.ID_ALARMS )
			{
				fragmentManager.beginTransaction()
							   .replace( R.id.main_frame, PlaceholderFragment.newInstance() )
							   .commit();
				menuSelection = MainMenuAdapter.ID_ALARMS;
			}
			break;

		case MainMenuAdapter.ID_LOGIN:
			if ( menuSelection != MainMenuAdapter.ID_LOGIN )
			{
				fragmentManager.beginTransaction()
							   .replace( R.id.main_frame, PlaceholderFragment.newInstance() )
							   .commit();
				menuSelection = MainMenuAdapter.ID_LOGIN;
			}
			break;

		case MainMenuAdapter.ID_CONFIG:
			if ( menuSelection != MainMenuAdapter.ID_CONFIG )
			{
				fragmentManager.beginTransaction()
							   .replace( R.id.main_frame,
										 ConfigurationFragment.newInstance( this ) )
							   .commit();
				menuSelection = MainMenuAdapter.ID_CONFIG;
			}
			break;

		default:
			Log.w( LOG_TAG,
				   " [WARN] > MainActivity.onMainMenuSelect defaulted on ID " + menuAdapter.getItemId(
						   pos ) );
		}
	}

	@Override
	public void onEcgBtConnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > ECG bluetooth connected" );
		AppState.writeToSessionLog( "ECG bluetooth device connected." );

		if ( emotionEcg != null )
		{
			emotionEcg.setSamplingFrequency( ECG_SAMPLING_FREQUENCY );
			emotionEcg.setHighPassFilter( ECG_HIGH_PASS_FILTER );
			emotionEcg.setSignalResolution( ECG_SIGNAL_RESOLUTION );

			emotionEcg.setupAndStartRead();
			view.ecgStatus.setGood();
		}
		else
		{
			Log.e( LOG_TAG,
				   " [ BT ] > Tried to startHttpWorker reading ECG data, but the object handle was null." );
			view.ecgStatus.setBad();
		}
	}

	@Override
	public void onEcgBtDisconnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > ECG bluetooth disconnected" );
		AppState.writeToSessionLog( "ECG bluetooth device disconnected." );
		view.ecgStatus.setBad();
	}

	@Override
	public void onEcgPacketReceive( final EmotionEcg.EcgData data )
	{
		Log.v( LOG_TAG, " [ BT ] > ECG packet received" );
		if ( data == null )
		{
			return;
		}

		AppState.writeToSessionLog( "ECG packet received {no:" + data.getPacketNumber() + ",samples:" + Arrays
				.toString( data.getSamples() ) + ",rr_ok:" + data.got_rr_interval + ",rr_interval:" + data
											.getRrInterval() + "}" );

		final int[] ecgData = data.getSamples();
		//final int rrInterval = data.getRrInterval();

		latestRow.mv = String.valueOf( ecgData[ 0 ] );
		newData = true;

		runOnUiThread( new Runnable()
		{
			@Override
			public void run()
			{
				for ( int datum : ecgData )
				{
					view.ecgSeries.appendData( new DataPoint( ++ecgGraphCounter, datum ),
											   true,
											   GRAPH_HORIZONTAL_RESOLUTION );
				}
			}
		} );
	}

	@Override
	public void onEcgPulseReceive( int pulse )
	{
		Log.v( LOG_TAG, " [ BT ] > ECG pulse received " + pulse );
		AppState.writeToSessionLog( "ECG pulse received {pulse:" + pulse + "}" );

		final String pulseStr = String.valueOf( pulse );
		latestRow.pulse = pulseStr;

		runOnUiThread( new Runnable()
		{
			@Override
			public void run()
			{
				view.ecgText.setText( pulseStr );
			}
		} );
	}

	@Override
	public void onBpGlucoseBtConnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > BPG bluetooth connected" );
		AppState.writeToSessionLog( "BPG bluetooth device connected." );

		if ( foraBpGlucose != null )
		{
			workerPool.startBPGWorker();
			view.bpStatus.setGood();
		}
		else
		{
			view.bpStatus.setBad();
		}
	}

	@Override
	public void onBpGlucoseBtDisconnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > BPG bluetooth disconnected" );
		AppState.writeToSessionLog( "BPG bluetooth device disconnected." );

		workerPool.stopBpgWorker();
		view.bpStatus.setBad();
	}

	@Override
	public void onBpGlucosePacketReceive( final ForaBpGlucose.ForaData data )
	{
		Log.v( LOG_TAG, " [ BT ] > BPG packet received" );
		if ( data == null )
		{
			return;
		}

		AppState.writeToSessionLog( "BPG packet received {time:" + data.getTimeStamp() + ",data:" + data
				.getDataString() + "}" );

		int systolic = data.getSystolic();
		int diastolic = data.getDiastolic();
		if ( systolic < 10 ) return;
		final int map = (int) ( ( ( 2.0 / 3.0 ) * diastolic ) + ( ( 1.0 / 3.0 ) * systolic ) );
		final String strSystolic = String.valueOf( systolic );
		final String strDiastolic = String.valueOf( diastolic );
		final String strMap = String.valueOf( map );

		latestRow.systolicbp = strSystolic;
		latestRow.diastolicbp = strDiastolic;
		latestRow.map2 = strDiastolic;
		newData = true;

		runOnUiThread( new Runnable()
		{
			@Override
			public void run()
			{
				view.sbpText.setText( strSystolic );
				view.dbpText.setText( strDiastolic );
				view.mapText.setText( strMap );
			}
		} );
	}

	@Override
	public void onBpGlucoseDataError()
	{
		Log.e( LOG_TAG, "There was a data error in a BPG packet checksum." );
		AppState.writeToSessionLog( "There was a data error in a BPG packet checksum." );
		Toast.makeText( this,
						"Blood Pressure Sensor Error - Readings may be inaccurate.",
						Toast.LENGTH_LONG ).show();
	}

	@Override
	public void onBpGlucoseDataAvailable()
	{
		Log.v( LOG_TAG, "[ BT ] > BPG data available." );
		if ( foraBpGlucose != null )
		{
			foraBpGlucose.getLatestData();
		}
	}

	@Override
	public void onOxometerBtConnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > O2X bluetooth connected" );
		AppState.writeToSessionLog( "O2X bluetooth device connected." );
		if ( noninOximeter != null )
		{
			noninOximeter.getData();
			view.o2Status.setGood();
		}
		else
		{
			view.o2Status.setBad();
		}
	}

	@Override
	public void onOxometerBtDisconnected( BluetoothDevice device )
	{
		Log.v( LOG_TAG, " [ BT ] > O2X bluetooth disconnected" );
		AppState.writeToSessionLog( "O2X bluetooth device disconnected." );
		view.o2Status.setBad();
	}

	@Override
	public void onOxometerPacketReceive( NoninOximeter.NoninData data )
	{
		Log.v( LOG_TAG, " [ BT ] > O2X packet received" );
		if ( data == null )
		{
			return;
		}

		AppState.writeToSessionLog( "BPG packet received {time:" + data.getTimeStamp() + ",SpO2:" + data
				.getSpO2() + ",pulse:" + data.getPulse() + "}" );

		int pulse = data.getPulse();
		int spo2 = data.getSpO2();

		final String strPulse = String.valueOf( pulse );
		final String strSpo2 = String.valueOf( spo2 );

		latestRow.oxygen = strSpo2;
		newData = true;

		runOnUiThread( new Runnable()
		{
			@Override
			public void run()
			{
				view.o2Text.setText( strSpo2 );
			}
		} );
	}

	@Override
	public void onEcgDeviceChange()
	{
		// Stop and clear the device if it exists.
		view.ecgStatus.setLoading();

		InitEcgAsync task = new InitEcgAsync();
		task.execute();

		emotionEcg = new EmotionEcg( rawEcgDevice, new Handler( ecgHandler ) );
		emotionEcg.connect();
	}

	@Override
	public void onBpDeviceChange()
	{
		// Stop and clear the device if it exists.
		view.bpStatus.setLoading();

		if ( foraBpGlucose != null )
		{
			foraBpGlucose.disconnect();
			foraBpGlucose.cleanup();
			foraBpGlucose = null;
			rawGlucoseBpDevice = null;
		}

		// Connect the new device.
		rawGlucoseBpDevice = mBluetoothAdapter.getRemoteDevice( AppState.getState()
																		.getBpgDevice().second );
		foraBpGlucose = new ForaBpGlucose( rawGlucoseBpDevice, new Handler( bpGlucoseHandler ) );
		foraBpGlucose.connect();
	}

	@Override
	public void onO2DeviceChange()
	{
		// Stop and clear the device if it exists.
		view.o2Status.setLoading();

		if ( noninOximeter != null )
		{
			noninOximeter.stopData();
			noninOximeter.disconnect();
			noninOximeter.cleanup();
			noninOximeter = null;
			rawNoninOxometer = null;
		}

		// Connect the new device.
		rawNoninOxometer = mBluetoothAdapter.getRemoteDevice( AppState.getState()
																	  .getO2xDevice().second );
		noninOximeter = new NoninOximeter( rawNoninOxometer, new Handler( oxometerHandler ) );
		noninOximeter.connect();
	}

	@Override
	public void triggerPatientInfoFragmentReload()
	{
		fragmentManager.beginTransaction()
					   .replace( R.id.main_frame, PatientInfoFragment.newInstance( this ) )
					   .commit();

		menuSelection = MainMenuAdapter.ID_PATIENT_INFO;
	}

	@Override
	public void onDiagnosticsRefreshEcg()
	{
		onEcgDeviceChange();
	}

	@Override
	public void onDiagnosticsRefreshBp()
	{
		onBpDeviceChange();
	}

	@Override
	public void onDiagnosticsRefreshO2()
	{
		onO2DeviceChange();
	}

	protected class ViewHolder
	{
		public    GraphView                    ecgGraph;
		public    TextView                     ecgText;
		public    DataStatusIndicator          ecgStatus;
		public    TextView                     sbpText;
		public    TextView                     dbpText;
		public    TextView                     mapText;
		public    DataStatusIndicator          bpStatus;
		public    TextView                     o2Text;
		public    DataStatusIndicator          o2Status;
		public    ListView                     mainMenu;
		public    FrameLayout                  frame;
		protected LineGraphSeries< DataPoint > ecgSeries;

		public ViewHolder( MainActivity activity )
		{
			ecgGraph = (GraphView) activity.findViewById( R.id.main_ecg_graph );
			ecgText = (TextView) activity.findViewById( R.id.main_ecg_text );
			ecgStatus = new DataStatusIndicator( activity,
												 R.id.main_ecg_ic_good,
												 R.id.main_ecg_ic_bad,
												 R.id.main_ecg_progressBar );

			sbpText = (TextView) activity.findViewById( R.id.main_sbp_text );
			dbpText = (TextView) activity.findViewById( R.id.main_dbp_text );
			mapText = (TextView) activity.findViewById( R.id.main_map_text );
			bpStatus = new DataStatusIndicator( activity,
												R.id.main_bp_ic_good,
												R.id.main_bp_ic_bad,
												R.id.main_bp_progressBar );

			o2Text = (TextView) activity.findViewById( R.id.main_o2_text );
			o2Status = new DataStatusIndicator( activity,
												R.id.main_o2_ic_good,
												R.id.main_o2_ic_bad,
												R.id.main_o2_progressBar );

			mainMenu = (ListView) activity.findViewById( R.id.main_list_view );
			frame = (FrameLayout) activity.findViewById( R.id.main_frame );
		}

		public void init()
		{
			setupGraph();
			setupMenu();
		}

		private void setupGraph()
		{
			ecgSeries = new LineGraphSeries<>();

			Resources res = getResources();

			ecgSeries.setColor( res.getColor( R.color.green_dark ) );

			formatGraph( ecgGraph );

			ecgGraph.addSeries( ecgSeries );
		}

		private void formatGraph( GraphView graph )
		{
			graph.getViewport().setXAxisBoundsManual( true );
			graph.getViewport().setMaxX( GRAPH_HORIZONTAL_RESOLUTION );
			graph.getViewport().setMinX( 0 );
			graph.getGridLabelRenderer().setHorizontalLabelsVisible( false );
			graph.getGridLabelRenderer().setVerticalLabelsVisible( false );
		}

		private void setupMenu()
		{
			menuAdapter = MainMenuAdapter.newInstance( MainActivity.this );
			mainMenu.setAdapter( menuAdapter );
			mainMenu.setOnItemClickListener( new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
				{
					onMainMenuSelect( position );
				}
			} );
		}

		public class DataStatusIndicator
		{
			public ImageView   bad;
			public ImageView   good;
			public ProgressBar loading;

			public DataStatusIndicator( MainActivity activity,
										int goodResID,
										int badResID,
										int loadingResId )
			{
				bad = (ImageView) activity.findViewById( badResID );
				good = (ImageView) activity.findViewById( goodResID );
				loading = (ProgressBar) activity.findViewById( loadingResId );
			}

			public void setBad()
			{
				good.setVisibility( View.GONE );
				loading.setVisibility( View.GONE );
				bad.setVisibility( View.VISIBLE );
			}

			public void setGood()
			{
				bad.setVisibility( View.GONE );
				loading.setVisibility( View.GONE );
				good.setVisibility( View.VISIBLE );
			}

			public void setLoading()
			{
				bad.setVisibility( View.GONE );
				good.setVisibility( View.GONE );
				loading.setVisibility( View.VISIBLE );
			}
		}
	}

	/*
		A class that can be used to generate some fake data points on the graphs. For
		demonstration only.
	 */
	@SuppressWarnings( "UnusedDeclaration" )
	private class MockDataGenerator
	{
		public static final int                         NUM_WORKERS = 1;
		private             ScheduledThreadPoolExecutor workerPool  = null;
		private             ScheduledFuture             worker      = null;

		public MockDataGenerator()
		{
			workerPool = new ScheduledThreadPoolExecutor( NUM_WORKERS );
		}

		public void start()
		{
			if ( worker == null )
			{
				worker = workerPool.scheduleAtFixedRate( new Worker( view ),
														 0,   /* Initial delay */
														 150, /* Period */
														 TimeUnit.MILLISECONDS );
			}
		}

		public void stop()
		{
			if ( worker != null )
			{
				worker.cancel( true );
				worker = null;
			}
		}

		private class Worker
				implements Runnable
		{
			private ViewHolder view = null;
			private Random     rng  = new Random();

			public Worker( ViewHolder view )
			{
				this.view = view;
			}

			@Override
			public void run()
			{
				final int[] ecgData = new int[]{
						1,
						2,
						3,
						4,
						5,
						6,
						7,
						8,
						9,
						10,
						11,
						12,
						13,
						14,
						15,
						16,
						17,
						18,
						19,
						20,
						21,
						22,
						23,
						24,
				};

				runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						for ( int datum : ecgData )
						{
							view.ecgSeries.appendData( new DataPoint( ++ecgGraphCounter, datum ),
													   true,
													   GRAPH_HORIZONTAL_RESOLUTION );
						}
					}
				} );
			}

			private void oldGen()
			{
				ecgGraphCounter++;

				final double ecg = 10 * Math.sin( System.currentTimeMillis() / Math.PI );
				final String pulse = String.valueOf( 80 + ( rng.nextInt( 3 ) - 1 ) );

				final int sbp = 120 + ( rng.nextInt( 3 ) - 1 );
				final String ssbp = String.valueOf( sbp );

				final int dbp = 80 + ( rng.nextInt( 3 ) - 1 );
				final String sdbp = String.valueOf( "/" + dbp );

				final int spo2 = 95 + ( rng.nextInt( 5 ) - 2 );
				final String sspo2 = String.valueOf( spo2 + "%" );

				/* Mean Arterial Pressure
				* http://en.wikipedia.org/wiki/Mean_arterial_pressure
				* */
				final int map = (int) ( ( ( 2.0 / 3.0 ) * dbp ) + ( ( 1.0 / 3.0 ) * sbp ) );
				final String smap = String.valueOf( map );

				/* UI updates must be run on the UI thread */
				runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						view.ecgText.setText( pulse );
						view.sbpText.setText( ssbp );
						view.dbpText.setText( sdbp );
						view.mapText.setText( smap );
						view.o2Text.setText( sspo2 );

						/* This bogs down the UI thread a lot. May need to find an alternative... */
						view.ecgSeries.appendData( new DataPoint( ecgGraphCounter, ecg ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
					}
				} );
			}
		}
	}

	/*
		A class that manages worker threads.
	 */
	private class WorkerPool
	{
		public static final int                         NUM_WORKERS    = 2;
		public static final int                         HTTP_INTERVAL  = 10;
		public static final int                         BPG_INTERVAL   = 5;
		public final        TimeUnit                    HTTP_TIME_UNIT = TimeUnit.SECONDS;
		public final        TimeUnit                    BPG_TIME_UNIT  = TimeUnit.SECONDS;
		protected           ScheduledThreadPoolExecutor threadPool     = null;
		protected           ScheduledFuture             httpWorkerTask = null;
		protected           ScheduledFuture             bpgWorkerTask  = null;

		public WorkerPool()
		{
			threadPool = new ScheduledThreadPoolExecutor( NUM_WORKERS );
		}

		public void startHttpWorker()
		{
			if ( httpWorkerTask == null )
			{
				httpWorkerTask = threadPool.scheduleAtFixedRate( new HttpWorker(),
																 0,
																 HTTP_INTERVAL,
																 HTTP_TIME_UNIT );
			}
		}

		public void stopHttpWorker()
		{
			if ( httpWorkerTask != null )
			{
				httpWorkerTask.cancel( true );
			}
		}

		public void startBPGWorker()
		{
			if ( bpgWorkerTask == null )
			{
				bpgWorkerTask = threadPool.scheduleAtFixedRate( new ForaBPGWorker(),
																0,
																BPG_INTERVAL,
																BPG_TIME_UNIT );
			}
		}

		public void stopBpgWorker()
		{
			if ( bpgWorkerTask != null )
			{
				bpgWorkerTask.cancel( true );
			}
		}

		/**
		 * Periodically posts data to the web server.
		 */
		protected class HttpWorker
				implements Runnable
		{
			@Override
			public void run()
			{
				if ( newData )
				{
					latestRow.patient_id = String.valueOf( AppState.getState()
																   .getCurrentPatient()
																   .getId() );
					latestRow.timestamp = DataRow.sdf.format( Calendar.getInstance().getTime() );
					HttpRequestManager.sendData( latestRow );
					newData = false;
				}
			}
		}

		/**
		 * Periodically makes a call to the bpg to check for data.
		 */
		protected class ForaBPGWorker
				implements Runnable
		{
			@Override
			public void run()
			{
				if ( foraBpGlucose != null )
				{
					foraBpGlucose.checkForData();
				}
			}
		}
	}

	/* 	For some reason, re-init-ing the ECG device (but not the others) hangs the UI for about 2
		seconds, so we need to do this in its own thread. */
	private class InitEcgAsync
			extends AsyncTask< Void, Void, Void >
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			if ( emotionEcg != null )
			{
				emotionEcg.stopAndIdle();
				emotionEcg.disconnect();
				emotionEcg.cleanup();
				emotionEcg = null;
				rawEcgDevice = null;
			}

			// Connect the new device.
			rawEcgDevice = mBluetoothAdapter.getRemoteDevice( AppState.getState()
																	  .getEcgDevice().second );

			return null;
		}
	}
}
