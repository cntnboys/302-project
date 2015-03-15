package ca.ualberta.medroad.auxiliary;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ca.ualberta.medroad.model.raw_table_rows.DataRow;
import ca.ualberta.medroad.model.raw_table_rows.PatientRow;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 15/03/2015.
 * <p/>
 * A hack to work with the server since apparently Heroku hates Android.
 */
public class HttpRequestManager
{
	protected static String PATIENT_URL = "https://project302.herokuapp.com/main/sendpatient/";
	protected static String DATA_URL    = "https://project302.herokuapp.com/main/senddata/";

	public static void sendPatient( PatientRow patientRow )
	{
		PostPatientAsync task = new PostPatientAsync();
		task.execute( patientRow );
	}

	public static void sendData( DataRow dataRow )
	{
		PostDataAsync task = new PostDataAsync();
		task.execute( dataRow );
	}

	protected static class PostPatientAsync
			extends AsyncTask< PatientRow, Void, Void >
	{

		@Override
		protected Void doInBackground( PatientRow... params )
		{
			String entity = PatientRow.getJSON( params[ 0 ] );
			HttpResponse response = composeAndExecutePost( PATIENT_URL,
														   entity );

			if ( response != null )
			{
				Log.d( MainActivity.LOG_TAG,
					   "Post request executed with response " + response
							   .getStatusLine()
							   .getStatusCode() + " - " + response.getStatusLine()
																  .getReasonPhrase() );
			}

			return null;
		}
	}

	protected static class PostDataAsync
			extends AsyncTask< DataRow, Void, Void >
	{
		@Override
		protected Void doInBackground( DataRow... params )
		{
			String entity = DataRow.getJSON( params[ 0 ] );
			HttpResponse response = composeAndExecutePost( DATA_URL, entity );

			if ( response != null )
			{
				Log.d( MainActivity.LOG_TAG,
					   "Post request executed for patient with response " + response
							   .getStatusLine()
							   .getStatusCode() + " - " + response.getStatusLine()
																  .getReasonPhrase() );
			}

			return null;
		}
	}


	protected static HttpResponse composeAndExecutePost( String url, String entity )
	{
		HttpClient client = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost( url );
		postRequest.addHeader( "Accept", "*/*" );
		postRequest.addHeader( "Content-Type", "application/json" );
		HttpResponse response = null;

		Log.v( MainActivity.LOG_TAG, " > Composed an HTTP request to " + url );
		Log.v( MainActivity.LOG_TAG, " > JSON payload: " + entity );
		Log.v( MainActivity.LOG_TAG, " > Headers: " );
		for ( Header header : postRequest.getAllHeaders() )
		{
			Log.v( MainActivity.LOG_TAG,
				  " >     " + header.getName() + ":" + header.getValue() );
		}

		try
		{
			postRequest.setEntity( new StringEntity( entity ) );
		}
		catch ( UnsupportedEncodingException e )
		{
			e.printStackTrace();
		}

		try
		{
			response = client.execute( postRequest );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "Post request failed: " + e.getMessage() );
		}

		return response;
	}
}
