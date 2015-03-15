package ca.ualberta.medroad.auxiliary;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

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
	protected static String              DEFAULT_PATIENT_URL = "https://project302.herokuapp.com/main/sendpatient/";
	protected static String              DEFAULT_DATA_URL    = "https://project302.herokuapp.com/main/senddata/";
	protected        ConManagerCallbacks callbackTarget      = null;
	protected        URL                 patientURL          = null;
	protected        URL                 dataURL             = null;
	protected        HttpURLConnection   con                 = null;
	protected        OutputStream        dataOutputStream    = null;
	protected        boolean             good                = false;
	private          String              csrfToken           = "";

	public HttpConnectionManager( ConManagerCallbacks callbackTarget )
	{
		this.callbackTarget = callbackTarget;

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

	public HttpConnectionManager( ConManagerCallbacks callbackTarget, String patientUrl, String dataUrl )
			throws MalformedURLException
	{
		this.callbackTarget = callbackTarget;

		DEFAULT_PATIENT_URL = patientUrl;
		DEFAULT_DATA_URL = dataUrl;

		patientURL = new URL( patientUrl );
		dataURL = new URL( dataUrl );
	}

	public void openDataStream()
	{
		OpenConnectionAsync task = new OpenConnectionAsync();
		task.execute();
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
			throw new IllegalStateException();
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

	public class OpenConnectionAsync
			extends AsyncTask< Void, Void, Void >
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			DefaultHttpClient client = new DefaultHttpClient();

			HttpGet getReq = new HttpGet( DEFAULT_DATA_URL );
			getReq.addHeader( "x-csrf-token", "fetch" );

			try
			{
				HttpResponse response = client.execute( getReq );
				Log.d( MainActivity.LOG_TAG, "Server responded with " + response.getStatusLine().getReasonPhrase() + ":");
				for ( Header header : response.getAllHeaders() )
				{
					Log.d( MainActivity.LOG_TAG, "   " + header.getName() + ":" + header.getValue() );
				}
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Get request failed: " + e.getMessage() );
			}

			for ( Cookie cookie : client.getCookieStore().getCookies() )
			{
				Log.d( MainActivity.LOG_TAG, "Cookie: " + cookie.getName() );

				if ( cookie.getName().equals( "csrftoken" ) )
				{
					Log.d( MainActivity.LOG_TAG, "Found CSRF token!" );
					csrfToken = cookie.getValue();
				}
			}

			try
			{
				con = (HttpURLConnection) dataURL.openConnection();

				con.setDoOutput( true );
				con.setChunkedStreamingMode( 0 );
				con.setRequestProperty( "csrftoken", csrfToken );
				// con.setFixedLengthStreamingMode(  ); // Set if content-length is known.

				dataOutputStream = new BufferedOutputStream( con.getOutputStream() );

				Log.d( MainActivity.LOG_TAG,
					   "Connection opened with response code: " + con.getResponseCode() + " - " + con
							   .getResponseMessage() );
				good = true;
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Server connection could not be opened: " + e.getMessage() );
			}
			return null;
		}

		@Override
		protected void onPostExecute( Void aVoid )
		{
			callbackTarget.onDataStreamConnected();
		}
	}

	public interface ConManagerCallbacks
	{
		public void onDataStreamConnected();
	}
}
