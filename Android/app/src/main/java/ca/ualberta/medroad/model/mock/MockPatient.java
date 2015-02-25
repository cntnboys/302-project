package ca.ualberta.medroad.model.mock;

import java.util.Calendar;

import ca.ualberta.medroad.model.Patient;
import ca.ualberta.medroad.model.PatientHistoryItem;
import ca.ualberta.medroad.model.PatientNote;

/**
 * Created by Yuey on 2015-02-25.
 */
public class MockPatient
		extends Patient
{
	public MockPatient()
	{
		super();

		name = "John Doe";
		ahcn = 123456789;

		Calendar birthday = Calendar.getInstance();
		birthday.add( Calendar.YEAR, -18 );

		dob = birthday;
		gender = Gender.Male;

		for ( int i = 0; i < 5; i++ )
		{
			historyItems.add( new PatientHistoryItem( "Test " + i,
													  Calendar.getInstance().getTime(),
													  "This is an example!" ) );
		}

		notes.add( new PatientNote( "Medical Alert", "Patient is allergic to penicillin.", 1 ) );
	}
}
