package ca.ualberta.medroad.auxiliary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import java.io.Serializable;

import ca.ualberta.medroad.model.Patient;
import ca.ualberta.medroad.model.Session;
import ca.ualberta.medroad.model.mock.MockPatient;
import ca.ualberta.medroad.view.MainActivity;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Singleton that represents persistent app state
 */
public class AppState
		implements Serializable
{
	protected static    AppState    state          = null;
	protected static    Context     context        = null;
	protected transient FileManager fileManager    = null;
	protected           Patient     currentPatient = null;
	protected transient Session     currentSession = null;
	protected           String      emotionEcgName = "AATOS-987";
	protected           String      emotionEcgAddr = "00:07:80:6D:5A:FE";
	protected           String      foraBpgName    = "TaiDoc-BTM";
	protected           String      foraBpgAddr    = "00:12:3E:00:03:17";
	protected           String      noninO2xName   = "Nonin_Medical_Inc._802706";
	protected           String      noninO2xAddr   = "00:1C:05:00:EA:DA";

	public static void initState( @NonNull Context context )
	{
		if ( AppState.context == null )
		{
			AppState.context = context;
		}

		if ( AppState.state == null )
		{
			FileManager tempManager = new FileManager( context );
			AppState tempState = tempManager.loadAppState();
			if ( tempState == null )
			{
				Log.w( MainActivity.LOG_TAG,
					   " [FILE] > The file manager returned null when loading app state" );
				state = new AppState();
			}

			state.fileManager = tempManager;
			if ( state.currentPatient == null )
			{
				state.currentPatient = new MockPatient();
			}
		}
	}

	@NonNull
	public static AppState getState()
	{
		if ( context == null || state == null )
		{
			throw new IllegalStateException( "App state not initialized." );
		}

		return AppState.state;
	}

	private AppState()
	{

	}

	public Patient getCurrentPatient()
	{
		return currentPatient;
	}

	public Pair< String, String > getEcgDevice()
	{
		return new Pair<>( emotionEcgName, emotionEcgAddr );
	}

	public Pair< String, String > getBpgDevice()
	{
		return new Pair<>( foraBpgName, foraBpgAddr );
	}

	public Pair< String, String > getO2xDevice()
	{
		return new Pair<>( noninO2xName, noninO2xAddr );
	}

	public void setEcgDevice( String name, String addr )
	{
		this.emotionEcgName = name;
		this.emotionEcgAddr = addr;
	}

	public void setBpgDevice( String name, String addr )
	{
		this.foraBpgName = name;
		this.foraBpgAddr = addr;
	}

	public void setO2xDevice( String name, String addr )
	{
		this.noninO2xName = name;
		this.noninO2xAddr = addr;
	}

	public FileManager getFileManager()
	{
		return fileManager;
	}

	public Session getCurrentSession()
	{
		return currentSession;
	}

	public void setCurrentSession( Session session )
	{
		this.currentSession = session;
	}

	public void saveState()
	{
		fileManager.saveAppState( state );
	}
}
