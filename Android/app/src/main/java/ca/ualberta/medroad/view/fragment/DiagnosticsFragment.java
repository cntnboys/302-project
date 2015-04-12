package ca.ualberta.medroad.view.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ca.ammi.medlib.BtHealthDevice;
import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.handlers.BtHealthDeviceHandler;
import ca.ualberta.medroad.view.MainActivity;
import ca.ualberta.medroad.view.list_adapters.TwoLineArrayAdapter;

public class DiagnosticsFragment
		extends Fragment
		implements BtHealthDeviceHandler.BtHealthDeviceCallbacks
{
	protected ViewHolder                    viewHolder       = null;
	protected BluetoothManager              bluetoothManager = null;
	protected BluetoothAdapter              bluetoothAdapter = null;
	protected List< BtHealthDeviceHandler > deviceHandlers   = new ArrayList<>();
	protected DiagnosticsCallbacks          callbackTarget   = null;
	protected BtHealthDevice[]              healthDevices    = new BtHealthDevice[]{
			null, null, null, null
	};

	public DiagnosticsFragment()
	{
		// Required empty public constructor
	}

	public static DiagnosticsFragment newInstance( DiagnosticsCallbacks target )
	{
		DiagnosticsFragment fragment = new DiagnosticsFragment();

		Bundle args = new Bundle();
		fragment.setArguments( args );
		fragment.callbackTarget = target;

		return fragment;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );


	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
		initializeBtHandles( activity );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		cleanBtHandles();
	}

	private void cleanBtHandles()
	{
		for ( BtHealthDevice device : healthDevices )
		{
			if ( device != null )
			{
				device.cleanup();
			}
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_diagnostics, container, false );

		viewHolder = new ViewHolder( view );
		viewHolder.init();

		return view;
	}

	private void initializeBtHandles( Activity activity )
	{
		bluetoothManager = (BluetoothManager) activity.getSystemService( Context.BLUETOOTH_SERVICE );
		bluetoothAdapter = bluetoothManager.getAdapter();

		for ( int i = 0; i < ViewHolder.NUM_GENERIC_DEVICES; i++ )
		{
			deviceHandlers.add( new BtHealthDeviceHandler( this, i ) );
		}
	}

	@Override
	public void onHealthDeviceBtConnected( BluetoothDevice device, int index )
	{
		viewHolder.deviceRows.get( index ).progressBar.setVisibility( View.INVISIBLE );
	}

	@Override
	public void onHealthDeviceBtDisconnected( BluetoothDevice device, int index )
	{
		viewHolder.deviceRows.get( index ).progressBar.setVisibility( View.INVISIBLE );
		if ( healthDevices[ index ] != null )
		{
			healthDevices[ index ].cleanup();
			healthDevices[ index ] = null;
		}
	}

	@Override
	public void onHealthDeviceDataReceive( ByteBuffer data, int length, int index )
	{
		viewHolder.deviceRows.get( index ).progressBar.setVisibility( View.INVISIBLE );
		byte[] raw = new byte[ length ];
		data.get( raw );
		StringBuilder sb = new StringBuilder();
		for ( byte b : raw )
		{
			sb.append( b );
			sb.append( ' ' );
		}
		viewHolder.deviceRows.get( index ).text.setText( sb.toString() );
	}

	public interface DiagnosticsCallbacks
	{
		void onDiagnosticsRefreshEcg();
		void onDiagnosticsRefreshBp();
		void onDiagnosticsRefreshO2();
	}

	protected class ViewHolder
	{
		public static final int                            NUM_GENERIC_DEVICES = 4;
		public              Button                         refreshEcgButton    = null;
		public              Button                         refreshBpButton     = null;
		public              Button                         refreshO2Button     = null;
		public              List< DeviceRow >              deviceRows          = new ArrayList<>();
		public              List< Pair< String, String > > deviceNames         = new ArrayList<>();
		public              TwoLineArrayAdapter            arrayAdapter        = null;

		public ViewHolder( View root )
		{
			refreshEcgButton = (Button) root.findViewById( R.id.f_diagnostic_refresh_ecg );
			refreshBpButton = (Button) root.findViewById( R.id.f_diagnostic_refresh_bp );
			refreshO2Button = (Button) root.findViewById( R.id.f_diagnostic_refresh_o2 );

			for ( int i = 0; i < NUM_GENERIC_DEVICES; i++ )
			{
				deviceRows.add( new DeviceRow( root, i ) );
			}
		}

		public void init()
		{
			for ( BluetoothDevice device : bluetoothManager.getAdapter().getBondedDevices() )
			{
				deviceNames.add( new Pair<>( device.getName(), device.getAddress() ) );
			}

			arrayAdapter = new TwoLineArrayAdapter( getActivity(), deviceNames, true );

			for ( int i = 0; i < NUM_GENERIC_DEVICES; i++ )
			{
				deviceRows.get( i ).spinner.setAdapter( arrayAdapter );
			}

			setupListeners();
		}

		private void setupListeners()
		{
			refreshEcgButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onDiagnosticsRefreshEcg();
				}
			} );
			refreshBpButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onDiagnosticsRefreshBp();
				}
			} );
			refreshO2Button.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onDiagnosticsRefreshO2();
				}
			} );

			for ( int i = 0; i < NUM_GENERIC_DEVICES; i++ )
			{
				DeviceRow row = deviceRows.get( i );
				final int finalI = i;
				row.spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
				{
					@Override
					public void onItemSelected( AdapterView< ? > parent,
												View view,
												int position,
												long id )
					{
						initBtItem( deviceNames.get( position ), finalI );
					}

					@Override
					public void onNothingSelected( AdapterView< ? > parent )
					{
					}
				} );
				row.button.setOnClickListener( new View.OnClickListener()
				{
					@Override
					public void onClick( View v )
					{
						refreshBpItem( finalI );
					}
				} );
			}
		}

		private void initBtItem( Pair< String, String > info, int i )
		{
			Set< BluetoothDevice > bondedDevices = bluetoothAdapter.getBondedDevices();

			healthDevices[ i ] = null;

			for ( BluetoothDevice device : bondedDevices )
			{
				if ( device.getAddress().equals( info.second ) )
				{
					healthDevices[ i ] = new BtHealthDevice( device,
															 new Handler( deviceHandlers.get( i ) ) );
				}
			}

			if ( healthDevices[ i ] == null )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Unable to properly initialize a BT device: " + info.first );
			}
			else
			{
				healthDevices[ i ].connect();
			}
		}

		private void refreshBpItem( int i )
		{
			if ( healthDevices[ i ] != null )
			{
				healthDevices[ i ].getData();
				viewHolder.deviceRows.get( i ).progressBar.setVisibility( View.VISIBLE );
			}
		}

		public class DeviceRow
		{
			public Spinner     spinner     = null;
			public ImageButton button      = null;
			public TextView    text        = null;
			public ProgressBar progressBar = null;

			public DeviceRow( View root, int index )
			{
				// IDs are typed constants, which makes this dumb.
				int spinnerId, buttonId, textId, progressId;
				switch ( index )
				{
				case 0:
					spinnerId = R.id.f_diagnostic_device_1;
					buttonId = R.id.f_diagnostic_device_1_button;
					textId = R.id.f_diagnostic_device_1_text;
					progressId = R.id.f_diagnostic_device_1_progress;
					break;

				case 1:
					spinnerId = R.id.f_diagnostic_device_2;
					buttonId = R.id.f_diagnostic_device_2_button;
					textId = R.id.f_diagnostic_device_2_text;
					progressId = R.id.f_diagnostic_device_2_progress;
					break;

				case 2:
					spinnerId = R.id.f_diagnostic_device_3;
					buttonId = R.id.f_diagnostic_device_3_button;
					textId = R.id.f_diagnostic_device_3_text;
					progressId = R.id.f_diagnostic_device_3_progress;
					break;

				case 3:
					spinnerId = R.id.f_diagnostic_device_4;
					buttonId = R.id.f_diagnostic_device_4_button;
					textId = R.id.f_diagnostic_device_4_text;
					progressId = R.id.f_diagnostic_device_4_progress;
					break;

				default:
					spinnerId = 0;
					buttonId = 0;
					textId = 0;
					progressId = 0;
					break;
				}

				spinner = (Spinner) root.findViewById( spinnerId );
				button = (ImageButton) root.findViewById( buttonId );
				text = (TextView) root.findViewById( textId );
				progressBar = (ProgressBar) root.findViewById( progressId );
			}
		}
	}
}
