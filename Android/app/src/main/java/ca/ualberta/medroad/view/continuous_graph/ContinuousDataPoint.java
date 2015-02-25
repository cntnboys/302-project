package ca.ualberta.medroad.view.continuous_graph;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

/**
 * Created by Yuey on 2015-02-25.
 */
public class ContinuousDataPoint
	implements DataPointInterface
{
	protected double value;

	public ContinuousDataPoint(double value)
	{
		this.value = value;
	}

	@Override
	public double getX()
	{
		return 0;
	}

	@Override
	public double getY()
	{
		return value;
	}
}
