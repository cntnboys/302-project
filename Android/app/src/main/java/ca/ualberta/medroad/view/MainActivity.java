package ca.ualberta.medroad.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
	public static final int                  GRAPH_HORIZONTAL_RESOLUTION = 500;
	public static final int                  REQUEST_ENABLE_BT           = 1;
	public static final String               ECG_BT_NAME                 = "AATOS-987";
	public static final int                  ECG_PAIRING_CODE            = 3448;
	public static       int                  ECG_SIGNAL_RESOLUTION       = 0; // note that signal resolution is actually /1000
	public static       int                  ECG_HIGH_PASS_FILTER        = 0;
	public static       int                  ECG_SAMPLING_FREQUENCY      = 1;
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
	protected           MockDataGenerator    mockDataGenerator           = new MockDataGenerator();
	private             long                 graphCounter                = 0;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		initializeBtHandles();

		// Initialize the AppState
		AppState.getState( getApplicationContext() );

		fragmentManager = getFragmentManager();

		view = new ViewHolder( this );
		view.init();

		onMainMenuSelect( 0 );
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		mockDataGenerator.start();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		checkBtStatus();
		getPairedBtDevices();
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

		mockDataGenerator.stop();
		idleBtDevices();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		disconnectAndCleanupBtDevices();
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
		Set< BluetoothDevice > devices = mBluetoothAdapter.getBondedDevices();

		/*
		 * Iterate through paired BT devices and find the ones we need.
		 */
		for ( BluetoothDevice device : devices )
		{
			if ( device.getName().equals( ECG_BT_NAME ) )
			{
				rawEcgDevice = device;
				emotionEcg = new EmotionEcg( rawEcgDevice,
											 new Handler( ecgHandler ) );
			}
		}
	}

	private void connectBtDevices()
	{
		if ( emotionEcg != null )
		{
			emotionEcg.connect();
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
						   .replace( R.id.main_frame,
									 PlaceholderFragment.newInstance() )
						   .commit();

		case 0:
			// Patient info
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame,
									 PatientInfoFragment.newInstance( AppState.getState(
											 getApplicationContext() )
																			  .getCurrentPatient() ) )
						   .commit();

		default:
			// Do nothing!
		}
	}

	@Override
	public void onEcgBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onEcgBtConnected called" );

		if ( emotionEcg != null )
		{
			emotionEcg.setSamplingFrequency( ECG_SAMPLING_FREQUENCY );
			emotionEcg.setHighPassFilter( ECG_HIGH_PASS_FILTER );
			emotionEcg.setSignalResolution( ECG_SIGNAL_RESOLUTION );

			emotionEcg.setupAndStartRead();
		}
		else
		{
			Log.e( LOG_TAG,
				   "Tried to start reading ECG data, but the object handle was null" );
		}
	}

	@Override
	public void onEcgBtDisconnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onEcgBtDisconnected called" );
	}

	@Override
	public void onEcgPacketReceive( EmotionEcg.EcgData data )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onEcgPacketReceive called" );
		Log.d( LOG_TAG,
			   "Peak: " + data.getPeak() + " Interval: " + data.getRrInterval() + " Samples[0]: " + data
					   .getSamples()[ 0 ] + " Packet: " + data.getPacketNumber() );
	}

	@Override
	public void onBpGlucoseBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onBpGlucoseBtConnected called" );
	}

	@Override
	public void onBpGlucoseBtDisconnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onBpGlucoseBtDisconnected called" );
	}

	@Override
	public void onBpGlucosePacketReceive( ForaBpGlucose.ForaData data )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onBpGlucosePacketReceive called" );
	}

	@Override
	public void onOxometerBtConnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onOxometerBtConnected called" );
	}

	@Override
	public void onOxometerBtDisconnected( BluetoothDevice device )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onOxometerBtDisconnected called" );
	}

	@Override
	public void onOxometerPacketReceive( NoninOximeter.NoninData data )
	{
		Log.d( LOG_TAG,
			   "ca.ualberta.medroad.view.MainActivity#onOxometerPacketReceive called" );
	}

	protected class ViewHolder
	{
		public GraphView   ecgGraph;
		public TextView    ecgText;
		public TextView    bpText;
		public TextView    o2Text;
		public ListView    mainMenu;
		public FrameLayout frame;

		protected LineGraphSeries< DataPoint > ecgSeries;

		public ViewHolder( MainActivity activity )
		{
			ecgGraph = (GraphView) activity.findViewById( R.id.main_ecg_graph );
			ecgText = (TextView) activity.findViewById( R.id.main_ecg_text );
			bpText = (TextView) activity.findViewById( R.id.main_bp_text );
			o2Text = (TextView) activity.findViewById( R.id.main_o2_text );
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

			ecgGraph.getViewport().setXAxisBoundsManual( true );
			ecgGraph.getViewport().setMaxX( GRAPH_HORIZONTAL_RESOLUTION );
			ecgGraph.getViewport().setMinX( 0 );
			ecgGraph.getGridLabelRenderer().setHorizontalLabelsVisible( false );
			ecgGraph.getGridLabelRenderer().setVerticalLabelsVisible( false );

			ecgGraph.addSeries( ecgSeries );
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
	}

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
				final double ecg = 10 * Math.sin( System.currentTimeMillis() / Math.PI );
				final int pulse = 80 + ( rng.nextInt( 3 ) - 1 );
				final int sbp = 120 + ( rng.nextInt( 3 ) - 1 );
				int dbp = 80 + ( rng.nextInt( 3 ) - 1 );
				final int spo2 = 95 + ( rng.nextInt( 5 ) - 2 );

				/* UI updates must be run on the UI thread */
				runOnUiThread( new Runnable()
				{
					@Override
					public void run()
					{
						view.ecgSeries.appendData( new DataPoint( graphCounter++,
																  ecg ),
												   true,
												   GRAPH_HORIZONTAL_RESOLUTION );
						view.ecgText.setText( String.valueOf( pulse ) );
						view.bpText.setText( String.valueOf( sbp ) );
						view.o2Text.setText( String.valueOf( spo2 ) + "%" );
					}
				} );
			}
		}
	}
}
