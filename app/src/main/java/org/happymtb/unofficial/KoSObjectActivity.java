package org.happymtb.unofficial;

import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.fragment.KoSObjectFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class KoSObjectActivity extends AppCompatActivity {

	public final static String AREA = "area";
	public final static String TYPE = "type";
	public final static String TITLE = "title";
	public final static String DATE = "date";
	public final static String PRICE = "price";
	public final static String CATEGORY = "category";
	public final static String URL = "KoSObjectLink";

	private Tracker mTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.kos_object_frame);


		// Obtain the shared Tracker instance.
		HappyApplication application = (HappyApplication) getApplication();
		mTracker = application.getDefaultTracker();

		// [START Google analytics screen]
		mTracker.setScreenName(GaConstants.Categories.KOS_OBJECT);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		// [END sGoogle analytics screen]

        Fragment fragment;

		if (savedInstanceState == null) {
			fragment = new KoSObjectFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.kosobjectframe, fragment, "kos_object")
					.commit();
		}
	}

	public long getObjectId() {
		String[] urlSplit = getObjectLink().split("id=");
		if (urlSplit.length == 2) {
			return Long.parseLong(urlSplit[1]);
		} else {
		return -1;
		}
	}
	
	public String getObjectArea() {
		return getIntent().getStringExtra(AREA);
	}

	public String getObjectDate() {
		return getIntent().getStringExtra(DATE);
	}

	public String getObjectType() {
		return getIntent().getStringExtra(TYPE);
	}

	public String getObjectTitle() {
		return getIntent().getStringExtra(TITLE);
	}

	public String getObjectCategory() {
        return getIntent().getStringExtra(CATEGORY);
	}

	public String getObjectPrice() {
		return getIntent().getStringExtra(PRICE);
	}

	public String getObjectLink() {
		String objectLink = "";
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action != null) {
			Uri data = intent.getData();
			objectLink = data.toString();

		} else {
			objectLink = intent.getExtras().getString(URL);
		}

		return objectLink;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		this.finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	public Tracker getTracker() {
		return mTracker;
	}
}