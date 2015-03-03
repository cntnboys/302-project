package ca.ualberta.medroad.view.continuous_graph;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

/**
 * Created by Yuey on 2015-02-25.
 */
public class ContinuousDataPoint
	implements DataPointInterface
{
	protected static long counter = 0;
	protected double value;
	protected long xPos;

	public ContinuousDataPoint(double value)
	{
		this.value = value;
		this.xPos = ++counter;
	}

	@Override
	public double getX()
	{
		return xPos;
	}

	@Override
	public double getY()
	{
		return value;
	}
}
