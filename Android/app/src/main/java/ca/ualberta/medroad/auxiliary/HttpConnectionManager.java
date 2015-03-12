package ca.ualberta.medroad.auxiliary;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ca.ualberta.medroad.model.raw_table_rows.DataRow;
import ca.ualberta.medroad.model.raw_table_rows.PatientRow;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-12.
 * <p/>
 * A wrapper for an HttpURLConnection. The class manages opening, closing and writing to the con.
 */
public class HttpConnectionManager
{
	public static final String            DEFAULT_SERVER_URL = "foo";
	protected           URL               serverURL          = null;
	protected           HttpURLConnection con                = null;
	protected           OutputStream      outputStream       = null;
	protected           boolean           good               = false;

	public HttpConnectionManager()
	{
		try
		{
			serverURL = new URL( DEFAULT_SERVER_URL );
		}
		catch ( MalformedURLException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "Server URL could not be created from the default URL: " + e.getMessage() + "\nDefault URL = " + DEFAULT_SERVER_URL );
		}
	}

	public HttpConnectionManager( String url )
			throws MalformedURLException
	{
		serverURL = new URL( url );
	}

	public void open()
	{
		try
		{
			con = (HttpURLConnection) serverURL.openConnection();

			con.setDoOutput( true );
			con.setChunkedStreamingMode( 0 );
			// con.setFixedLengthStreamingMode(  ); // Set if content-length is known.

			outputStream = new BufferedOutputStream( con.getOutputStream() );

			good = true;
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "Server connection could not be opened: " + e.getMessage() );
		}
	}

	public void writePatientRow(PatientRow row)
			throws IOException, IllegalStateException
	{
		if ( good )
		{
			// TODO
			outputStream.write( new byte[ 0 ] );
		}
		else
		{
			throw new IllegalStateException();
		}
	}

	public void writeDataRow(DataRow row)
	{

	}

	public boolean isGood()
	{
		return good;
	}

	public void close()
	{
		good = false;
		con.disconnect();
	}
}
