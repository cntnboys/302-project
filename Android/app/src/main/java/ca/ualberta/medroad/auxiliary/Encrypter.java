package ca.ualberta.medroad.auxiliary;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-03-12.
 * <p/>
 * This class is used to encrypt outgoing JSON payloads. The current
 * implementation simply returns the same string.
 */
public class Encrypter
{
	public static final String STRING_ENCODING = "UTF-8";

	public static byte[] encryptToByteArray( String s )
	{
		Log.d( MainActivity.LOG_TAG, "Encrypting: " + s );

		byte[] rawBytes;

		try
		{
			rawBytes = s.getBytes( STRING_ENCODING );
		}
		catch ( UnsupportedEncodingException e )
		{
			Log.e( MainActivity.LOG_TAG,
				   "The Encrypter couldn't use string encoding " + STRING_ENCODING + ": " + e
						   .getMessage() );
			rawBytes = s.getBytes();
		}

		// Do encryption of the string and return it as an array of bytes.

		return rawBytes;
	}

	public static String encryptToString( String s )
	{
		Log.d( MainActivity.LOG_TAG, "Encrypting \"" + s + "\"" );

		// Do encryption of the string and return it as a string.

		return s;
	}
}
