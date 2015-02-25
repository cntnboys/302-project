package ca.ualberta.medroad.view.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.model.Patient;


public class PatientInfoFragment
		extends Fragment
{
	private static final String ARG_DATA = "patient";

	private Patient data;

	public static PatientInfoFragment newInstance( Patient data )
	{
		PatientInfoFragment fragment = new PatientInfoFragment();

		Bundle args = new Bundle();
		args.putSerializable( ARG_DATA, data );
		fragment.setArguments( args );

		return fragment;
	}

	public PatientInfoFragment()
	{
		// Required empty public constructor
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		if ( getArguments() != null )
		{
			data = (Patient) getArguments().getSerializable( ARG_DATA );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.fragment_patient_info, container, false );
	}

	protected static class ViewHolder
	{

	}
}
