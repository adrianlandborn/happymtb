package pebeijer.happymtb.fragment;

import java.util.List;

import pebeijer.happymtb.MainActivity;
import pebeijer.happymtb.MessageActivity;
import pebeijer.happymtb.R;
import pebeijer.happymtb.listener.LoginListener;
import pebeijer.happymtb.listener.MarkAsReadListener;
import pebeijer.happymtb.listener.ThreadListListener;
import pebeijer.happymtb.adapter.ListThreadsAdapter;
import pebeijer.happymtb.helpers.Utilities;
import pebeijer.happymtb.item.Thread;
import pebeijer.happymtb.item.ThreadData;
import pebeijer.happymtb.task.LoginTask;
import pebeijer.happymtb.task.MarkAsReadTask;
import pebeijer.happymtb.task.ThreadListTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ThreadListFragment extends ListFragment implements DialogInterface.OnCancelListener {
	private final static int DIALOG_FETCH_THREADS_ERROR = 0;
	private final static int DIALOG_MARK_AS_READ_ERROR = 1;	

	private ProgressDialog progDialog = null;
	private ThreadListTask getThreads;
	public static ThreadData mThreadData = new ThreadData(1, 1, null, 0, false);
	private MarkAsReadTask markAsRead;	
	private SharedPreferences preferences;
	private String mUsername = "";
	private static Toast mToast;
	MainActivity mActivity;
	TextView mLoginStatus;
	TextView mPageText;
	TextView mCurrentPage;		
	TextView mByText;
	TextView mMaxPages;
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();
		
		setListShownNoAnimation(true);
		mToast = Toast.makeText(mActivity , "" , Toast.LENGTH_LONG );
		
		CookieSyncManager.createInstance(mActivity);
		CookieSyncManager.getInstance().startSync();		
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mUsername = preferences.getString("username", "");				
		
		if (!mActivity.GetThreadLogined()) {
			if (mUsername.length() > 0) {
				progDialog = ProgressDialog.show(mActivity, "", "", true, true);
				progDialog.setContentView(R.layout.progresslayout);
				progDialog.setOnCancelListener(this);
	
				LoginTask login = new LoginTask();
				login.addLoginListener(new LoginListener() {
					public void Success() {
						mActivity.SetThreadLogined(true);
					
						ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.thread_login_status_image);
						LoginStatusImage.setImageResource(R.drawable.online);
	
						TextView LoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
						LoginStatus.setText("Inloggad som " + mUsername);
						
						setHasOptionsMenu(true);
						
						FetchData();		
					}
	
					public void Fail() {
						mActivity.SetThreadLogined(false);
						
						ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.thread_login_status_image);
						LoginStatusImage.setImageResource(R.drawable.offline);
	
						TextView LoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
						LoginStatus.setText("Ej inloggad");
	
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("cookiename", "");
						editor.putString("cookievalue", "");
						editor.commit();
						
						setHasOptionsMenu(true);
						
						FetchData();		
					}
				});
				login.execute(mActivity);
			}
		} else {
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("cookiename", "");
			editor.putString("cookievalue", "");
			editor.commit();
			
			setHasOptionsMenu(true);
			
			mActivity.SetThreadLogined(true);
			
			ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.thread_login_status_image);
			LoginStatusImage.setImageResource(R.drawable.online);

			TextView LoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
			LoginStatus.setText("Inloggad som " + mUsername);		
			
			FetchData();		
		}

/*		
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	    	@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
	    		if ((mThreadData.getThreads() != null) && (mThreadData.getThreads().size() > 0)) {
	    			mToast.setText(mThreadData.getThreads().get(position).getMessageText());
	                mToast.show();
	    		}
				return true;
			}
	    });		
*/        
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		int mTextSize = Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 0)]);	
	    
		mLoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
		mPageText = (TextView) mActivity.findViewById(R.id.thread_page_text);
		mCurrentPage = (TextView) mActivity.findViewById(R.id.thread_current_page);		
		mByText = (TextView) mActivity.findViewById(R.id.thread_by_text);
		mMaxPages = (TextView) mActivity.findViewById(R.id.thread_no_of_pages);
		
		mLoginStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mPageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mCurrentPage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mByText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mMaxPages.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
	}		

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		if (mActivity.GetThreadLogined() == false) {
			inflater.inflate(R.menu.threadmenu, menu);
		} else {
			inflater.inflate(R.menu.threadloginedmenu, menu);				
		}
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.thread_submenu:
			return true;
		case R.id.thread_left:
			PreviousPage();
			return true;
		case R.id.thread_refresh:
			RefreshPage();
			return true;
		case R.id.thread_right:
			NextPage();
			return true;	
		case R.id.thread_markasread:
			MarkAsRead();
			return true;			
		case R.id.thread_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

			alert.setTitle("Gå till sidan...");
			alert.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mThreadData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			alert.setView(input);

			alert.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (Utilities.isInteger(input.getText().toString())) {
						mThreadData.setCurrentPage(Integer.parseInt(input.getText().toString()));
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
		case R.id.thread_new_thread:
			String url = "http://happymtb.org/forum/posting.php/1";
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			startActivity(browserIntent);							
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
	
	@Override
	public void onDestroy() {
		progDialog.dismiss();
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

	public void MarkAsRead() {	
		if ((progDialog == null) || (!progDialog.isShowing())) {
			progDialog = ProgressDialog.show(mActivity, "", "", true, true);
			progDialog.setContentView(R.layout.progresslayout);
			progDialog.setOnCancelListener(this);
		}

		markAsRead = new MarkAsReadTask();
		markAsRead.addMarkAsReadListener(new MarkAsReadListener() {
			public void Success() {
				FetchData();
			}

			public void Fail() {
				progDialog.dismiss();
//				showDialog(DIALOG_MARK_AS_READ_ERROR);
			}
		});
		markAsRead.execute(mActivity);	
	}
	
	public void RefreshPage() {
		mThreadData.setListPosition(0);
		mActivity.SetThreadDataItems(null);
		FetchData();
	}

	public void NextPage() {
		if (mThreadData.getCurrentPage() < mThreadData.getMaxPages()) {
			mThreadData.setListPosition(0);
			mActivity.SetThreadDataItems(null);
			mThreadData.setCurrentPage(mThreadData.getCurrentPage() + 1);
			FetchData();
		}
	}

	public void PreviousPage() {
    	if (mThreadData.getCurrentPage() > 1) {
    		mThreadData.setListPosition(0);
    		mActivity.SetThreadDataItems(null);
    		mThreadData.setCurrentPage(mThreadData.getCurrentPage() - 1);
			FetchData();
    	}
	}
	
	public void FetchData() {
		if ((progDialog == null) || (!progDialog.isShowing())) {
			progDialog = ProgressDialog.show(mActivity, "", "", true, true);
			progDialog.setContentView(R.layout.progresslayout);
			progDialog.setOnCancelListener(this);
		}
		
		getThreads = new ThreadListTask();
		getThreads.addThreadListListener(new ThreadListListener() {
			public void Success(List<Thread> threads) {
				mThreadData.setThreads(threads);
				FillList();
				progDialog.dismiss();
			}

			public void Fail() {
				progDialog.dismiss();
//				showDialog(DIALOG_MARK_AS_READ_ERROR);
			}
		});
		getThreads.execute(mThreadData.getCurrentPage(), mActivity);
	}

	private void FillList() {
		ListThreadsAdapter adapter = new ListThreadsAdapter(mActivity, mThreadData.getThreads());
		setListAdapter(adapter);
		
		getListView().setSelection(mThreadData.getListPosition());
		
		mCurrentPage.setText(Integer.toString(mThreadData.getCurrentPage()));					
		mMaxPages.setText(Integer.toString(mThreadData.getThreads().get(0).getNumberOfThreadPages()));
		mThreadData.setMaxPages(mThreadData.getThreads().get(0).getNumberOfThreadPages());						
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		OpenThread(position);			
	}

	public void OpenThread (int position) {
		Intent Message = new Intent(mActivity, MessageActivity.class);
		Message.putExtra("ThreadId", mThreadData.getThreads().get(position).getThreadId());
		Message.putExtra("Logined", mActivity.GetThreadLogined());
		Message.putExtra("New", false);
		startActivity(Message);				
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_THREADS_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage("Det blev något fel vid hämtning av trådarna")
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

		case DIALOG_MARK_AS_READ_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel när alla inläggen skulle markeras som lästa")
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
	public void onCancel(DialogInterface arg0) {
		if (markAsRead != null) {
			markAsRead.cancel(true);
		}

		if (getThreads != null) {
			getThreads.cancel(true);
		}	
	}
}
