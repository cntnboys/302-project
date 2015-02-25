package ca.ualberta.medroad.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Class representing a note or alert that should be obvious to the user, for example an allergy or
 * other medical alert.
 */
public class PatientNote
		implements Serializable, Comparable< PatientNote >
{
	protected String title;
	protected String description;
	protected int    severity; // 1 is highest

	public PatientNote(String title, String description, int severity)
	{
		this.title = title;
		this.description = description;
		this.severity = severity;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public int getSeverity()
	{
		return severity;
	}

	@Override
	public int compareTo( @NonNull PatientNote another )
	{
		return this.severity - another.severity;
	}
}
