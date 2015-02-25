package ca.ualberta.medroad.auxiliary;

import android.content.Context;

import ca.ualberta.medroad.model.Patient;
import ca.ualberta.medroad.model.mock.MockPatient;

/**
 * Created by Yuey on 2015-02-25.
 *
 * Singleton that represents persistent app state
 */
public class AppState
{
	private static AppState state          = null;
	private        Context  context        = null;
	private        Patient  currentPatient = new MockPatient(); // Example data

	public static AppState getState( Context ctx )
	{
		if ( state == null )
		{
			AppState.state = new AppState( ctx );
		}

		return AppState.state;
	}

	public AppState( Context ctx )
	{
		this.context = ctx;
	}

	public Patient getCurrentPatient()
	{
		return currentPatient;
	}
}
