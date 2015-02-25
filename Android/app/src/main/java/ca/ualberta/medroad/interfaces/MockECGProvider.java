package ca.ualberta.medroad.interfaces;

/**
 * Created by Yuey on 2015-02-25.
 */
public class MockECGProvider
	implements ECGProvider
{

	@Override
	public int getNextGraphPoint()
	{
		return (int) ((Math.sin( System.currentTimeMillis() )) * 10.0);
	}

	@Override
	public int getPeriod_ms()
	{
		return 100;
	}

	@Override
	public int getCurrentSample()
	{
		return 80;
	}
}
