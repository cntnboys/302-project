package ca.ualberta.medroad.auxiliary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.Locale;

import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-17.
 */
public class FileManager
{
	public final String               APP_STATE_FILENAME    = "state.ser";
	public final String               LOG_FILE_EXT          = ".log";
	public final SimpleDateFormat     TIMESTAMP_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss    ",
			Locale.getDefault() );
	private      File                 docsRootExtDir        = null;
	private      File                 docsRootIntDir        = null;
	protected    BufferedOutputStream sessionLog            = null;
	protected    boolean              logOpen               = false;

	public FileManager( Context context )
	{
		String state = Environment.getExternalStorageState();

		if ( !Environment.MEDIA_MOUNTED.equals( state ) )
		{
			// TODO
			// External media is not available
		}
		else
		{
			docsRootExtDir = new File( Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOCUMENTS ), "MedROAD" );

			if ( !docsRootExtDir.mkdirs() )
			{
				Log.w( MainActivity.LOG_TAG, " [FILE] > The directory to store log files was not created" );
			}
		}

		docsRootIntDir = context.getFilesDir();
	}

	public void openSessionLog()
	{
		if ( sessionLog != null )
		{
			closeSessionLog();
		}

		Log.v( MainActivity.LOG_TAG,
			   " [FILE] > Opening the session log output stream for " + AppState
					   .getState()
					   .getCurrentSession()
					   .getId() );
		sessionLog = openBufferedOutStream( AppState.getState()
													.getCurrentSession()
													.getId() );
		logOpen = true;

		writeLogOpenMessage();
	}

	private void writeLogOpenMessage()
	{
		writeToSessionLog( "Session log opened." );
		writeToSessionLog( "Session ID: " + AppState.getState()
													.getCurrentSession()
													.getId() );
		writeToSessionLog( "Patient ID: " + AppState.getState()
													.getCurrentPatient()
													.getId() );
	}

	public void writeToSessionLog( String msg )
	{
		try
		{
			Date now = Calendar.getInstance().getTime();
			sessionLog.write( Encrypter.encryptToByteArray(
					TIMESTAMP_DATE_FORMAT.format( now ) + msg + "\n\r" ) );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to write a buffer to the session log: " + e
						   .getMessage() );
		}
	}

	public void closeSessionLog()
	{
		writeLogCloseMessage();

		try
		{
			Log.v( MainActivity.LOG_TAG,
				   " [FILE] > Closing the session log output stream for " + AppState
						   .getState()
						   .getCurrentSession()
						   .getId() );
			sessionLog.flush();
			sessionLog.close();
			sessionLog = null;
			logOpen = false;
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to close the session log output stream: " + e
						   .getMessage() );
		}
	}

	private void writeLogCloseMessage()
	{
		writeToSessionLog( "Session log closed." );
	}

	public void saveAppState( AppState saveMe )
	{
		File outFile = new File( docsRootIntDir, APP_STATE_FILENAME );

		if ( outFile.exists() )
		{
			if ( !outFile.delete() )
			{
				Log.e( MainActivity.LOG_TAG,
					   " [FILE] > Failed to clear an existing app state" );
			}
		}
		try
		{
			if ( !outFile.createNewFile() )
			{
				throw new IOException();
			}
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to create a new file to store app state: " + e
						   .getMessage() );
		}

		OutputStream os = null;
		ObjectOutput oo = null;

		try
		{
			os = new BufferedOutputStream( new FileOutputStream( outFile ) );
			oo = new ObjectOutputStream( os );
		}
		catch ( FileNotFoundException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to create a BufferedOutputStream to save AppState: " + e
						   .getMessage() );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to create an ObjectOutputStream to save AppState: " + e
						   .getMessage() );
		}

		if ( oo == null )
		{
			return;
		}

		try
		{
			oo.writeObject( saveMe );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to write the app state to storage: " + e.getMessage() );
		}

		try
		{
			oo.close();
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Unable to close ObjectOutput: " + e.getMessage() );
		}
		try
		{
			os.close();
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Unable to close OutputStream: " + e.getMessage() );
		}
	}

	public AppState loadAppState()
	{
		AppState result = null;

		File inFile = new File( docsRootIntDir, APP_STATE_FILENAME );
		InputStream is = null;
		ObjectInput oi = null;

		try
		{
			is = new BufferedInputStream( new FileInputStream( inFile ) );
			oi = new ObjectInputStream( is );
		}
		catch ( FileNotFoundException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to open a BufferedInputStream to load app state: " + e
						   .getMessage() );
		}
		catch ( StreamCorruptedException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to open an ObjectInputStream to load app state (StreamCorruptedException): " + e
						   .getMessage() );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to open an ObjectInputStream to load app state (IOException): " + e
						   .getMessage() );
		}

		if ( oi == null )
		{
			return null;
		}

		try
		{
			result = (AppState) oi.readObject();
		}
		catch ( ClassNotFoundException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to load app state (ClassNotFoundException): " + e
						   .getMessage() );
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to load app state (IOException): " + e.getMessage() );
		}

		try
		{
			oi.close();
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Unable to close ObjectInput: " + e.getMessage() );
		}
		try
		{
			is.close();
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Unable to close InputStream: " + e.getMessage() );
		}

		return result;
	}

	public boolean isLogOpen()
	{
		return logOpen;
	}

	protected BufferedOutputStream openBufferedOutStream( String filename )
	{
		File outFile = new File( docsRootExtDir, filename + LOG_FILE_EXT );
		try
		{
			if ( !outFile.createNewFile() )
			{
				throw new IOException();
			}
			else
			{
				Log.v( MainActivity.LOG_TAG,
					   " [FILE] > Created " + outFile.getAbsolutePath() + " " + outFile
							   .exists() );
			}
		}
		catch ( IOException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to create the file " + outFile.getAbsolutePath() + ": " + e
						   .getMessage() );
		}

		try
		{
			return new BufferedOutputStream( new FileOutputStream( outFile ) );
		}
		catch ( FileNotFoundException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   " [FILE] > Failed to open a FileOutputStream: " + e.getMessage() );
			return null;
		}
	}

}
