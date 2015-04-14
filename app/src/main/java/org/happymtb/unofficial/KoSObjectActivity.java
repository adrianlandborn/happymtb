package org.happymtb.unofficial;

import org.happymtb.unofficial.fragment.KoSObjectFragment;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class KoSObjectActivity extends FragmentActivity {
	private ActionBar mActionBar;
	Fragment mFragment = new KoSObjectFragment();	
	int mFrameId = R.id.kosobjectframe;
	int mFrameLayout = R.layout.kos_object_frame;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
		mActionBar = getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayHomeAsUpEnabled(true);		
				
		mFragment = new KoSObjectFragment();
		mFrameId = R.id.kosobjectframe;
		mFrameLayout = R.layout.kos_object_frame;
			
		setContentView(mFrameLayout);
		getSupportFragmentManager()
        .beginTransaction()
        .replace(mFrameId, mFragment)
        .commit();        
	}
	
	public String GetObjectLink() {
		String ObjectLink;
		Bundle bundle = getIntent().getExtras();
		ObjectLink = bundle.getString("KoSObjectLink");

		return ObjectLink;
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