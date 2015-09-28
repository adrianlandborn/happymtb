package org.happymtb.unofficial;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.fragment.KoSSearchDialogFragment;
import org.happymtb.unofficial.fragment.KoSSortDialogFragment;
import org.happymtb.unofficial.item.ThreadData;
import org.happymtb.unofficial.item.Thread;
import org.happymtb.unofficial.fragment.ArticlesListFragment;
import org.happymtb.unofficial.fragment.CalendarListFragment;
import org.happymtb.unofficial.fragment.HomesListFragment;
import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.fragment.SettingsFragment;
import org.happymtb.unofficial.fragment.ShopsListFragment;
import org.happymtb.unofficial.fragment.ForumListFragment;
import org.happymtb.unofficial.fragment.VideoListFragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
        ActionBar.OnNavigationListener, KoSSortDialogFragment.SortDialogDataListener,
        KoSSearchDialogFragment.SearchDialogDataListener {

    private static final int HOME = 0;
    private static final int FORUM = 1;
    private static final int ARTICLES = 2;
    private static final int KOP_OCH_SALJ = 3;
    private static final int VIDEO = 4;
    private static final int SHOPS = 5;
    private static final int CALENDAR = 6;
    private static final int SETTINGS = 7;
    public static ThreadData mThreadData = new ThreadData(1, 1, null, 0, false);
    Fragment mRestoredFragment;
    private ActionBar mActionBar;
    ArrayAdapter<String> mActionbarAdapter;
    private SharedPreferences mPreferences;

    private List<SortListener> mSortListeners;
    private List<SearchListener> mSearchListeners;

    private String mCurrentFragmentTag = null;
    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";
    private Toast mBackToast;

    public interface SortListener {
        void onSortParamChanged(int attPos, int orderPos);
    }

    public interface SearchListener {
        void onSearchParamChanged(String text, int category, int region, int type);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setDisplayHomeAsUpEnabled(false);

        // Set up the dropdown list navigation in the action bar.

        mActionbarAdapter = new ArrayAdapter<String>(mActionBar.getThemedContext(),
                android.R.layout.simple_list_item_1,
                android.R.id.text1, new String[]{
                getString(R.string.title_bar_home),
                getString(R.string.title_bar_forum),
                getString(R.string.title_bar_articles),
                getString(R.string.title_bar_kos),
                getString(R.string.title_bar_videos),
                getString(R.string.title_bar_shops),
                getString(R.string.title_bar_calendar),
                getString(R.string.title_bar_settings),});

        // Specify a SpinnerAdapter to populate the dropdown list.
        mActionBar.setListNavigationCallbacks(mActionbarAdapter, this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int startPage = mPreferences.getInt("startpage", HOME);

        mActionBar.setSelectedNavigationItem(startPage);

        // Restore fragments
        if (savedInstanceState != null) {
            //Restore the fragment's instance
//            startPage =  savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);

            mCurrentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            mRestoredFragment = getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        } else {
            startPage = mPreferences.getInt("startpage", HOME);
            mActionBar.setSelectedNavigationItem(startPage);
        }
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
        super.onSaveInstanceState(outState);
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
        outState.putString(CURRENT_FRAGMENT_TAG, mCurrentFragmentTag);
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

    private void switchContent(int position) {

        if (mRestoredFragment != null) {
            mRestoredFragment = null;

            return;
        }

        Fragment frag = null;
        switch (position) {
            case HOME:
                frag = new HomesListFragment();
                mCurrentFragmentTag = HomesListFragment.TAG;
                break;
            case FORUM:
                frag = new ForumListFragment();
                mCurrentFragmentTag = ForumListFragment.TAG;
                break;
            case ARTICLES:
                frag = new ArticlesListFragment();
                mCurrentFragmentTag = ArticlesListFragment.TAG;
                break;
            case KOP_OCH_SALJ:
                frag = new KoSListFragment();
                mCurrentFragmentTag = KoSListFragment.TAG;
                break;
            case VIDEO:
                frag = new VideoListFragment();
                mCurrentFragmentTag = VideoListFragment.TAG;
                break;
            case SHOPS:
                frag = new ShopsListFragment();
                mCurrentFragmentTag = ShopsListFragment.TAG;
                break;
            case CALENDAR:
                frag = new CalendarListFragment();
                mCurrentFragmentTag = CalendarListFragment.TAG;
                break;
            case SETTINGS:
                frag = new SettingsFragment();
                mCurrentFragmentTag = SettingsFragment.TAG;
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, frag, mCurrentFragmentTag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mBackToast != null && mBackToast.getView().getWindowToken() != null) {
            finish();
        } else {
            mBackToast = Toast.makeText(this, "Tryck igen för att avsluta", Toast.LENGTH_SHORT);
            mBackToast.show();
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

    public void setThreadLoggedIn(Boolean loggedIn) {
        mThreadData.setLoggedIn(loggedIn);
    }

    public boolean getThreadLoggedIn() {
        return mThreadData.getLoggedIn();
    }

    public void setThreadDataItems(List<Thread> threads) {
        mThreadData.setThreads(threads);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (mActionBar.getSelectedNavigationIndex() == SETTINGS) {
                        mActionBar.setSelectedNavigationItem(mPreferences.getInt("startpage", 0));
                    } else {
                        onBackPressed();
                    }
                    return true;
            }
        }
        return false;
    }

    public void addSortListener(SortListener l) {
        if (mSortListeners == null ) {
            mSortListeners = new ArrayList<SortListener>();
        }
        mSortListeners.add(l);
    }

    public void removeSortListener(SortListener l) {
        mSortListeners.remove(l);
    }

    public void addSearchListener(SearchListener l) {
        if (mSearchListeners == null) {
            mSearchListeners = new ArrayList<SearchListener>();
        }
        mSearchListeners.add(l);
    }

    public void removeSearchListener(SearchListener l) {
        mSearchListeners.remove(l);
    }

    @Override
    public void onSortData(int attrPos, int orderPos) {
        for (SortListener l : mSortListeners) {
            l.onSortParamChanged(attrPos, orderPos);
        }
    }

    @Override
    public void onSearchData(String text, int category, int region, int type) {
        for (SearchListener l : mSearchListeners) {
            l.onSearchParamChanged(text, category, region, type);
        }
    }
}
