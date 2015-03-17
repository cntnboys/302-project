package ca.ualberta.medroad.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Class representing a patient.
 */
public class Patient
		implements Serializable
{
	protected int      id;
	protected String   ahcn;
	protected Calendar dob;
	protected String   doctor;
	protected String   name;

	public int getId()
	{
		return id;
	}

	public void setId( int id )
	{
		this.id = id;
	}

	public String getAhcn()
	{
		return ahcn;
	}

	public void setAhcn( String ahcn )
	{
		this.ahcn = ahcn;
	}

	public Calendar getDob()
	{
		return dob;
	}

	public void setDob( Calendar dob )
	{
		this.dob = dob;
	}

	public String getDoctor()
	{
		return doctor;
	}

	public void setDoctor( String doctor )
	{
		this.doctor = doctor;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}
}
