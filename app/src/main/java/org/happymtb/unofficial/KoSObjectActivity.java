package org.happymtb.unofficial;

import org.happymtb.unofficial.fragment.KoSObjectFragment;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class KoSObjectActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
        ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

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
	
	public String getObjectLink() {
		String objectLink = "";
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action != null) {
			Uri data = intent.getData();
			objectLink = data.toString();

		} else {
			objectLink = intent.getExtras().getString("KoSObjectLink");
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