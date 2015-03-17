package ca.ualberta.medroad.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Yuey on 2015-03-12.
 * <p/>
 * Class representing a user session.
 */
public class Session
{
	public static final SimpleDateFormat ID_GEN = new SimpleDateFormat( "yyyyMMddHHmmss",
																		Locale.getDefault() );
	protected String id;

	public Session()
	{
		id = ID_GEN.format( Calendar.getInstance().getTime() );
	}

	public String getId()
	{
		return id;
	}
}
