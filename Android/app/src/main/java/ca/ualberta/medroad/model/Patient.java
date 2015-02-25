package ca.ualberta.medroad.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yuey on 2015-02-25.
 *
 * Class representing a patient.
 */
public class Patient
		implements Serializable
{
	public enum Gender
	{
		Male,
		Female,
		Other
	}

	protected String                     name;
	protected int                        ahcn;
	protected Date                       dob;
	protected Gender                     gender;
	protected List< PatientNote >        notes;
	protected List< PatientHistoryItem > historyItems;

	public Patient()
	{
		notes = new ArrayList<>();
		historyItems = new ArrayList<>();
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public int getAhcn()
	{
		return ahcn;
	}

	public void setAhcn( int ahcn )
	{
		this.ahcn = ahcn;
	}

	public Date getDob()
	{
		return dob;
	}

	public void setDob( Date dob )
	{
		this.dob = dob;
	}

	public Gender getGender()
	{
		return gender;
	}

	public void setGender( Gender gender )
	{
		this.gender = gender;
	}

	public List< PatientNote > getNotes()
	{
		return notes;
	}

	public List< PatientHistoryItem > getHistoryItems()
	{
		return historyItems;
	}

	/**
	 * Checks if the patient has any notes.
	 * @return 0 if the patient has no notes else the value of the highest severity note.
	 */
	public int hasNotes()
	{
		if ( notes.isEmpty() ) return 0;

		int result = 100;
		for (PatientNote note : notes)
		{
			if ( note.severity < result ) result = note.severity;
		}

		return result;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public static String genderToString( Gender g )
	{
		switch ( g )
		{
		case Male:
			return "Male";

		case Female:
			return "Female";

		case Other:
		default:
			return "Other";
		}
	}

	public static Gender stringToGender( String s )
	{
		switch ( s.toLowerCase() )
		{
		case "male":
		case "m":
			return Gender.Male;

		case "female":
		case  "f":
			return Gender.Female;

		default:
			return Gender.Other;
		}
	}
}
