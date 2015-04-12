package ca.ualberta.medroad.model;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-12.
 * <p/>
 * Class representing a user session.
 */
public class Session
{
	public static final SimpleDateFormat ID_GEN = new SimpleDateFormat( "yyyyMMddHHmmss",
																		Locale.getDefault() );
	public static final long MAX_INT =  2147483547;
	public static final long MIN_INT = -2147483547;

	protected String id;

	public Session()
	{
		id = truncate( ID_GEN.format( Calendar.getInstance().getTime() ) );
	}

	/* Needed so that the server can accept the session ID. There's a limit on the max size of the ID (as an integer) */
	private String truncate( String s )
	{
		long i = Long.valueOf( s );
		i %= MAX_INT * 2;
		i -= MAX_INT;

		if ( i > MAX_INT || i < MIN_INT )
		{
			Log.wtf( MainActivity.LOG_TAG, "ID generation exceeded ID limits." );
		}

		return String.valueOf( i );
	}

	public String getId()
	{
		return id;
	}
}
