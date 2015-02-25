package ca.ualberta.medroad.view.continuous_graph;

import android.graphics.Canvas;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Yuey on 2015-02-25.
 */
public class ContinuousDataSeries
		implements Series< ContinuousDataPoint >
{
	protected final int size;
	protected Queue< ContinuousDataPoint > data;
	protected double minY;
	protected double maxY;

	public ContinuousDataSeries(int size)
	{
		this.size = size;
		data = new LinkedList<>(  );
	}

	@Override
	public double getLowestValueX()
	{
		return 0;
	}

	@Override
	public double getHighestValueX()
	{
		return size;
	}

	@Override
	public double getLowestValueY()
	{
		return minY;
	}

	@Override
	public double getHighestValueY()
	{
		return maxY;
	}

	@Override
	public Iterator< ContinuousDataPoint > getValues( double v, double v2 )
	{
		return null;
	}

	@Override
	public void draw( GraphView graphView, Canvas canvas, boolean b )
	{

	}

	@Override
	public String getTitle()
	{
		return null;
	}

	@Override
	public int getColor()
	{
		return 0;
	}

	@Override
	public void setOnDataPointTapListener( OnDataPointTapListener onDataPointTapListener )
	{

	}

	@Override
	public void onTap( float v, float v2 )
	{

	}

	@Override
	public void onGraphViewAttached( GraphView graphView )
	{

	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
