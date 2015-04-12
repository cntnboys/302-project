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
 *
 * @see ca.ualberta.medroad.view.MainActivity#onMainMenuSelect(int)
 */
public class MainMenuAdapter
		extends ArrayAdapter< MainMenuAdapter.MenuItem >
{
	public static final int ID_PATIENT_INFO = 1;
	public static final int ID_DIAGNOSTICS  = 2;
	public static final int ID_ALARMS       = 3;
	public static final int ID_LOGIN        = 4;
	public static final int ID_CONFIG       = 100;

	protected List< MenuItem > data;

	private MainMenuAdapter( Context context, List< MenuItem > data )
	{
		super( context, R.layout.list_item_main, R.id.list_item_main_title, data );
		this.data = data;
	}

	public static MainMenuAdapter newInstance( Context ctx )
	{
		List< MenuItem > nData = new ArrayList<>();

		nData.add( new MenuItem( ID_PATIENT_INFO,
								 "Patient Info",
								 "View patient information.",
								 R.drawable.ic_info_dark ) );

		nData.add( new MenuItem( ID_DIAGNOSTICS,
								 "Diagnostics",
								 "Control bluetooth devices.",
								 R.drawable.ic_search_dark ) );

		nData.add( new MenuItem( ID_ALARMS,
								 "Alarms",
								 "Configure alarms",
								 R.drawable.ic_volume_dark ) );

		nData.add( new MenuItem( ID_CONFIG,
								 "Configure",
								 "Set up bluetooth devices",
								 R.drawable.ic_bluetooth_dark ) );

		return new MainMenuAdapter( ctx, nData );
	}

	@Override
	public long getItemId( int position )
	{
		return data.get( position ).ID;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder;

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
		public final long   ID;
		public       String title;
		public       String description;
		public       int    icoResId;

		public MenuItem( long ID, String title, String desc, int ico )
		{
			this.ID = ID;
			this.title = title;
			this.description = desc;
			this.icoResId = ico;
		}
	}
}
