package ca.ualberta.medroad.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Yuey on 2015-02-25.
 *
 * Class representing an item in a patient medical history.
 */
public class PatientHistoryItem
		implements Serializable
{
	protected String title;
	protected Date   date;
	protected String summary;

	public PatientHistoryItem(String title, Date date, String summary)
	{
		this.title = title;
		this.date = date;
		this.summary = summary;
	}

	public String getTitle()
	{
		return title;
	}

	public Date getDate()
	{
		return date;
	}

	public String getSummary()
	{
		return summary;
	}
}
