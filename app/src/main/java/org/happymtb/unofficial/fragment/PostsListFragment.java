package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.happymtb.unofficial.PostsActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.MessagesAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Message;
import org.happymtb.unofficial.item.MessageData;
import org.happymtb.unofficial.listener.MessageListListener;
import org.happymtb.unofficial.listener.PageTextWatcher;
import org.happymtb.unofficial.task.MessageImageDownloadTask;
import org.happymtb.unofficial.task.MessageListTask;

import java.io.File;
import java.util.List;

public class PostsListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener {
    private static final String BASE_URL = "https://happyride.se/forum/read.php/1/";
    public static final String TAG = "posts_tag";

    private MessageListTask mMessageListTask;
    private MessageData mMessageData;
    private MessagesAdapter mMessageAdapter;
    private SharedPreferences mPreferences;
    private String mUsername;
    private PostsActivity mActivity;
    private ListView mListView;

    private TextView mCurrentPage;
    private TextView mMaxPages;
    private FloatingActionButton mReplyFab;


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = ((PostsActivity) getActivity());
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        CookieSyncManager.createInstance(mActivity);
        CookieSyncManager.getInstance().startSync();

        mUsername = mPreferences.getString("username", "");

        mCurrentPage = mActivity.findViewById(R.id.message_current_page);
        mMaxPages = mActivity.findViewById(R.id.message_no_of_pages);
        mReplyFab = mActivity.findViewById(R.id.reply);

        Bundle bundle = mActivity.getIntent().getExtras();
        boolean loggedIn = bundle.getBoolean(PostsActivity.LOGGED_IN);
        if (loggedIn) {
            mReplyFab.setVisibility(View.VISIBLE);
            mReplyFab.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 reply();
                                             }
                                         }
            );
        }

        if (savedInstanceState != null && savedInstanceState.getSerializable(DATA) != null) {
            // Restore Current page.
            mMessageData = (MessageData) savedInstanceState.getSerializable(DATA);

            fillList();

            showProgress(false);
        } else {

            int page = bundle.getInt(PostsActivity.PAGE);
            String threadId = bundle.getString(PostsActivity.THREAD_ID);
            mMessageData = new MessageData(page, 1, loggedIn, threadId, null, 0);

            clearBitmapDir();

            fetchData();
        }

        ImageView loginStatusImage = mActivity.findViewById(R.id.message_login_status_image);
        TextView loginStatus = mActivity.findViewById(R.id.message_login_status);
        if (mMessageData.getLogined()) {
            loginStatusImage.setImageResource(R.drawable.ic_online);
            loginStatus.setText(mActivity.getString(R.string.logged_in_as) + mUsername);
        } else {
            loginStatusImage.setImageResource(R.drawable.ic_offline);
            loginStatus.setText(R.string.not_logged_in);
        }
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.posts_frame, container, false);
//    }

    public void clearBitmapDir() {
        final String PATH = "/mnt/sdcard/happymtb/";
        File dir = new File(PATH);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                new File(dir, child).delete();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        outState.putInt(CURRENT_PAGE, mMessageData.getCurrentPage());
        if (mListView != null) {
            mMessageData.setListPosition(mListView.getFirstVisiblePosition());
            outState.putSerializable(DATA, mMessageData);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.posts_menu, menu);

        menu.findItem(R.id.message_left).setVisible(mMessageData.getCurrentPage() > 1);
        menu.findItem(R.id.message_right).setVisible(mMessageData.getCurrentPage() < mMessageData.getMaxPages());
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
            case R.id.message_right:
                nextPage();
                return true;
            case R.id.message_go_to_page:
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

                alert.setTitle(R.string.goto_page);
                alert.setMessage(getString(R.string.enter_page_number, mMessageData.getMaxPages()));

                // Set an EditText view to get user input
                final EditText input = new EditText(mActivity);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                alert.setView(input);

                alert.setPositiveButton(R.string.open, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do something with value!
                        if (HappyUtils.isInteger(input.getText().toString())) {
                            mMessageAdapter = null;
                            mMessageData.setMessages(null);
                            mMessageData.setCurrentPage(Integer.parseInt(input.getText().toString()));
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

                input.addTextChangedListener(new PageTextWatcher(dialog, mMessageData.getMaxPages()));

                dialog.show();
                return true;
//            case R.id.message_new_post:
//                reply();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reply() {
        String url = BASE_URL + mMessageData.getThreadId() + "/#REPLY";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // TODO Setup options for "Citera"
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Handle options for "Citera"
//        switch (item.getItemId()) {
//            case R.id.message_submenu:
//                return true;
        return super.onContextItemSelected(item);


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

    public void reloadCleanList() {
        mMessageAdapter = null;
        mMessageData.setMessages(null);
        fetchData();
    }

    public void nextPage() {
        if (mMessageData.getCurrentPage() < mMessageData.getMaxPages()) {
            mMessageAdapter = null;
            mMessageData.setMessages(null);
            mMessageData.setCurrentPage(mMessageData.getCurrentPage() + 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
            fetchData();
        }
    }

    public void previousPage() {
        if (mMessageData.getCurrentPage() > 1) {
            mMessageAdapter = null;
            mMessageData.setMessages(null);
            mMessageData.setCurrentPage(mMessageData.getCurrentPage() - 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
            fetchData();
        }
    }

    @Override
    protected void fetchData() {
        showProgress(true);

        if ((mMessageData.getMessages() != null) && (mMessageData.getMessages().size() > 0)) {
            fillList();

            showProgress(false);
        } else {
            mMessageListTask = new MessageListTask();
            mMessageListTask.addMessageListListener(new MessageListListener() {
                    public void success(List<Message> messages) {
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            mMessageData.setMessages(messages);
                            fillList();

                            showProgress(false);
                        }
                    }

                    public void fail() {

                        if (getActivity() != null && !getActivity().isFinishing()) {
                            Toast.makeText(mActivity, mActivity.getString(R.string.messages_not_found), Toast.LENGTH_LONG).show();

                            showProgress(false);
                        }
                    }
                }

            );
            mMessageListTask.execute(mActivity, mMessageData.getThreadId(), Integer.toString(mMessageData.getCurrentPage()));
        }
    }

    private void fillList() {
        if (mMessageAdapter == null) {
            mMessageAdapter = new MessagesAdapter(mActivity, mMessageData.getMessages());
            setListAdapter(mMessageAdapter);
        } else {
            mMessageAdapter.setItems(mMessageData.getMessages());
            mMessageAdapter.notifyDataSetChanged();
        }

        new MessageImageDownloadTask().execute(mMessageData.getMessages(), mMessageAdapter, mActivity);

        mListView = getListView();
        mListView.setSelection(mMessageData.getListPosition());

        mCurrentPage.setText(Integer.toString(mMessageData.getCurrentPage()));
        mMaxPages.setText(Integer.toString(mMessageData.getMessages().get(0).getNumberOfMessagePages()));
        mMessageData.setMaxPages(mMessageData.getMessages().get(0).getNumberOfMessagePages());

        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mMessageListTask != null) {
            mMessageListTask.cancel(true);
        }
    }
}
