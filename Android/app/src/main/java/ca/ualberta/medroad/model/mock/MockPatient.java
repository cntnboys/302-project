package ca.ualberta.medroad.model.mock;

import java.util.Calendar;

import ca.ualberta.medroad.model.Patient;

/**
 * Created by Yuey on 2015-03-16.
 */
public class MockPatient
	extends Patient
{
	public MockPatient()
	{
		Calendar c = Calendar.getInstance();

		this.id = (int) generateNewId();
		this.ahcn = "n/a";
		this.dob = c;
		this.doctor = "n/a";
		this.name = "John Doe";
	}

	private long generateNewId()
	{
		return Calendar.getInstance().getTimeInMillis();
	}
}
