package ca.ualberta.medroad.view.list_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.model.PatientHistoryItem;

/**
 * Created by Yuey on 2015-02-25.
 * <p/>
 * Adapter class for a patient history.
 */
public class PatientHistoryAdapter
		extends ArrayAdapter< PatientHistoryItem >
{
	protected List< PatientHistoryItem > data;

	public PatientHistoryAdapter( Context context, List< PatientHistoryItem > objects )
	{
		super( context,
			   R.layout.list_item_patient_info,
			   R.id.list_item_patient_info_title,
			   objects );
		this.data = objects;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		ViewHolder viewHolder;

		if ( convertView == null )
		{
			LayoutInflater inflater = (LayoutInflater) super.getContext()
															.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			convertView = inflater.inflate( R.layout.list_item_patient_info, parent, false );

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
		public static final SimpleDateFormat sdf         = new SimpleDateFormat( "",
																				 Locale.getDefault() );
		public              TextView         title       = null;
		public              TextView         date        = null;
		public              TextView         description = null;

		public ViewHolder( View v )
		{
			title = (TextView) v.findViewById( R.id.list_item_patient_info_title );
			date = (TextView) v.findViewById( R.id.list_item_patient_info_date );
			description = (TextView) v.findViewById( R.id.list_item_patient_info_summary );
		}

		public void init( PatientHistoryItem data )
		{
			title.setText( data.getTitle() );
			date.setText( sdf.format( data.getDate() ) );
			description.setText( data.getSummary() );
		}
	}
}
