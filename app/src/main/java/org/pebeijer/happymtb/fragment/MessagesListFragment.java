package org.pebeijer.happymtb.fragment;

import java.io.File;
import java.util.List;

import org.pebeijer.happymtb.MessageActivity;
import org.pebeijer.happymtb.R;
import org.pebeijer.happymtb.adapter.ListMessagesAdapter;
import org.pebeijer.happymtb.helpers.Utilities;
import org.pebeijer.happymtb.item.Message;
import org.pebeijer.happymtb.item.MessageData;
import org.pebeijer.happymtb.listener.MessageListListener;
import org.pebeijer.happymtb.task.MessageImageDownloadTask;
import org.pebeijer.happymtb.task.MessageListTask;

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
		FetchData();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mUsername = mPreferences.getString("username", "");
				
		if (mMessageData.getLogined()) {
			ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.message_login_status_image);
			LoginStatusImage.setImageResource(R.drawable.online);

			TextView LoginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
			LoginStatus.setText("Inloggad som " + mUsername);
		} else {
			ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.message_login_status_image);
			LoginStatusImage.setImageResource(R.drawable.offline);

			TextView LoginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
			LoginStatus.setText("Ej inloggad");
		}

		ClearBitmapDir();
		
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		int mTextSize = Integer.parseInt(TextSizeArray[mPreferences.getInt("textsize", 0)]);
	    
		mLoginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
		mPageText = (TextView) mActivity.findViewById(R.id.message_page_text);
		mCurrentPage = (TextView) mActivity.findViewById(R.id.message_current_page);		
		mByText = (TextView) mActivity.findViewById(R.id.message_by_text);
		mMaxPages = (TextView) mActivity.findViewById(R.id.message_no_of_pages);
		
		mLoginStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mPageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mCurrentPage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mByText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mMaxPages.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
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
			PreviousPage();
			return true;
		case R.id.message_refresh:
			RefreshPage();
			return true;
		case R.id.message_right:
			NextPage();
			return true;								
		case R.id.message_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

			alert.setTitle("G� till sidan...");
			alert.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mMessageData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			alert.setView(input);

			alert.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (Utilities.isInteger(input.getText().toString())) {
						mMessageData.setCurrentPage(Integer.parseInt(input.getText().toString()));
						FetchData();
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

	public void RefreshPage() {
		mMessageData.setListPosition(0);
		mActivity.SetMessageDataItems(null);		
		FetchData();
	}

	public void NextPage() {
		if (mMessageData.getCurrentPage() < mMessageData.getMaxPages()) {
			mMessageData.setListPosition(0);
			mActivity.SetMessageDataItems(null);
			mMessageData.setCurrentPage(mMessageData.getCurrentPage() + 1);
			FetchData();
		}
	}

	public void PreviousPage() {
    	if (mMessageData.getCurrentPage() > 1) {
			mMessageData.setListPosition(0);
			mActivity.SetMessageDataItems(null);    	
    		mMessageData.setCurrentPage(mMessageData.getCurrentPage() - 1);
    		FetchData();
    	}
	}
	
	private void FetchData() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mMessageData = mActivity.GetMessageData();
		if ((mMessageData.getMessages() != null) && (mMessageData.getMessages().size() > 0)) {
			FillList();	
			mProgressDialog.dismiss();
		} else {				
			mMessageListTask = new MessageListTask();
			mMessageListTask.addMessageListListener(new MessageListListener() {
				public void Success(List<Message> messages) {
					mMessageData.setMessages(messages);
					FillList();					
					mProgressDialog.dismiss();
				}
	
				public void Fail() {
					mProgressDialog.dismiss();
	//				showDialog(DIALOG_FETCH_MESSAGES_ERROR);
				}
			});
			mMessageListTask.execute(mActivity, mMessageData.getThreadId(), Integer.toString(mMessageData.getCurrentPage()));
		}
	}

	private void FillList() {	
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
			builder.setMessage("Det blev n�got fel vid h�mtning av inl�ggen")
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
