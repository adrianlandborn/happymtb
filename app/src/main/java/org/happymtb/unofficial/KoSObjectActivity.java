package org.happymtb.unofficial;

import org.happymtb.unofficial.fragment.KoSObjectFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class KoSObjectActivity extends AppCompatActivity {

	public final static String CATEGORY = "category";
	public final static String URL = "KoSObjectLink";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		setContentView(R.layout.kos_object_frame);
        Fragment fragment;

		if (savedInstanceState == null) {
			fragment = new KoSObjectFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.kosobjectframe, fragment, "kos_object")
					.commit();
		}
	}
	
	public String getCategory() {
		return getIntent().getStringExtra(CATEGORY);
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
}