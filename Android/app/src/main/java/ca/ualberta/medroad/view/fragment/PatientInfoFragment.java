package ca.ualberta.medroad.view.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.model.Patient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PatientInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PatientInfoFragment
		extends Fragment
{
	protected ViewHolder        view           = null;
	protected FragmentCallbacks callbackTarget = null;

	public static final SimpleDateFormat DOB_FORMAT = new SimpleDateFormat( "LLLL d, yyyy",
																			Locale.getDefault() );

	public static PatientInfoFragment newInstance( FragmentCallbacks callbackTarget )
	{
		PatientInfoFragment fragment = new PatientInfoFragment();

		Bundle args = new Bundle();
		fragment.setArguments( args );
		fragment.callbackTarget = callbackTarget;

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
	}

	@Override
	public View onCreateView( LayoutInflater inflater,
							  ViewGroup container,
							  Bundle savedInstanceState )
	{
		// Inflate the layout for this fragment
		View v = inflater.inflate( R.layout.fragment_patient_info, container, false );
		view = new ViewHolder( v );
		view.init();

		return v;
	}

	protected class ViewHolder
	{
		public EditText nameEntry;
		public EditText ahcnEntry;
		public EditText dobEntry;
		public TextView ageText;
		public EditText physicianEntry;
		public TextView pidText;
		public TextView sidText;
		public Button   newPatientButton;

		public ViewHolder( View parentView )
		{
			nameEntry = (EditText) parentView.findViewById( R.id.f_patient_info_name_entry );
			ahcnEntry = (EditText) parentView.findViewById( R.id.f_patient_info_ahcn_entry );
			dobEntry = (EditText) parentView.findViewById( R.id.f_patient_info_dob_entry );
			ageText = (TextView) parentView.findViewById( R.id.f_patient_info_age );
			physicianEntry = (EditText) parentView.findViewById( R.id.f_patient_info_physician_entry );
			pidText = (TextView) parentView.findViewById( R.id.f_patient_info_id );
			sidText = (TextView) parentView.findViewById( R.id.f_patient_info_sid );
			newPatientButton = (Button) parentView.findViewById( R.id.f_patient_info_new_patient_button );
		}

		public void init()
		{
			setEntryValues( AppState.getState().getCurrentPatient() );
			setupListeners();
		}

		public void setEntryValues( Patient p )
		{
			nameEntry.setText( p.getName() );
			ahcnEntry.setText( p.getAhcn() );
			if ( p.getDob() == null )
			{
				ageText.setText( "Age unknown." );
			}
			else
			{
				dobEntry.setText( DOB_FORMAT.format( p.getDob().getTime() ) );
				ageText.setText( getAge( p.getDob() ) );
			}
			physicianEntry.setText( p.getDoctor() );
			pidText.setText( String.valueOf( p.getId() ) );
			sidText.setText( AppState.getState().getCurrentSession().getId() );
		}

		protected void setupListeners()
		{
			nameEntry.addTextChangedListener( new TextWatcher()
			{
				@Override
				public void beforeTextChanged( CharSequence s, int start, int count, int after )
				{

				}

				@Override
				public void onTextChanged( CharSequence s, int start, int before, int count )
				{

				}

				@Override
				public void afterTextChanged( Editable s )
				{
					AppState.getState().getCurrentPatient().setName( s.toString() );
				}
			} );

			ahcnEntry.addTextChangedListener( new TextWatcher()
			{
				@Override
				public void beforeTextChanged( CharSequence s, int start, int count, int after )
				{

				}

				@Override
				public void onTextChanged( CharSequence s, int start, int before, int count )
				{

				}

				@Override
				public void afterTextChanged( Editable s )
				{
					AppState.getState().getCurrentPatient().setAhcn( s.toString() );
				}
			} );

			ahcnEntry.setOnEditorActionListener( new TextView.OnEditorActionListener()
			{
				@Override
				public boolean onEditorAction( TextView v, int actionId, KeyEvent event )
				{
					switch ( actionId )
					{
					case EditorInfo.IME_ACTION_NEXT:
						dobEntry.callOnClick();
						return true;

					default:
						return false;
					}
				}
			} );

			dobEntry.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					DatePickerFragment.newInstance( AppState.getState()
															.getCurrentPatient()
															.getDob(), PatientInfoFragment.this )
									  .show( getChildFragmentManager(), "DatePicker" );
				}
			} );

			physicianEntry.addTextChangedListener( new TextWatcher()
			{
				@Override
				public void beforeTextChanged( CharSequence s, int start, int count, int after )
				{

				}

				@Override
				public void onTextChanged( CharSequence s, int start, int before, int count )
				{

				}

				@Override
				public void afterTextChanged( Editable s )
				{
					AppState.getState().getCurrentPatient().setDoctor( s.toString() );
				}
			} );

			newPatientButton.setOnClickListener( new View.OnClickListener()
			{
				@Override
				public void onClick( View v )
				{
					AppState.getState().newPatient();
					callbackTarget.triggerPatientInfoFragmentReload();
				}
			} );
		}
	}

	public static class DatePickerFragment
			extends DialogFragment
			implements DatePickerDialog.OnDateSetListener
	{
		protected Calendar            startDate;
		protected PatientInfoFragment parentFragment;

		public static DatePickerFragment newInstance( Calendar c, PatientInfoFragment infoFragment )
		{
			DatePickerFragment result = new DatePickerFragment();

			result.startDate = c;
			result.parentFragment = infoFragment;

			return result;
		}

		@Override
		public Dialog onCreateDialog( Bundle savedInstanceState )
		{
			return new DatePickerDialog( getActivity(),
										 this,
										 startDate.get( Calendar.YEAR ),
										 startDate.get( Calendar.MONTH ),
										 startDate.get( Calendar.DAY_OF_MONTH ) );
		}

		@Override
		public void onDateSet( DatePicker view, int year, int monthOfYear, int dayOfMonth )
		{
			Patient p = AppState.getState().getCurrentPatient();

			p.getDob().set( Calendar.YEAR, year );
			p.getDob().set( Calendar.MONTH, monthOfYear );
			p.getDob().set( Calendar.DAY_OF_MONTH, dayOfMonth );

			parentFragment.view.dobEntry.setText( PatientInfoFragment.DOB_FORMAT.format( p.getDob()
																						  .getTime() ) );
			parentFragment.view.ageText.setText( getAge( p.getDob() ) );
			parentFragment.view.physicianEntry.requestFocus();
		}
	}

	private static String getAge( Calendar dob )
	{
		Calendar now = Calendar.getInstance();
		int diff = now.get( Calendar.YEAR ) - dob.get( Calendar.YEAR ) - 1;

		if ( now.get( Calendar.MONTH ) > dob.get( Calendar.MONTH ) || ( now.get( Calendar.MONTH ) == dob
				.get( Calendar.MONTH ) && now.get( Calendar.DATE ) >= dob.get( Calendar.DATE ) ) )
		{
			++diff;
		}

		return String.valueOf( diff ) + " years old.";
	}

	public interface FragmentCallbacks
	{
		public void triggerPatientInfoFragmentReload();
	}
}
