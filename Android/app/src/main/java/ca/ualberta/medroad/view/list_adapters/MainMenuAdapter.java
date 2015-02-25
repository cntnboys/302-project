package ca.ualberta.medroad.view.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.ualberta.medroad.R;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Adapter class to display the main menu list view. This is poor software engineering at its
 * finest.
 */
public class MainMenuAdapter
		extends ArrayAdapter< MainMenuAdapter.MenuItem >
{
	protected List< MenuItem > data;

	public static MainMenuAdapter newInstance( Context ctx )
	{
		List< MenuItem > nData = new ArrayList<>();

		nData.add( new MenuItem( "Patient Info",
								 "View patient information.",
								 R.drawable.ic_placeholder_dark ) );

		nData.add( new MenuItem( "Diagnostics",
								 "Take diagnostic measurements, photos, and notes.",
								 R.drawable.ic_placeholder_dark ) );

		nData.add( new MenuItem( "Account", "", R.drawable.ic_placeholder_dark ) );

		return new MainMenuAdapter( ctx, nData );
	}

	private MainMenuAdapter( Context context, List< MenuItem > data )
	{
		super( context, R.layout.list_item_main, R.id.list_item_main_title, data );
		this.data = data;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder = null;

		if ( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext()
															.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( R.layout.list_item_main, parent, false );

			viewHolder = new ViewHolder( convertView );
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.init( data.get( position ) );
		return convertView;
	}

	protected static class ViewHolder
	{
		public TextView  title    = null;
		public TextView  subtitle = null;
		public ImageView ico      = null;
		public ImageView alert    = null;

		public ViewHolder( View v )
		{
			title = (TextView) v.findViewById( R.id.list_item_main_title );
			subtitle = (TextView) v.findViewById( R.id.list_item_main_subtitle );
			ico = (ImageView) v.findViewById( R.id.list_item_main_ico );
			alert = (ImageView) v.findViewById( R.id.list_item_main_alert );
		}

		public void init( MenuItem item )
		{
			title.setText( item.title );
			subtitle.setText( item.description );
			ico.setImageResource( item.icoResId );
		}
	}

	public static class MenuItem
	{
		public String title;
		public String description;
		public int    icoResId;

		public MenuItem( String title, String desc, int ico )
		{
			this.title = title;
			this.description = desc;
			this.icoResId = ico;
		}
	}
}
