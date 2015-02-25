package ca.ualberta.medroad.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.ualberta.medroad.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaceholderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaceholderFragment
		extends Fragment
{
	public static PlaceholderFragment newInstance( )
	{
		PlaceholderFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		fragment.setArguments( args );
		return fragment;
	}

	public PlaceholderFragment()
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
		return inflater.inflate( R.layout.placeholder, container, false );
	}


}
