package ca.ualberta.medroad.auxiliary.mock;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import ca.ammi.medlib.EmotionEcg;
import ca.ualberta.medroad.auxiliary.EmotionEcgHandler;
import ca.ualberta.medroad.auxiliary.ForaBpGlucoseHandler;
import ca.ualberta.medroad.auxiliary.NoninOxometerHandler;

/**
 * Created by Yuey on 02/03/2015.
 */
public class MockDataSource
{
	public static final int NUM_WORKERS = 1;

	protected ScheduledThreadPoolExecutor                    threadPool   = null;
	protected EmotionEcgHandler.EcgHandlerCallbacks          ecgCallbacks = null;
	protected ForaBpGlucoseHandler.BpGlucoseHandlerCallbacks bpgCallbacks = null;
	protected NoninOxometerHandler.OxometerHandlerCallbacks  o2xCallbacks = null;

	public MockDataSource()
	{
		threadPool = new ScheduledThreadPoolExecutor( NUM_WORKERS );
	}

	public void start()
	{

	}

	public void stop()
	{

	}

	private class ECGDataGenerator
			implements Runnable
	{
		protected Handler.Callback callback;

		public ECGDataGenerator( Handler.Callback callback )
		{
			this.callback = callback;
		}

		@Override
		public void run()
		{

		}
	}
}
