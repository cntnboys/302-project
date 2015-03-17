package ca.ualberta.medroad.view.list_adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yuey on 2015-03-17.
 */
public class TwoLineArrayAdapter
		extends ArrayAdapter< Pair< String, String > >
{
	protected List< Pair< String, String > > data;

	public TwoLineArrayAdapter( Context context, List< Pair< String, String > > objects )
	{
		super( context, android.R.layout.simple_list_item_2, android.R.id.text1, objects );
		this.data = objects;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;

		if ( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( android.R.layout.simple_list_item_2, parent, false );

			holder = new ViewHolder( convertView );
			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		Pair<String, String> item = data.get( position );
		holder.text1.setText( item.first );
		holder.text2.setText( item.second );

		return convertView;
	}

	@Override
	public View getDropDownView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder holder;

		if ( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( android.R.layout.simple_list_item_2, parent, false );

			holder = new ViewHolder( convertView );
			convertView.setTag( holder );
		}
		else
		{
			holder = (ViewHolder) convertView.getTag();
		}

		Pair<String, String> item = data.get( position );
		holder.text1.setText( item.first );
		holder.text2.setText( item.second );

		return convertView;
	}

	protected class ViewHolder
	{
		public TextView text1;
		public TextView text2;

		public ViewHolder( View parentView )
		{
			text1 = (TextView) parentView.findViewById( android.R.id.text1 );
			text2 = (TextView) parentView.findViewById( android.R.id.text2 );
		}
	}
}
