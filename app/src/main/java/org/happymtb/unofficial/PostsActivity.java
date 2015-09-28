package org.happymtb.unofficial;

import java.util.List;

import org.happymtb.unofficial.fragment.PostsListFragment;
import org.happymtb.unofficial.item.Message;
import org.happymtb.unofficial.item.MessageData;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class PostsActivity extends FragmentActivity {
	private ActionBar mActionBar;
	Fragment mFragment = new PostsListFragment();
	int mFrameId = R.id.messageframe;
	int mFrameLayout = R.layout.message_frame;
	public static MessageData mMessageData = new MessageData(1, 1, false, "", null, 0);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		
		// Set up the action bar to show a dropdown list.
		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		String ThreadId;
		Bundle bundle = getIntent().getExtras();
		ThreadId = bundle.getString("ThreadId");
		
		Boolean Logined;
		bundle = getIntent().getExtras();
		Logined = bundle.getBoolean("Logined");

		Boolean New;
		bundle = getIntent().getExtras();
		New = bundle.getBoolean("New");

		int Page;
		bundle = getIntent().getExtras();
		Page = bundle.getInt("Page");		
		
		mMessageData = new MessageData(Page, 1, Logined, ThreadId, null, 0);
		
		mFragment = new PostsListFragment();
		mFrameId = R.id.messageframe;
		mFrameLayout = R.layout.message_frame;
			
		setContentView(mFrameLayout);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(mFrameId, mFragment, PostsListFragment.TAG)
					.commit();
		}
	}
	
	public void SetMessageData(MessageData MessageData) {
		mMessageData = MessageData;
	}
	
	public void SetMessageDataItems(List<Message> Messages) {
		mMessageData.setMessages(Messages);
	}		
	
	public MessageData GetMessageData() {		 
		return mMessageData;
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