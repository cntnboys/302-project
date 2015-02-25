package ca.ualberta.medroad.interfaces;

/**
 * Created by Yuey on 2015-02-25.
 */
public interface RealTimeDataProvider
{
	public int getPeriod_ms();

	public int getCurrentSample();
}
