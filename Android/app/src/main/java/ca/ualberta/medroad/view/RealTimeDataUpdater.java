package ca.ualberta.medroad.view;

import com.jjoe64.graphview.series.DataPoint;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ca.ualberta.medroad.interfaces.ECGProvider;
import ca.ualberta.medroad.interfaces.RealTimeDataProvider;

/**
 * Created by Yuey on 2015-02-25.
 */
public class RealTimeDataUpdater
{
	public static final int NUM_WORKERS = 3;

	protected ECGProvider                 ecgProvider;
	protected ScheduledFuture             ecgsf;
	protected Queue< DataPoint >          ecgGraphData;
	protected RealTimeDataProvider        o2Provider;
	protected ScheduledFuture             o2sf;
	protected RealTimeDataProvider        bpProvider;
	protected ScheduledFuture             bpsf;
	protected ScheduledThreadPoolExecutor workerPool;
	protected MainActivity                activity;

	public RealTimeDataUpdater( MainActivity activity,
								ECGProvider ecg,
								RealTimeDataProvider bp,
								RealTimeDataProvider o2 )
	{
		this.activity = activity;

		this.ecgProvider = ecg;
		this.ecgGraphData = new LinkedList<>(  );
		this.o2Provider = o2;
		this.bpProvider = bp;

		workerPool = new ScheduledThreadPoolExecutor( NUM_WORKERS );

		for ( int i = 0; i < 100; i++ )
		{
			ecgGraphData.add( new DataPoint( i - 100, 0 ) );
		}
	}

	public void start()
	{
		if ( ecgsf == null )
		{
			ecgsf = workerPool.scheduleAtFixedRate( new ECGUpdater(),
													0,
													ecgProvider.getPeriod_ms(),
													TimeUnit.MILLISECONDS );
		}

		if ( bpsf == null )
		{
			bpsf = workerPool.scheduleAtFixedRate( new BPUpdater(),
												   0,
												   o2Provider.getPeriod_ms(),
												   TimeUnit.MILLISECONDS );
		}

		if ( o2sf == null )
		{
			o2sf = workerPool.scheduleAtFixedRate( new O2Updater(),
												   0,
												   bpProvider.getPeriod_ms(),
												   TimeUnit.MILLISECONDS );
		}
	}

	public void stop()
	{
		if ( ecgsf != null )
		{
			ecgsf.cancel( true );
			ecgsf = null;
		}

		if ( bpsf != null )
		{
			bpsf.cancel( true );
			bpsf = null;
		}

		if ( o2sf != null )
		{
			o2sf.cancel( true );
			o2sf = null;
		}
	}

	private class ECGUpdater
			implements Runnable
	{
		long graphCounter = 0;

		@Override
		public void run()
		{
			final int sample = ecgProvider.getCurrentSample();
			final int graphPoint = ecgProvider.getNextGraphPoint();

			activity.runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					activity.view.ecgText.setText( String.valueOf( sample ) );
					ecgGraphData.add( new DataPoint( graphCounter++, graphPoint ) );
					ecgGraphData.remove();
					activity.view.ecgSeries.resetData( ecgGraphData.toArray(new DataPoint[ecgGraphData.size()]) );
				}
			} );
		}
	}

	private class O2Updater
			implements Runnable
	{
		long graphCounter = 0;

		@Override
		public void run()
		{
			final int sample = o2Provider.getCurrentSample();

			activity.runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					activity.view.o2Text.setText( String.valueOf( sample ) );
				}
			} );
		}
	}

	private class BPUpdater
			implements Runnable
	{
		long graphCounter = 0;

		@Override
		public void run()
		{
			final int sample = bpProvider.getCurrentSample();

			activity.runOnUiThread( new Runnable()
			{
				@Override
				public void run()
				{
					activity.view.bpText.setText( String.valueOf( sample ) );
				}
			} );
		}
	}
}
