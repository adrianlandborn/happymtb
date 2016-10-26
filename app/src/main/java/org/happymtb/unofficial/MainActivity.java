package org.happymtb.unofficial;

import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.fragment.ArticlesListFragment;
import org.happymtb.unofficial.fragment.CalendarListFragment;
import org.happymtb.unofficial.fragment.HomesListFragment;
import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.fragment.SavedListFragment;
import org.happymtb.unofficial.fragment.SettingsFragment;
import org.happymtb.unofficial.fragment.ShopsListFragment;
import org.happymtb.unofficial.fragment.ForumListFragment;
import org.happymtb.unofficial.fragment.VideoListFragment;
import org.happymtb.unofficial.helpers.HappyUtils;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import com.crashlytics.android.Crashlytics;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kobakei.ratethisapp.RateThisApp;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int HOME = 0;
    private static final int FORUM = 1;
    private static final int ARTICLES = 2;
    private static final int KOP_OCH_SALJ = 3;
    private static final int SAVED = 4;
    private static final int PROFILE = 5;
    private static final int VIDEO = 6;
    private static final int SHOPS = 7;
    private static final int CALENDAR = 8;
    private static final int SETTINGS = 9;

    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String CURRENT_FRAGMENT_TAG = "current_fragment_tag";
    private static final String OPEN_DRAWER = "open_drawer";
    private static final String SHOW_RATING_DIALOG = "show_Rating_dialog";

    private Tracker mTracker;

    private Fragment mCurrentFragment;
    private SharedPreferences mPreferences;

    private String mCurrentFragmentTag = null;
    private boolean mLogin;
    private NavigationView mNavigationView;
    DrawerLayout mDrawer;


    private SlidingMenu mKosSlidingMenu;

    private int mCheckedNavigationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        if (HappyUtils.isDebug(getApplicationContext())) {
            setContentView(R.layout.activity_main_drawer_debug);
        } else {
            setContentView(R.layout.activity_main_drawer);
        }

        // Obtain the shared Tracker instance.
        HappyApplication application = (HappyApplication) getApplication();
        mTracker = application.getDefaultTracker();

        // [START Google analytics screen]
        mTracker.setScreenName(GaConstants.Categories.MAIN);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END sGoogle analytics screen]

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Restore fragments
        if (savedInstanceState != null) {

            mCurrentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG);
            mCurrentFragment = getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        } else {
            //TODO Enable after list is fixed
//            mCheckedNavigationItem = mPreferences.getInt(SettingsActivity.START_PAGE, KOP_OCH_SALJ);
            mCheckedNavigationItem = KOP_OCH_SALJ;
            switchContent(mCheckedNavigationItem);
            mNavigationView.getMenu().getItem(mCheckedNavigationItem).setChecked(true);
        }

        //Open the drawer the first time the app is started
        if (mPreferences.getBoolean(OPEN_DRAWER, true)) {
            mDrawer.openDrawer(GravityCompat.START);
            mPreferences.edit().putBoolean(OPEN_DRAWER, false).apply();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);

        // Force show the Rate dialog
//        RateThisApp.showRateDialog(this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            //Restore the fragment's instance
            mCheckedNavigationItem =  savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
            mNavigationView.getMenu().getItem(mCheckedNavigationItem).setChecked(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mCheckedNavigationItem);
//        outState.putString(CURRENT_FRAGMENT_TAG, mCurrentFragmentTag);
    }

//    @Override
//    public boolean onNavigationItemSelected(int position, long id) {
//         When the given dropdown item is selected, show its contents in the
//         container view.
//        switchContent(position);
//        return true;
//    }

    private void switchContent(int position) {

//        if (mCurrentFragment != null) {
//            mCurrentFragment = null;
//
//            return;
//        }

        Fragment frag = null;
        String newFragmentTag = "";
        switch (position) {
            case HOME:
                frag = new HomesListFragment();
                newFragmentTag = HomesListFragment.TAG;
                break;
            case FORUM:
                frag = new ForumListFragment();
                newFragmentTag = ForumListFragment.TAG;
                break;
            case ARTICLES:
                frag = new ArticlesListFragment();
                newFragmentTag = ArticlesListFragment.TAG;
                break;
            case KOP_OCH_SALJ:
                frag = new KoSListFragment();
                newFragmentTag = KoSListFragment.TAG;
                break;
            case SAVED:
                frag = new SavedListFragment();
                newFragmentTag = SavedListFragment.TAG;
                break;
            case PROFILE:
                Intent browserIntent = new Intent(this, WebViewActivity.class);
                browserIntent.putExtra("url", "http://happyride.se/annonser/my_ads.php");
                startActivity(browserIntent);
                break;
            case VIDEO:
                frag = new VideoListFragment();
                newFragmentTag = VideoListFragment.TAG;
                break;
            case SHOPS:
                frag = new ShopsListFragment();
                newFragmentTag = ShopsListFragment.TAG;
                break;
            case CALENDAR:
                frag = new CalendarListFragment();
                newFragmentTag = CalendarListFragment.TAG;
                break;
            case SETTINGS:
                frag = new SettingsFragment();
                newFragmentTag = SettingsFragment.TAG;
                break;
        }

        if (mKosSlidingMenu != null) {
            if (newFragmentTag.equals(KoSListFragment.TAG)) {
                mKosSlidingMenu.setSlidingEnabled(true);
            } else {
                mKosSlidingMenu.setSlidingEnabled(false);
            }
        }

        if (frag != null && !newFragmentTag.equals(mCurrentFragmentTag)) {
            System.out.println("New Fragment: " + frag);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, frag, newFragmentTag)
                    .addToBackStack(null)
                    .commit();

            mCurrentFragmentTag = newFragmentTag;
            mCurrentFragment = frag;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
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

    public void setLoggedIn(boolean loggedIn) {
        mLogin = loggedIn;
    }

    public boolean isLoggedIn() {
        return mLogin;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);

        int id = item.getItemId();
        int pos = 0;

        if (id == R.id.nav_home) {
            pos = HOME;
        } else if (id == R.id.nav_forum) {
            pos = FORUM;
        } else if (id == R.id.nav_articles) {
            pos = ARTICLES;
        } else if (id == R.id.nav_kos) {
            pos = KOP_OCH_SALJ;
        } else if (id == R.id.nav_saved) {
            pos = SAVED;
        } else if (id == R.id.nav_my_ads) {
            pos = PROFILE;
        } else if (id == R.id.nav_videos) {
            pos = VIDEO;
        } else if (id == R.id.nav_shops) {
            pos = SHOPS;
        } else if (id == R.id.nav_calendar) {
            pos = CALENDAR;
        } else if (id == R.id.nav_settings) {
            pos = SETTINGS;
        }
        if (pos == SETTINGS) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        mCheckedNavigationItem = pos;
        switchContent(pos);

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public SlidingMenu getKosSlidingMenu() {
        return mKosSlidingMenu;
    }

    public void setKosSlidingMenu(SlidingMenu slidingMenu) {
        this.mKosSlidingMenu = slidingMenu;
    }


    public Tracker getTracker() {
        return mTracker;
    }
}
