package org.happymtb.unofficial;

import java.util.List;

import org.happymtb.unofficial.item.KoSData;
import org.happymtb.unofficial.item.ThreadData;
import org.happymtb.unofficial.item.KoSItem;
import org.happymtb.unofficial.item.Thread;
import org.happymtb.unofficial.fragment.ArticlesListFragment;
import org.happymtb.unofficial.fragment.CalendarListFragment;
import org.happymtb.unofficial.fragment.HomesListFragment;
import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.fragment.SettingsFragment;
import org.happymtb.unofficial.fragment.ShopsListFragment;
import org.happymtb.unofficial.fragment.ThreadListFragment;
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
        ActionBar.OnNavigationListener {

    private static final int HOME = 1;
    private static final int FORUM = 2;
    private static final int ARTICLES = 3;
    private static final int KOP_OCH_SALJ = 4;
    private static final int VIDEO = 5;
    private static final int SHOPS = 6;
    private static final int CALENDAR = 7;
    private static final int SETTINGS = 8;
    public static String mActiveKoSObjectLink = "";
    public static KoSData mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "ASC", "Stigande", 0);
    public static ThreadData mThreadData = new ThreadData(1, 1, null, 0, false);
    Fragment mFragment = new HomesListFragment();
    int mFrameId = R.id.homeframe;
    int mFrameLayout = R.layout.home_frame;
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
                android.R.id.text1, new String[]{
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

    private void switchContent(int position) {

        switch (position + 1) {
            case HOME:
                mFragment = new HomesListFragment();
                mFrameId = R.id.homeframe;
                mFrameLayout = R.layout.home_frame;
                break;
            case FORUM:
                mFragment = new ThreadListFragment();
                mFrameId = R.id.threadframe;
                mFrameLayout = R.layout.thread_frame;
                break;
            case ARTICLES:
                mFragment = new ArticlesListFragment();
                mFrameId = R.id.content_frame;
                mFrameLayout = R.layout.content_frame;
                break;
            case KOP_OCH_SALJ:
                mFragment = new KoSListFragment();
                mFrameId = R.id.kosframe;
                mFrameLayout = R.layout.kos_frame;
                break;
            case VIDEO:
                mFragment = new VideoListFragment();
                mFrameId = R.id.videoframe;
                mFrameLayout = R.layout.video_frame;
                break;
            case SHOPS:
                mFragment = new ShopsListFragment();
                mFrameId = R.id.content_frame;
                mFrameLayout = R.layout.content_frame;
                break;
            case CALENDAR:
                mFragment = new CalendarListFragment();
                mFrameId = R.id.calendarframe;
                mFrameLayout = R.layout.calendar_frame;
                break;
            case SETTINGS:
                mFragment = new SettingsFragment();
                mFrameId = R.id.settingsframe;
                mFrameLayout = R.layout.settings_frame;
                break;
        }
        setContentView(mFrameLayout);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mFrameId, mFragment)
                .addToBackStack(null)
                .commit();
    }

    private void back() {
        if (mBackToast != null && mBackToast.getView().getWindowToken() != null) {
            finish();
        } else {
            mBackToast = Toast.makeText(this, "Tryck igen för att avsluta", Toast.LENGTH_SHORT);
            mBackToast.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mActionBar.getSelectedNavigationIndex() == mPreferences.getInt("startpage", 0)) {
            back();
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

    public void setActiveObjectLink(String activeKoSObjectLink) {
        mActiveKoSObjectLink = activeKoSObjectLink;
    }

    public String GetActiveObjectLink() {
        return mActiveKoSObjectLink;
    }

    public void setKoSData(KoSData koSData) {
        mKoSData = koSData;
    }

    public void setKoSDataItems(List<KoSItem> koSItems) {
        mKoSData.setKoSItems(koSItems);
    }

    public KoSData getKoSData() {
        return mKoSData;
    }

    public void setThreadData(ThreadData threadData) {
        mThreadData = threadData;
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

    public ThreadData GetThreadData() {
        return mThreadData;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_DOWN:
                    if (mActionBar.getSelectedNavigationIndex() == mPreferences.getInt("startpage", 0)) {
                        back();
                    } else {
                        mActionBar.setSelectedNavigationItem(mPreferences.getInt("startpage", 0));
                    }
                    return true;
            }
        }
        return false;
    }
}
