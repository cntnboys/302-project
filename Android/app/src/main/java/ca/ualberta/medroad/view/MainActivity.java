package ca.ualberta.medroad.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import ca.ualberta.medroad.R;
import ca.ualberta.medroad.auxiliary.AppState;
import ca.ualberta.medroad.view.fragment.PatientInfoFragment;
import ca.ualberta.medroad.view.fragment.PlaceholderFragment;
import ca.ualberta.medroad.view.list_adapters.MainMenuAdapter;


public class MainActivity
		extends Activity
{
	protected ViewHolder      view;
	protected FragmentManager fragmentManager;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );

		// Initialize the AppState
		AppState.getState( getApplicationContext() );

		fragmentManager = getFragmentManager();

		view = new ViewHolder( this );

		setupMenu();

		onMainMenuSelect( 0 );
	}

	private void setupMenu()
	{
		view.mainMenu.setAdapter( MainMenuAdapter.newInstance( this ) );
		view.mainMenu.setOnItemClickListener( new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
			{
				MainActivity.this.onMainMenuSelect( position );
			}
		} );
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

	private void onMainMenuSelect( int pos )
	{
		switch ( pos )
		{
		case -1:
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame, PlaceholderFragment.newInstance() )
						   .commit();

		case 0:
			// Patient info
			fragmentManager.beginTransaction()
						   .replace( R.id.main_frame,
									 PatientInfoFragment.newInstance( AppState.getState(
											 getApplicationContext() ).getCurrentPatient() ) )
						   .commit();

		default:
			// Do nothing!
		}
	}

	protected static class ViewHolder
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
