package ca.ualberta.medroad.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import ca.ualberta.medroad.R;


public class MainActivity
		extends Activity
{
	protected ViewHolder view;
	protected FragmentManager fragmentManager;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		fragmentManager = getFragmentManager();

		view = new ViewHolder( this );

		onMainMenuSelect( -1 );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
		case R.id.action_settings:
			return true;

		default:
			return super.onOptionsItemSelected( item );
		}
	}

	private void onMainMenuSelect(int pos)
	{
		switch ( pos )
		{
		case -1:
			fragmentManager.beginTransaction().replace( R.id.main_frame, PlaceholderFragment.newInstance() ).commit();

		default:
			// Do nothing!
		}
	}

	public static class ViewHolder
	{
		public GraphView   ecgGraph;
		public TextView    ecgText;
		public TextView    bpText;
		public TextView    o2Text;
		public ListView    mainMenu;
		public FrameLayout frame;

		public ViewHolder( MainActivity activity )
		{
			ecgGraph = (GraphView) activity.findViewById( R.id.main_ecg_graph );
			ecgText = (TextView) activity.findViewById( R.id.main_ecg_text );
			bpText = (TextView) activity.findViewById( R.id.main_bp_text );
			o2Text = (TextView) activity.findViewById( R.id.main_o2_text );
			mainMenu = (ListView) activity.findViewById( R.id.main_list_view );
			frame = (FrameLayout) activity.findViewById( R.id.main_frame );
		}
	}
}
