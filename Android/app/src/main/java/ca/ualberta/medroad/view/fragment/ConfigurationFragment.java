package ca.ualberta.medroad.view.fragment;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.view.list_adapters.TwoLineArrayAdapter;

public class ConfigurationFragment
		extends Fragment
{
	protected List< Pair< String, String > > deviceNames  = new ArrayList<>();
	protected TwoLineArrayAdapter            arrayAdapter = null;
	protected ViewHolder                     view         = null;

	public static ConfigurationFragment newInstance()
	{
		ConfigurationFragment fragment = new ConfigurationFragment();

		Bundle args = new Bundle();
		fragment.setArguments( args );

		return fragment;
	}

	public ConfigurationFragment()
	{
		// Required empty public constructor
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

	protected class ViewHolder
	{
		public Spinner emotionEcgSpinner;
		public Spinner foraBpgSpinner;
		public Spinner noninO2xSpinner;

		public ViewHolder( View parentView )
		{
			emotionEcgSpinner = (Spinner) parentView.findViewById( R.id.f_config_ecg_device );
			foraBpgSpinner = (Spinner) parentView.findViewById( R.id.f_config_bp_device );
			noninO2xSpinner = (Spinner) parentView.findViewById( R.id.f_config_o2_device );
		}

		public void init()
		{
			// Resolve bluetooth devices we have available.
			BluetoothAdapter adapter = (BluetoothAdapter) getActivity().getSystemService( Context.BLUETOOTH_SERVICE );
			for ( BluetoothDevice device : adapter.getBondedDevices() )
			{
				deviceNames.add( new Pair<>( device.getName(), device.getAddress() ) );
			}

			arrayAdapter = new TwoLineArrayAdapter( getActivity(), deviceNames );

			emotionEcgSpinner.setAdapter( arrayAdapter );
			foraBpgSpinner.setAdapter( arrayAdapter );
			noninO2xSpinner.setAdapter( arrayAdapter );

			setupListeners();
		}

		private void setupListeners()
		{
			emotionEcgSpinner.setOnItemClickListener( new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
				{
					Pair<String, String> item = deviceNames.get( position );
					AppState.getState().setEcgDevice( item.first, item.second );
				}
			} );

			foraBpgSpinner.setOnItemClickListener( new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
				{
					Pair<String, String> item = deviceNames.get( position );
					AppState.getState().setBpgDevice( item.first, item.second );
				}
			} );

			noninO2xSpinner.setOnItemClickListener( new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
				{
					Pair<String, String> item = deviceNames.get( position );
					AppState.getState().setO2xDevice( item.first, item.second );
				}
			} );
		}
	}
}
