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
	public static final String            DEFAULT_PATIENT_URL = "foo";
	public static final String            DEFAULT_DATA_URL    = "bar";
	protected           URL               patientURL          = null;
	protected           URL               dataURL             = null;
	protected           HttpURLConnection con                 = null;
	protected           OutputStream      dataOutputStream    = null;
	protected           boolean           good                = false;

	public HttpConnectionManager()
	{
		try
		{
			patientURL = new URL( DEFAULT_PATIENT_URL );
			dataURL = new URL( DEFAULT_DATA_URL );
		}
		catch ( MalformedURLException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "Server URL could not be created from the default URL: " + e.getMessage() + "\nDefault URL = " + DEFAULT_PATIENT_URL );
			Log.e( MainActivity.LOG_TAG,
				   "Server URL could not be created from the default URL: " + e.getMessage() + "\nDefault URL = " + DEFAULT_PATIENT_URL );
		}
	}

	public HttpConnectionManager( String patientUrl, String dataUrl )
			throws MalformedURLException
	{
		patientURL = new URL( patientUrl );
		dataURL = new URL( dataUrl );
	}

	public void openDataStream()
	{
		try
		{
			con = (HttpURLConnection) dataURL.openConnection();

			con.setDoOutput( true );
			con.setChunkedStreamingMode( 0 );
			// con.setFixedLengthStreamingMode(  ); // Set if content-length is known.

			dataOutputStream = new BufferedOutputStream( con.getOutputStream() );

			good = true;
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "Server connection could not be opened: " + e.getMessage() );
		}
	}

	public void writePatientRow( PatientRow row )
	{

	}

	public void writeDataRow( DataRow row )
			throws IOException, IllegalStateException
	{
		if ( good )
		{
			dataOutputStream.write( DataRow.directToByte( row ) );
		}
		else
		{
			throw new IllegalStateException(  );
		}
	}

	public boolean isGood()
	{
		return good;
	}

	public void closeDataStream()
	{
		good = false;
		con.disconnect();
	}
}
