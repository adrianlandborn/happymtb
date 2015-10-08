package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

public class ForumListFragment extends RefreshListfragment {
	public final static String COOKIE_NAME = "cookiename";
	public final static String COOKIE_VALUE = "cookivalue";
	public static String TAG = "forum_frag";

	private ThreadListTask mThreadsTask;
	private ThreadData mThreadData;
	private MarkAsReadTask mMarkAsRead;
	private SharedPreferences mPreferences;
	private String mUsername = "";
	private MainActivity mActivity;
	private ListView mListView;
	ImageView mLoginStatusImage;
	private TextView mLoginStatus;
	private TextView mCurrentPage;
	private TextView mMaxPages;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.thread_frame, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mActivity = (MainActivity) getActivity();

		CookieSyncManager.createInstance(mActivity);
		CookieSyncManager.getInstance().startSync();		
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		mUsername = mPreferences.getString("username", "");

		mLoginStatusImage = (ImageView) mActivity.findViewById(R.id.thread_login_status_image);
		mLoginStatus = (TextView) mActivity.findViewById(R.id.thread_login_status);
		mCurrentPage = (TextView) mActivity.findViewById(R.id.thread_current_page);
		mMaxPages = (TextView) mActivity.findViewById(R.id.thread_no_of_pages);

        mThreadData = new ThreadData();

		if (savedInstanceState != null) {
			// Restore Current page.
			mThreadData.setThreads((ArrayList<Thread>) savedInstanceState.getSerializable(DATA));
			mThreadData.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 1));
			mThreadData.setLoggedIn(savedInstanceState.getBoolean(LOGGED_IN));

			setLogin(savedInstanceState.getBoolean(LOGGED_IN));
			fillList();
			showProgress(false);
		} else if (mActivity.isLoggedIn()) {
			setLogin(true);
			fetchData();

		} else if(!TextUtils.isEmpty(mUsername)){
			startLogin();
		} else {
			setLogin(false);
			fetchData();
		}
	}

	private void setLogin(boolean login) {
        mActivity.setLoggedIn(login);
		mThreadData.setLoggedIn(login);

		if (login) {
			mLoginStatusImage.setImageResource(R.drawable.ic_online);
			mLoginStatus.setText("Inloggad som " + mUsername);
		} else {
			mLoginStatusImage.setImageResource(R.drawable.ic_offline);
			mLoginStatus.setText("Ej inloggad");

			SharedPreferences.Editor editor = mPreferences.edit();
			editor.putString(COOKIE_NAME, "");
			editor.putString(COOKIE_VALUE, "");
			editor.apply();
		}
		setHasOptionsMenu(true);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (mListView != null && mThreadData != null) {
			mThreadData.setListPosition(mListView.getFirstVisiblePosition());
			outState.putInt(CURRENT_PAGE, mThreadData.getCurrentPage());
			outState.putSerializable(DATA, (ArrayList<Thread>) mThreadData.getThreads());
			outState.putBoolean(LOGGED_IN, mThreadData.getLoggedIn());
		}

		super.onSaveInstanceState(outState);
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.forum_menu, menu);
        menu.findItem(R.id.thread_new_thread).setVisible(mThreadData.getLoggedIn());

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

	private void startLogin() {
		LoginTask loginTask = new LoginTask();
		loginTask.addLoginListener(new LoginListener() {
			public void success() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					setLogin(true);
					fetchData();
				}
			}

			public void fail() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					setLogin(false);
					fetchData();
				}
			}
		});
		loginTask.execute(mActivity);
	}

	public void markAsRead() {
        mSwipeRefreshLayout.setRefreshing(false);
        showProgress(true);

		mMarkAsRead = new MarkAsReadTask();
		mMarkAsRead.addMarkAsReadListener(new MarkAsReadListener() {
            public void success() {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    fetchData();
                }
            }

            public void fail() {
                showProgress(false);
                Toast.makeText(getActivity(), getString(R.string.thread_error_mark_as_read), Toast.LENGTH_SHORT).show();
            }
        });
		mMarkAsRead.execute(mActivity);
	}
	
	public void refreshList() {
		mThreadData.setListPosition(0);
		mThreadData.setCurrentPage(1);
		fetchData();
	}

	public void nextPage() {
		if (mThreadData.getCurrentPage() < mThreadData.getMaxPages()) {
			mThreadData.setListPosition(0);
			mThreadData.setCurrentPage(mThreadData.getCurrentPage() + 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners

			fetchData();
		}
	}

	public void previousPage() {
    	if (mThreadData.getCurrentPage() > 1) {
    		mThreadData.setListPosition(0);
    		mThreadData.setCurrentPage(mThreadData.getCurrentPage() - 1);

            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
			fetchData();
    	}
	}
	
	public void fetchData() {
        showProgress(true);

        mThreadData.setThreads(null);

		mThreadsTask = new ThreadListTask();
		mThreadsTask.addThreadListListener(new ThreadListListener() {
            public void success(List<Thread> threads) {
                if (getActivity() != null  && !getActivity().isFinishing()) {
                    mThreadData.setThreads(threads);
                    fillList();
                    showProgress(false);
                }
            }

            public void fail() {
				if (getActivity() != null  && !getActivity().isFinishing()) {
					Toast.makeText(getActivity(), R.string.thread_download_error, Toast.LENGTH_SHORT).show();
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
		Intent postActivity = new Intent(mActivity, PostsActivity.class);
		postActivity.putExtra("ThreadId", mThreadData.getThreads().get(position).getThreadId());
		postActivity.putExtra("Logined", mThreadData.getLoggedIn());
		postActivity.putExtra("New", false);
		startActivity(postActivity);
	}
	
}
