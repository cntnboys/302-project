package ca.ualberta.medroad.auxiliary;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
	protected static final String              HOST                = "project302.herokuapp.com";
	protected static final String              DATA_PATH           = "/main/senddata/";
	protected static final String              PATIENT_PATH        = "/main/sendpatient/";
	protected static       String              DEFAULT_PATIENT_URL = "https://project302.herokuapp.com/main/sendpatient/";
	protected static       String              DEFAULT_DATA_URL    = "https://project302.herokuapp.com/main/senddata/";
	protected              ConManagerCallbacks callbackTarget      = null;
	protected              URL                 hostURL             = null;
	protected              URL                 patientURL          = null;
	protected              URL                 dataURL             = null;
	protected              HttpsURLConnection  dataConnection      = null;
	protected              HttpsURLConnection  patientConnection   = null;
	protected              OutputStream        dataOut             = null;
	protected              OutputStream        patientOut          = null;

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

	public void closeDataStream()
	{
		CloseConnectionAsync task = new CloseConnectionAsync();
		task.execute();
	}

	public void writePatientRow( PatientRow row )
			throws IOException
	{
		patientOut.write( PatientRow.directToByte( row ) );
		Log.d( MainActivity.LOG_TAG,
			   "Server responded with " + patientConnection.getResponseCode() + " - " + patientConnection
					   .getResponseMessage() );
	}

	public void writeDataRow( DataRow row )
			throws IOException
	{
		dataOut.write( DataRow.directToByte( row ) );
		Log.d( MainActivity.LOG_TAG,
			   "Server responded with " + patientConnection.getResponseCode() + " - " + patientConnection
					   .getResponseMessage() );
	}

	public class OpenConnectionAsync
			extends AsyncTask< Void, Void, Void >
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			try
			{
				dataConnection = (HttpsURLConnection) dataURL.openConnection();

				formatConnectionRequest( dataConnection );

				dataOut = new BufferedOutputStream( dataConnection.getOutputStream() );
				Log.d( MainActivity.LOG_TAG,
					   "A connection was made to the server with response " + dataConnection
							   .getResponseCode() + " - " + dataConnection.getResponseMessage() );
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Unable to open the HTTPS connection for data: " + e.getMessage() );
			}

			try
			{
				patientConnection = (HttpsURLConnection) patientURL.openConnection();

				formatConnectionRequest( patientConnection );

				patientOut = new BufferedOutputStream( patientConnection.getOutputStream() );
				Log.d( MainActivity.LOG_TAG,
					   "A connection was made to the server with response " + patientConnection
							   .getResponseCode() + " - " + patientConnection.getResponseMessage() );
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Unable to open the HTTPS connection for patients: " + e.getMessage() );
			}

			return null;
		}

		@Override
		protected void onPostExecute( Void aVoid )
		{
			callbackTarget.onDataStreamConnected();
		}
	}

	private void formatConnectionRequest( HttpsURLConnection dataConnection )
	{
		dataConnection.setDoOutput( true );
		dataConnection.setChunkedStreamingMode( 0 );
		dataConnection.setRequestProperty( "Accept", "*/*" );
		dataConnection.setRequestProperty( "Content-Type", "application/json" );

		Log.d( MainActivity.LOG_TAG, "Connection formatted with headers: " );
		for ( String prop : dataConnection.getRequestProperties().keySet() )
		{
			Log.d( MainActivity.LOG_TAG,
				   prop + ":" + dataConnection.getRequestProperties()
											  .get( prop ) );
		}
	}

	public class CloseConnectionAsync
			extends AsyncTask< Void, Void, Void >
	{
		@Override
		protected Void doInBackground( Void... params )
		{
			dataConnection.disconnect();
			patientConnection.disconnect();

			try
			{
				dataOut.close();
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Failed to close the data output stream: " + e.getMessage() );
			}

			try
			{
				patientOut.close();
			}
			catch ( IOException e )
			{
				Log.e( MainActivity.LOG_TAG,
					   "Failed to close the patient output stream: " + e.getMessage() );
			}

			return null;
		}
	}

	public interface ConManagerCallbacks
	{
		public void onDataStreamConnected();
	}
}
