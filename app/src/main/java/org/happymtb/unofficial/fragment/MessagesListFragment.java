package org.happymtb.unofficial.fragment;

import java.io.File;
import java.util.List;

import org.happymtb.unofficial.MessageActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListMessagesAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Message;
import org.happymtb.unofficial.item.MessageData;
import org.happymtb.unofficial.listener.MessageListListener;
import org.happymtb.unofficial.task.MessageImageDownloadTask;
import org.happymtb.unofficial.task.MessageListTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesListFragment extends ListFragment implements DialogInterface.OnCancelListener{
	private final static int DIALOG_FETCH_MESSAGES_ERROR = 0;
	
	private ProgressDialog mProgressDialog = null;
	private MessageListTask mMessageListTask;
	private MessageData mMessageData;
	private ListMessagesAdapter mMessageAdapter;
	private SharedPreferences mPreferences;
	private String mUsername = "";
	MessageActivity mActivity;	
	TextView mLoginStatus;
	TextView mPageText;
	TextView mCurrentPage;		
	TextView mByText;
	TextView mMaxPages;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mActivity = ((MessageActivity) getActivity());
		
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);		
		
		CookieSyncManager.createInstance(mActivity);
		CookieSyncManager.getInstance().startSync();	
		
		mMessageData = mActivity.GetMessageData();
		fetchData();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mUsername = mPreferences.getString("username", "");
				
		if (mMessageData.getLogined()) {
			ImageView loginStatusImage = (ImageView) mActivity.findViewById(R.id.message_login_status_image);
			loginStatusImage.setImageResource(R.drawable.ic_online);

			TextView loginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
			loginStatus.setText("Inloggad som " + mUsername);
		} else {
			ImageView loginStatusImage = (ImageView) mActivity.findViewById(R.id.message_login_status_image);
			loginStatusImage.setImageResource(R.drawable.ic_offline);

			TextView loginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
			loginStatus.setText("Ej inloggad");
		}

		ClearBitmapDir();

		mLoginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
		mPageText = (TextView) mActivity.findViewById(R.id.message_page_text);
		mCurrentPage = (TextView) mActivity.findViewById(R.id.message_current_page);		
		mByText = (TextView) mActivity.findViewById(R.id.message_by_text);
		mMaxPages = (TextView) mActivity.findViewById(R.id.message_no_of_pages);
		
	}
	
	public void ClearBitmapDir() {
 		String PATH = "/mnt/sdcard/happymtb/";		
 		File dir = new File(PATH);
		if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(dir, children[i]).delete();
	        }
	    }		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.message_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.message_submenu:
			return true;
		case R.id.message_left:
			previousPage();
			return true;
		case R.id.message_refresh:
			refreshPage();
			return true;
		case R.id.message_right:
			nextPage();
			return true;								
		case R.id.message_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

			alert.setTitle("Gå till sidan...");
			alert.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mMessageData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			alert.setView(input);

			alert.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString())) {
						mMessageData.setCurrentPage(Integer.parseInt(input.getText().toString()));
						fetchData();
					}					
				}
			});

			alert.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
				}
			});

			alert.show();
			return true;	
		}
		return super.onOptionsItemSelected(item);
	}			
	
	@Override
	public void onDestroy() {
		mProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
		CookieSyncManager.getInstance().stopSync();
	}

	@Override
	public void onStart() {
		super.onStart();
		CookieSyncManager.getInstance().sync();
	}

	public void refreshPage() {
		mMessageData.setListPosition(0);
		mActivity.SetMessageDataItems(null);		
		fetchData();
	}

	public void nextPage() {
		if (mMessageData.getCurrentPage() < mMessageData.getMaxPages()) {
			mMessageData.setListPosition(0);
			mActivity.SetMessageDataItems(null);
			mMessageData.setCurrentPage(mMessageData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void previousPage() {
    	if (mMessageData.getCurrentPage() > 1) {
			mMessageData.setListPosition(0);
			mActivity.SetMessageDataItems(null);    	
    		mMessageData.setCurrentPage(mMessageData.getCurrentPage() - 1);
    		fetchData();
    	}
	}
	
	private void fetchData() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mMessageData = mActivity.GetMessageData();
		if ((mMessageData.getMessages() != null) && (mMessageData.getMessages().size() > 0)) {
			fillList();
			mProgressDialog.dismiss();
		} else {				
			mMessageListTask = new MessageListTask();
			mMessageListTask.addMessageListListener(new MessageListListener() {
				public void success(List<Message> messages) {
                    if (getActivity() != null) {
                        mMessageData.setMessages(messages);
                        fillList();
                        mProgressDialog.dismiss();
                    }
				}
	
				public void fail() {
                    if (getActivity() != null) {
                        mProgressDialog.dismiss();
                    }
				}
			});
			mMessageListTask.execute(mActivity, mMessageData.getThreadId(), Integer.toString(mMessageData.getCurrentPage()));
		}
	}

	private void fillList() {
		mMessageAdapter = new ListMessagesAdapter(mActivity, mMessageData.getMessages());
		setListAdapter(mMessageAdapter);
		
		new MessageImageDownloadTask().execute(mMessageData.getMessages(), mMessageAdapter, mActivity);

		getListView().setSelection(mMessageData.getListPosition());
		
		mCurrentPage.setText(Integer.toString(mMessageData.getCurrentPage()));		
		mMaxPages.setText(Integer.toString(mMessageData.getMessages().get(0).getNumberOfMessagePages()));
		mMessageData.setMaxPages(mMessageData.getMessages().get(0).getNumberOfMessagePages());		
	}

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_MESSAGES_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage("Det blev något fel vid hämtning av inläggen")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									mActivity.finish();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mMessageListTask != null) {
			mMessageListTask.cancel(true);
		}
	}
}
