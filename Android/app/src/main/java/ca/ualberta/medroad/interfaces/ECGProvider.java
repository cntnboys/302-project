package ca.ualberta.medroad.interfaces;

/**
 * Created by Yuey on 2015-02-25.
 */
public interface ECGProvider
	extends RealTimeDataProvider
{
	public int getNextGraphPoint();
}
