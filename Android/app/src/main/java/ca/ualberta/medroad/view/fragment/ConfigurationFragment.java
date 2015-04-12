package ca.ualberta.medroad.view.fragment;


import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.view.list_adapters.TwoLineArrayAdapter;

public class ConfigurationFragment
		extends Fragment
{
	protected List< Pair< String, String > > deviceNames    = new ArrayList<>();
	protected ConfigurationCallbacks         callbackTarget = null;
	protected TwoLineArrayAdapter            arrayAdapter   = null;
	protected ViewHolder                     view           = null;

	public ConfigurationFragment()
	{
		// Required empty public constructor
	}

	public static ConfigurationFragment newInstance( ConfigurationCallbacks callbackTarget )
	{
		ConfigurationFragment fragment = new ConfigurationFragment();

		Bundle args = new Bundle();
		fragment.setArguments( args );
		fragment.callbackTarget = callbackTarget;

		return fragment;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState )
	{
		View v = inflater.inflate( R.layout.fragment_configuration, container, false );

		view = new ViewHolder( v );
		view.init();

		return v;
	}

	public interface ConfigurationCallbacks
	{
		public void onEcgDeviceChange();

		public void onBpDeviceChange();

		public void onO2DeviceChange();
	}

	protected class ViewHolder
	{
		public Spinner     emotionEcgSpinner;
		public ImageButton emotionEcgButton;
		public Spinner     foraBpgSpinner;
		public ImageButton foraBpgButton;
		public Spinner     noninO2xSpinner;
		public ImageButton noninO2xButton;
		private boolean ecgInit = true;
		private boolean bpgInit = true;
		private boolean o2xInit = true;

		public ViewHolder( View parentView )
		{
			emotionEcgSpinner = (Spinner) parentView.findViewById( R.id.f_config_ecg_device );
			emotionEcgButton = (ImageButton) parentView.findViewById( R.id.f_config_ecg_button );
			foraBpgSpinner = (Spinner) parentView.findViewById( R.id.f_config_bp_device );
			foraBpgButton = (ImageButton) parentView.findViewById( R.id.f_config_bp_button );
			noninO2xSpinner = (Spinner) parentView.findViewById( R.id.f_config_o2_device );
			noninO2xButton = (ImageButton) parentView.findViewById( R.id.f_config_o2_button );
		}

		public void init()
		{
			// Resolve bluetooth devices we have available.
			BluetoothManager manager = (BluetoothManager) getActivity().getSystemService( Context.BLUETOOTH_SERVICE );
			for ( BluetoothDevice device : manager.getAdapter().getBondedDevices() )
			{
				deviceNames.add( new Pair<>( device.getName(), device.getAddress() ) );
			}

			arrayAdapter = new TwoLineArrayAdapter( getActivity(), deviceNames, false );

			emotionEcgSpinner.setAdapter( arrayAdapter );
			foraBpgSpinner.setAdapter( arrayAdapter );
			noninO2xSpinner.setAdapter( arrayAdapter );

			setCurrentSelection();

			setupListeners();
		}

		private void setCurrentSelection()
		{
			AppState state = AppState.getState();

			for ( Pair< String, String > item : deviceNames )
			{
				if ( item.equals( state.getEcgDevice() ) )
				{
					emotionEcgSpinner.setSelection( deviceNames.indexOf( item ) );
					continue;
				}

				if ( item.equals( state.getBpgDevice() ) )
				{
					foraBpgSpinner.setSelection( deviceNames.indexOf( item ) );
					continue;
				}

				if ( item.equals( state.getO2xDevice() ) )
				{
					noninO2xSpinner.setSelection( deviceNames.indexOf( item ) );
				}
			}
		}

		private void setupListeners()
		{
			emotionEcgSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected( AdapterView< ? > parent,
											View view,
											int position,
											long id )
				{
					Pair< String, String > item = deviceNames.get( position );
					AppState.getState().setEcgDevice( item.first, item.second );
					if ( ecgInit )
					{
						ecgInit = false;
						return;
					}
					callbackTarget.onEcgDeviceChange();
				}

				@Override
				public void onNothingSelected( AdapterView< ? > parent )
				{
					// Do nothing!
				}
			} );

			emotionEcgButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onEcgDeviceChange();
				}
			} );

			foraBpgSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected( AdapterView< ? > parent,
											View view,
											int position,
											long id )
				{
					Pair< String, String > item = deviceNames.get( position );
					AppState.getState().setBpgDevice( item.first, item.second );
					if ( bpgInit )
					{
						bpgInit = false;
						return;
					}
					callbackTarget.onBpDeviceChange();
				}

				@Override
				public void onNothingSelected( AdapterView< ? > parent )
				{
					// Do nothing!
				}
			} );

			foraBpgButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onBpDeviceChange();
				}
			} );

			noninO2xSpinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected( AdapterView< ? > parent,
											View view,
											int position,
											long id )
				{
					Pair< String, String > item = deviceNames.get( position );
					AppState.getState().setO2xDevice( item.first, item.second );
					if ( o2xInit )
					{
						o2xInit = false;
						return;
					}
					callbackTarget.onO2DeviceChange();
				}

				@Override
				public void onNothingSelected( AdapterView< ? > parent )
				{
					// Do nothing!
				}
			} );

			noninO2xButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					callbackTarget.onO2DeviceChange();
				}
			} );
		}
	}
}
