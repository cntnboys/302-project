package ca.ualberta.medroad.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ca.ammi.medlib.BtHealthDevice;
import ca.ammi.medlib.EmotionEcg;
import ca.ammi.medlib.ForaBpGlucose;
import ca.ammi.medlib.NoninOximeter;
import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.auxiliary.EmotionEcgHandler;
import ca.ualberta.medroad.auxiliary.ForaBpGlucoseHandler;
import ca.ualberta.medroad.auxiliary.NoninOxometerHandler;
import ca.ualberta.medroad.view.fragment.PatientInfoFragment;
import ca.ualberta.medroad.view.fragment.PlaceholderFragment;
import ca.ualberta.medroad.view.list_adapters.MainMenuAdapter;


public class MainActivity
		extends Activity
		implements EmotionEcgHandler.EcgHandlerCallbacks, ForaBpGlucoseHandler.BpGlucoseHandlerCallbacks, NoninOxometerHandler.OxometerHandlerCallbacks
{
	public static final String               LOG_TAG                     = "MedROAD";
	public static final int                  GRAPH_HORIZONTAL_RESOLUTION = 100;
	public static final int                  REQUEST_ENABLE_BT           = 1;
	public static final String               ECG_BT_NAME                 = "AATOS-987";
	public static       int                  ECG_SIGNAL_RESOLUTION       = 0; // note that signal resolution is actually /1000
	public static       int                  ECG_HIGH_PASS_FILTER        = 0;
	public static       int                  ECG_SAMPLING_FREQUENCY      = 1;
	public static final String               BP_BT_NAME                  = "BP_BT_DEVICE_NAME";
	public static final String               O2_BT_NAME                  = "O2_BT_DEVICE_NAME";
	protected           ViewHolder           view                        = null;
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
	@SuppressWarnings( "UnusedDeclaration" )
	protected           MockDataGenerator    mockDataGenerator           = new MockDataGenerator();
	private             long                 graphCounter                = 0;

	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		initializeBtHandles();

		// Initialize the AppState
		AppState.getState( getApplicationContext() );

		fragmentManager = getFragmentManager();

		if ( view == null )
		{
			view = new ViewHolder( this );
		}

		view.init();

		onMainMenuSelect( 0 );
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		view.ecgStatus.setLoading();
		view.bpStatus.setLoading();
		view.o2Status.setLoading();

		getPairedBtDevices();

		//mockDataGenerator.start();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		checkBtStatus();
		connectBtDevices();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();

		//mockDataGenerator.stop();
		idleBtDevices();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		disconnectAndCleanupBtDevices();
	}

	@Override
	protected void onSaveInstanceState( @NonNull Bundle outState )
	{
		super.onSaveInstanceState( outState );
	}

	@Override
	protected void onRestoreInstanceState( @NonNull Bundle savedInstanceState )
	{
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
		case R.id.action_settings:
			return true;

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
		for ( BluetoothDevice device : devices )
		{
			if ( emotionEcg == null && device.getName().equals( ECG_BT_NAME ) )
			{
				rawEcgDevice = device;
				emotionEcg = new EmotionEcg( rawEcgDevice, new Handler( ecgHandler ) );
				continue;
			}

			if ( foraBpGlucose == null && device.getName().equals( BP_BT_NAME ) )
			{
				rawGlucoseBpDevice = device;
				foraBpGlucose = new ForaBpGlucose( rawGlucoseBpDevice,
												   new Handler( bpGlucoseHandler ) );
				continue;
			}

			if ( noninOximeter == null && device.getName().equals( O2_BT_NAME ) )
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
	}

	private void disconnectAndCleanupBtDevices()
	{
		if ( emotionEcg != null )
		{
			emotionEcg.disconnect();
			emotionEcg.cleanup();
		}

		if ( foraBpGlucose != null )
		{
			foraBpGlucose.disconnect();
			foraBpGlucose.cleanup();
		}

		if ( noninOximeter != null )
		{
			noninOximeter.disconnect();
			noninOximeter.cleanup();
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
		switch ( pos )
		{
		case -1:
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame, PlaceholderFragment.newInstance() )
						   .commit();

		case 0:
			// Patient info
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame,
									 PatientInfoFragment.newInstance( AppState.getState(
											 getApplicationContext() ).getCurrentPatient() ) )
						   .commit();

		default:
			// Do nothing!
		}
	}

	@Override
	public void onEcgBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onEcgBtConnected called" );

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
			Log.e( LOG_TAG, "Tried to start reading ECG data, but the object handle was null" );
			view.ecgStatus.setBad();
		}
	}

	@Override
	public void onEcgBtDisconnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onEcgBtDisconnected called" );
		view.ecgStatus.setBad();
	}

	@Override
	public void onEcgPacketReceive( EmotionEcg.EcgData data )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onEcgPacketReceive called" );
		Log.d( LOG_TAG,
			   "Peak: " + data.getPeak() + " Interval: " + data.getRrInterval() + " Samples[0]: " + data
					   .getSamples()[ 0 ] + " Packet: " + data.getPacketNumber() );
	}

	@Override
	public void onBpGlucoseBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onBpGlucoseBtConnected called" );
		if ( foraBpGlucose != null )
		{
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
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onBpGlucoseBtDisconnected called" );
		view.bpStatus.setBad();
	}

	@Override
	public void onBpGlucosePacketReceive( ForaBpGlucose.ForaData data )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onBpGlucosePacketReceive called" );
	}

	@Override
	public void onOxometerBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onOxometerBtConnected called" );
		if ( noninOximeter != null )
		{
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
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onOxometerBtDisconnected called" );
		view.o2Status.setBad();
	}

	@Override
	public void onOxometerPacketReceive( NoninOximeter.NoninData data )
	{
		Log.d( LOG_TAG, "ca.ualberta.medroad.view.MainActivity#onOxometerPacketReceive called" );
	}

	protected class ViewHolder
	{
		public    GraphView                    ecgGraph;
		public    TextView                     ecgText;
		public    DataStatusIndicator          ecgStatus;
		public    GraphView                    bpGraph;
		public    TextView                     sbpText;
		public    TextView                     dbpText;
		public    DataStatusIndicator          bpStatus;
		public    GraphView                    o2Graph;
		public    TextView                     o2Text;
		public    DataStatusIndicator          o2Status;
		public    ListView                     mainMenu;
		public    FrameLayout                  frame;
		protected LineGraphSeries< DataPoint > ecgSeries;
		protected LineGraphSeries< DataPoint > sbpSeries;
		protected LineGraphSeries< DataPoint > dbpSeries;
		protected LineGraphSeries< DataPoint > o2xSeries;

		public ViewHolder( MainActivity activity )
		{
			ecgGraph = (GraphView) activity.findViewById( R.id.main_ecg_graph );
			ecgText = (TextView) activity.findViewById( R.id.main_ecg_text );
			ecgStatus = new DataStatusIndicator( activity,
												 R.id.main_ecg_ic_good,
												 R.id.main_ecg_ic_bad,
												 R.id.main_ecg_progressBar );

			bpGraph = (GraphView) activity.findViewById( R.id.main_bp_graph );
			sbpText = (TextView) activity.findViewById( R.id.main_sbp_text );
			dbpText = (TextView) activity.findViewById( R.id.main_dbp_text );
			bpStatus = new DataStatusIndicator( activity,
												R.id.main_bp_ic_good,
												R.id.main_bp_ic_bad,
												R.id.main_bp_progressBar );

			o2Graph = (GraphView) activity.findViewById( R.id.main_o2_graph );
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
			sbpSeries = new LineGraphSeries<>();
			dbpSeries = new LineGraphSeries<>();
			o2xSeries = new LineGraphSeries<>();

			Resources res = getResources();

			ecgSeries.setColor( res.getColor( R.color.green_dark ) );
			sbpSeries.setColor( res.getColor( R.color.red_dark ) );
			dbpSeries.setColor( res.getColor( R.color.orange_dark ) );
			o2xSeries.setColor( res.getColor( R.color.blue_dark ) );

			formatGraph( ecgGraph );
			formatGraph( bpGraph );
			formatGraph( o2Graph );

			ecgGraph.addSeries( ecgSeries );
			bpGraph.addSeries( sbpSeries );
			bpGraph.addSeries( dbpSeries );
			o2Graph.addSeries( o2xSeries );
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
			mainMenu.setAdapter( MainMenuAdapter.newInstance( MainActivity.this ) );
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
														 100, /* Period */
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
				graphCounter++;

				final double ecg = 10 * Math.sin( System.currentTimeMillis() / Math.PI );
				final String pulse = String.valueOf( 80 + ( rng.nextInt( 3 ) - 1 ) );

				final int sbp = 120 + ( rng.nextInt( 3 ) - 1 );
				final String ssbp = String.valueOf( sbp );

				final int dbp = 80 + ( rng.nextInt( 3 ) - 1 );
				final String sdbp = String.valueOf( dbp );

				final int spo2 = 95 + ( rng.nextInt( 5 ) - 2 );
				final String sspo2 = String.valueOf( spo2 );

				/* UI updates must be run on the UI thread */
				runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						view.ecgText.setText( pulse );
						view.sbpText.setText( ssbp );
						view.dbpText.setText( sdbp );
						view.o2Text.setText( sspo2 );

						/* This bogs down the UI thread a lot. May need to find an alternative... */
						view.ecgSeries.appendData( new DataPoint( graphCounter, ecg ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
						view.sbpSeries.appendData( new DataPoint( graphCounter, sbp ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
						view.dbpSeries.appendData( new DataPoint( graphCounter, dbp ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
						view.o2xSeries.appendData( new DataPoint( graphCounter, spo2 ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
					}
				} );
			}
		}
	}
}
