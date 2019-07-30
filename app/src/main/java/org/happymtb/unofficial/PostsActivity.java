package org.happymtb.unofficial;

import org.happymtb.unofficial.fragment.PostsListFragment;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

public class PostsActivity extends AppCompatActivity {
    public static final String PAGE = "Page";
    public static final String LOGGED_IN = "Logined";
    public static final String NEW_POST = "New";
    public static final String THREAD_ID = "ThreadId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.posts_frame);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.messageframe, new PostsListFragment(), PostsListFragment.TAG)
					.commit();
		}
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