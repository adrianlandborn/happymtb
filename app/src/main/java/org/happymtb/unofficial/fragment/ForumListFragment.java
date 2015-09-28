package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.PostsActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListThreadsAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Thread;
import org.happymtb.unofficial.item.ThreadData;
import org.happymtb.unofficial.listener.LoginListener;
import org.happymtb.unofficial.listener.MarkAsReadListener;
import org.happymtb.unofficial.listener.PageTextWatcher;
import org.happymtb.unofficial.listener.ThreadListListener;
import org.happymtb.unofficial.task.LoginTask;
import org.happymtb.unofficial.task.MarkAsReadTask;
import org.happymtb.unofficial.task.ThreadListTask;

import java.util.List;

public class ForumListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener {
	private final static int DIALOG_FETCH_THREADS_ERROR = 0;
	private final static int DIALOG_MARK_AS_READ_ERROR = 1;	
	public final static String COOKIE_NAME = "cookiename";
	public final static String COOKIE_VALUE = "cookivalue";
	public static String TAG = "forum_frag";

	private ThreadListTask mThreadsTask;
	public static ThreadData mThreadData = new ThreadData(1, 1, null, 0, false);
	private MarkAsReadTask mMarkAsRead;
	private SharedPreferences mPreferences;
	private String mUsername = "";
	private MainActivity mActivity;
	private ListView mListView;
	private TextView mLoginStatus;
	private TextView mPageText;
	private TextView mCurrentPage;
	private TextView mByText;
	private TextView mMaxPages;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.thread_frame, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();
		
//		setListShownNoAnimation(true);

		CookieSyncManager.createInstance(mActivity);
		CookieSyncManager.getInstance().startSync();		
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mUsername = mPreferences.getString("username", "");

		if (savedInstanceState != null) {
			// Restore Current page.
			mThreadData.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 1));
			mThreadData.setListPosition(savedInstanceState.getInt(CURRENT_POSITION, 0));
		}

		if (!mActivity.getThreadLoggedIn()) {
			if (mUsername.length() >= 0) { // Always show forum

				LoginTask loginTask = new LoginTask();
				loginTask.addLoginListener(new LoginListener() {
					public void success() {
						MainActivity activity = (MainActivity) getActivity();
						if (activity != null) {
							activity.setThreadLoggedIn(true);

							ImageView loginStatusImage = (ImageView) activity.findViewById(R.id.thread_login_status_image);
							loginStatusImage.setImageResource(R.drawable.ic_online);

							TextView loginStatus = (TextView) activity.findViewById(R.id.thread_login_status);
							loginStatus.setText("Inloggad som " + mUsername);

							setHasOptionsMenu(true);

							fetchData();
						}
					}

					public void fail() {
						MainActivity activity = (MainActivity) getActivity();
						if (getActivity() != null) {
							activity.setThreadLoggedIn(false);

							ImageView LoginStatusImage = (ImageView) activity.findViewById(R.id.thread_login_status_image);
							LoginStatusImage.setImageResource(R.drawable.ic_offline);

							TextView loginStatus = (TextView) activity.findViewById(R.id.thread_login_status);
							loginStatus.setText("Ej inloggad");

							SharedPreferences.Editor editor = mPreferences.edit();
							editor.putString(COOKIE_NAME, "");
							editor.putString(COOKIE_VALUE, "");
							editor.apply();

							setHasOptionsMenu(true);

							fetchData();
						}
					}
				});
				loginTask.execute(mActivity);
			} else {
				// Empty username
			}
		} else {
			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putString(COOKIE_NAME, "");
			editor.putString(COOKIE_VALUE, "");
			editor.apply();
			
			setHasOptionsMenu(true);
			
			mActivity.setThreadLoggedIn(true);
			
			ImageView LoginStatusImage = (ImageView) mActivity.findViewById(R.id.thread_login_status_image);
			LoginStatusImage.setImageResource(R.drawable.ic_online);

			TextView LoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
			LoginStatus.setText("Inloggad som " + mUsername);

			fetchData();
		}

/*		
	    getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
	    	@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
	    		if ((mThreadData.mThreadsTask() != null) && (mThreadData.mThreadsTask().size() > 0)) {
	    			mToast.setText(mThreadData.mThreadsTask().get(position).getMessageText());
	                mToast.show();
	    		}
				return true;
			}
	    });		
*/

		mLoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
		mPageText = (TextView) mActivity.findViewById(R.id.thread_page_text);
		mCurrentPage = (TextView) mActivity.findViewById(R.id.thread_current_page);
		mByText = (TextView) mActivity.findViewById(R.id.thread_by_text);
		mMaxPages = (TextView) mActivity.findViewById(R.id.thread_no_of_pages);

	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENT_PAGE, mThreadData.getCurrentPage());
		outState.putInt(CURRENT_POSITION, mListView.getFirstVisiblePosition());
		super.onSaveInstanceState(outState);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.forum_menu, menu);
        menu.findItem(R.id.thread_new_thread).setVisible(mActivity.getThreadLoggedIn());

		super.onCreateOptionsMenu(menu, inflater);
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.thread_submenu:
                return true;
            case R.id.thread_left:
                previousPage();
                return true;
            case R.id.thread_right:
                nextPage();
                return true;
            case R.id.thread_markasread:
                markAsRead();
                return true;
            case R.id.thread_go_to_page:
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

                alert.setTitle(R.string.goto_page);
                alert.setMessage(getString(R.string.enter_page_number, mThreadData.getMaxPages()));


                // Set an EditText view to get user input
                final EditText input = new EditText(mActivity);
                alert.setView(input);

                alert.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do something with value!
                        if (HappyUtils.isInteger(input.getText().toString())) {
                            mThreadData.setCurrentPage(Integer.parseInt(input.getText().toString()));
                            fetchData();
                        }
                    }
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                final AlertDialog dialog = alert.create();

                input.addTextChangedListener(new PageTextWatcher(dialog, mThreadData.getMaxPages()));

                dialog.show();
                return true;
            case R.id.thread_new_thread:
                String url = "http://happymtb.org/forum/posting.php/1";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
		}
		return super.onOptionsItemSelected(item);
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

	public void markAsRead() {
        mSwipeRefreshLayout.setRefreshing(false);
        showProgress(true);

		mMarkAsRead = new MarkAsReadTask();
		mMarkAsRead.addMarkAsReadListener(new MarkAsReadListener() {
            public void success() {
                if (getActivity() != null) {
                    fetchData();
                }
            }

            public void fail() {
                showProgress(false);
            }
        });
		mMarkAsRead.execute(mActivity);
	}
	
	public void refreshList() {
		mThreadData.setListPosition(0);
		mThreadData.setCurrentPage(1);
		mActivity.setThreadDataItems(null);
		fetchData();
	}

	public void nextPage() {
		if (mThreadData.getCurrentPage() < mThreadData.getMaxPages()) {
			mThreadData.setListPosition(0);
			mActivity.setThreadDataItems(null);
			mThreadData.setCurrentPage(mThreadData.getCurrentPage() + 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners

			fetchData();
		}
	}

	public void previousPage() {
    	if (mThreadData.getCurrentPage() > 1) {
    		mThreadData.setListPosition(0);
    		mActivity.setThreadDataItems(null);
    		mThreadData.setCurrentPage(mThreadData.getCurrentPage() - 1);

            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
			fetchData();
    	}
	}
	
	public void fetchData() {
        showProgress(true);

		mThreadsTask = new ThreadListTask();
		mThreadsTask.addThreadListListener(new ThreadListListener() {
            public void success(List<Thread> threads) {
                if (getActivity() != null) {
                    mThreadData.setThreads(threads);
                    fillList();
                    showProgress(false);
                }
            }

            public void fail() {
				if (getActivity() != null) {
					Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
					showProgress(false);
				}
			}
        });
        mThreadsTask.execute(mThreadData.getCurrentPage(), mActivity);
	}

	private void fillList() {
		ListThreadsAdapter adapter = new ListThreadsAdapter(mActivity, mThreadData.getThreads());
		setListAdapter(adapter);

		mListView = getListView();
		
		mListView.setSelection(mThreadData.getListPosition());
		
		mCurrentPage.setText(Integer.toString(mThreadData.getCurrentPage()));					
		mMaxPages.setText(Integer.toString(mThreadData.getThreads().get(0).getNumberOfThreadPages()));
		mThreadData.setMaxPages(mThreadData.getThreads().get(0).getNumberOfThreadPages());						
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		openThread(position);
	}

	public void openThread(int position) {
		Intent Message = new Intent(mActivity, PostsActivity.class);
		Message.putExtra("ThreadId", mThreadData.getThreads().get(position).getThreadId());
		Message.putExtra("Logined", mActivity.getThreadLoggedIn());
		Message.putExtra("New", false);
		startActivity(Message);				
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_THREADS_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle(R.string.error_message);
			builder.setMessage(R.string.thread_download_error)
					.setPositiveButton(R.string.OK,
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
			builder.setTitle(R.string.error_message);
			builder.setMessage(
					R.string.thread_error_mark_as_read)
					.setPositiveButton(R.string.OK,
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
		if (mMarkAsRead != null) {
			mMarkAsRead.cancel(true);
		}

		if (mThreadsTask != null) {
			mThreadsTask.cancel(true);
		}	
	}
}
