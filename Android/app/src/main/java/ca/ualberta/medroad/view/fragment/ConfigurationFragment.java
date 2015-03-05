package ca.ualberta.medroad.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.ualberta.medroad.R;

public class ConfigurationFragment
		extends Fragment
{
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
		// Inflate the layout for this fragment
		return inflater.inflate( R.layout.fragment_configuration, container, false );
	}


}
