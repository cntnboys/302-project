package ca.ualberta.medroad.auxiliary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;

import java.io.Serializable;

import ca.ualberta.medroad.model.Patient;
import ca.ualberta.medroad.model.mock.MockPatient;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Singleton that represents persistent app state
 */
public class AppState
		implements Serializable
{
	protected static AppState state = null;

	protected transient Context context        = null;
	protected           Patient currentPatient = new MockPatient(); // Example data
	protected           String  emotionEcgName = "AATOS-987";
	protected           String  emotionEcgAddr = "00:07:80:6D:5A:FE";
	protected           String  foraBpgName    = "TaiDoc-BTM";
	protected           String  foraBpgAddr    = "00:12:3E:00:03:17";
	protected           String  noninO2xName   = "Nonin_Medical_Inc._802706";
	protected           String  noninO2xAddr   = "00:1C:05:00:EA:DA";

	public static void initState( @NonNull Context context )
	{
		AppState.state = new AppState( context );
	}

	@NonNull
	public static AppState getState()
	{
		if ( state == null )
		{
			throw new IllegalStateException( "App state not initialized." );
		}

		return AppState.state;
	}

	public AppState( Context ctx )
	{
		this.context = ctx;
		this.currentPatient = new MockPatient();
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
		this.foraBpgName = name;
		this.foraBpgAddr = addr;
	}
}
