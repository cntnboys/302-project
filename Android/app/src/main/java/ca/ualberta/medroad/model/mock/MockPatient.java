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
		c.set( Calendar.YEAR, 1993 );
		c.set( Calendar.MONTH, 3 );
		c.set( Calendar.DAY_OF_MONTH, 2 );

		this.id = 1;
		this.ahcn = "123456789";
		this.dob = c;
		this.doctor = "Dr. Foo";
		this.name = "John Doe";
	}
}
