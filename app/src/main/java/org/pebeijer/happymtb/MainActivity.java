package org.pebeijer.happymtb;

import java.util.List;

import org.pebeijer.happymtb.item.KoSData;
import org.pebeijer.happymtb.item.ThreadData;
import org.pebeijer.happymtb.item.KoSItem;
import org.pebeijer.happymtb.item.Thread;
import org.pebeijer.happymtb.fragment.ArticlesListFragment;
import org.pebeijer.happymtb.fragment.CalendarListFragment;
import org.pebeijer.happymtb.fragment.HomesListFragment;
import org.pebeijer.happymtb.fragment.KoSListFragment;
import org.pebeijer.happymtb.fragment.SettingsFragment;
import org.pebeijer.happymtb.fragment.ShopsListFragment;
import org.pebeijer.happymtb.fragment.ThreadListFragment;
import org.pebeijer.happymtb.fragment.VideoListFragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {

	protected ListFragment mFrag;
	public static String mActiveKoSObjectLink = "";
	public static KoSData mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "ASC", "Stigande", 0);	
	public static ThreadData mThreadData = new ThreadData(1, 1, null, 0, false);	
	Fragment mFragment = new HomesListFragment();	
	int mFrameId = R.id.homeframe;
	int mFrameLayout = R.layout.homeframe;
	private ActionBar mActionBar;	
	ArrayAdapter<String> mActionbaradapter;
	private SharedPreferences mPreferences;	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private Toast mBackToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);				
		
		// Set up the action bar to show a dropdown list.
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		
		// Set up the dropdown list navigation in the action bar.
		
		mActionbaradapter = new ArrayAdapter<String>(mActionBar.getThemedContext(),
				android.R.layout.simple_list_item_1,
				android.R.id.text1, new String[] {
						getString(R.string.title_bar_home),
						getString(R.string.title_bar_forum),
						getString(R.string.title_bar_articles),								
						getString(R.string.title_bar_kos),
						getString(R.string.title_bar_video),
						getString(R.string.title_bar_shops),
						getString(R.string.title_bar_calendar),
						getString(R.string.title_bar_settings),});
		
		mActionBar.setListNavigationCallbacks(mActionbaradapter, this);
		// Specify a SpinnerAdapter to populate the dropdown list.

		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mActionBar.setSelectedNavigationItem(mPreferences.getInt("startpage", 0));
	}	

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		switchContent(position);     			
		return true;
	}

    public void switchContent(int position) {     	
    	
//    	int title = R.string.title_bar_home;
    	
		switch (position + 1) {
		case 1:
			mFragment = new HomesListFragment();
			mFrameId = R.id.homeframe;
			mFrameLayout = R.layout.homeframe;			
//			title = R.string.title_bar_home;
			break;
		case 2:
			mFragment = new ThreadListFragment();
			mFrameId = R.id.threadframe;
			mFrameLayout = R.layout.threadframe;
//			title = R.string.title_bar_forum;
		    break;
		case 3:
			mFragment = new ArticlesListFragment();
			mFrameId = R.id.content_frame; 
			mFrameLayout = R.layout.content_frame; 
//			title = R.string.title_bar_articles;
		    break;
		case 4:
			mFragment = new KoSListFragment();
			mFrameId = R.id.kosframe;
			mFrameLayout = R.layout.kosframe;
//			title = R.string.title_bar_kos;
		    break;
		case 5:
			mFragment = new VideoListFragment();
			mFrameId = R.id.videoframe; 
			mFrameLayout = R.layout.videoframe; 
//			title = R.string.title_bar_video;
		    break;
		case 6:
			mFragment = new ShopsListFragment();
			mFrameId = R.id.content_frame; 
			mFrameLayout = R.layout.content_frame; 
//			title = R.string.title_bar_shops;
		    break;
		case 7:
			mFragment = new CalendarListFragment();
			mFrameId = R.id.calendarframe; 
			mFrameLayout = R.layout.calendarframe; 
//			title = R.string.title_bar_shops;
		    break;		    
		case 8:
			mFragment = new SettingsFragment();
			mFrameId = R.id.settingsframe;
			mFrameLayout = R.layout.settingsframe;
//			title = R.string.title_bar_settings;
		    break;
	    }		
		setContentView(mFrameLayout);
		getSupportFragmentManager()
        .beginTransaction()
        .replace(mFrameId, mFragment)
        .addToBackStack(null)
        .commit();  	
    }
	
    public void Back() {
        if(mBackToast != null && mBackToast.getView().getWindowToken() != null) {
            finish();
        } else {
        	mBackToast = Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT);
        	mBackToast.show();
        }    	
    }
    
    @Override
    public void onBackPressed() {
    	if (mActionBar.getSelectedNavigationIndex() == mPreferences.getInt("startpage", 0)) {    		
    		Back();
    	} else {
    		mActionBar.setSelectedNavigationItem(mPreferences.getInt("startpage", 0));
    	}
    }    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		onBackPressed();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	    
    
	public void SetActiveObjectLink(String ActiveKoSObjectLink) {
		mActiveKoSObjectLink = ActiveKoSObjectLink;
	}
	
	public String GetActiveObjectLink() {		 
		return mActiveKoSObjectLink;
	}	

	public void SetKoSData(KoSData KoSData) {		
		mKoSData = KoSData;
	}	

	public void SetKoSDataItems(List<KoSItem> KoSItems) {
		mKoSData.setKoSItems(KoSItems);
	}		
	
	public KoSData GetKoSData() {
		return mKoSData;
	}	
	
	public void SetThreadData(ThreadData ThreadData) {		
		mThreadData = ThreadData;
	}

	public void SetThreadLogined(Boolean Logined) {		
		mThreadData.setLogined(Logined);
	}

	public boolean GetThreadLogined() {		
		return mThreadData.getLogined();
	}	
	
	public void SetThreadDataItems(List<Thread> Threads) {
		mThreadData.setThreads(Threads);
	}		
	
	public ThreadData GetThreadData() {
		return mThreadData;
	}			
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
	        switch (event.getAction()) {
	        case KeyEvent.ACTION_DOWN:
	        	if (mActionBar.getSelectedNavigationIndex() == mPreferences.getInt("startpage", 0)) {    		
	        		Back();
	        	} else {
	        		mActionBar.setSelectedNavigationItem(mPreferences.getInt("startpage", 0));
	        	}
	   	        return true;
	        }
	    }
	    return false;
	}
}
