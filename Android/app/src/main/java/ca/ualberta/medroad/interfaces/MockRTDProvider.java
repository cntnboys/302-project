package ca.ualberta.medroad.interfaces;

/**
 * Created by Yuey on 2015-02-25.
 */
public class MockRTDProvider
	implements RealTimeDataProvider
{
	@Override
	public int getPeriod_ms()
	{
		return 100;
	}

	@Override
	public int getCurrentSample()
	{
		return 100;
	}
}
