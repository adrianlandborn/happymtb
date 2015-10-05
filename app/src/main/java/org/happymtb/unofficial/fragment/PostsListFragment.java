package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import org.happymtb.unofficial.PostsActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListMessagesAdapter;
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
    private final static String BASE_URL = "http://happymtb.org/forum/read.php/1/";

    public static String TAG = "posts_tag";

    private MessageListTask mMessageListTask;
    private MessageData mMessageData;
    private ListMessagesAdapter mMessageAdapter;
    private SharedPreferences mPreferences;
    private String mUsername = "";
    private PostsActivity mActivity;
    private ListView mListView;
    private TextView mLoginStatus;
    private TextView mPageText;
    private TextView mCurrentPage;
    private TextView mByText;
    private TextView mMaxPages;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = ((PostsActivity) getActivity());

        setHasOptionsMenu(true);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        CookieSyncManager.createInstance(mActivity);
        CookieSyncManager.getInstance().startSync();


        mProgressView = mActivity.findViewById(R.id.progress_container_id);

        mMessageData = mActivity.GetMessageData();

        if (savedInstanceState != null) {
            // Restore Current page.
            mMessageData.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 1));
            mMessageData.setListPosition(savedInstanceState.getInt(CURRENT_POSITION, 0));
        }

        fetchData();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mUsername = mPreferences.getString("username", "");

        mLoginStatus = (TextView) mActivity.findViewById(R.id.message_login_status);
        mPageText = (TextView) mActivity.findViewById(R.id.message_page_text);
        mCurrentPage = (TextView) mActivity.findViewById(R.id.message_current_page);
        mByText = (TextView) mActivity.findViewById(R.id.message_by_text);
        mMaxPages = (TextView) mActivity.findViewById(R.id.message_no_of_pages);

        ImageView loginStatusImage = (ImageView) mActivity.findViewById(R.id.message_login_status_image);

        if (mMessageData.getLogined()) {
            loginStatusImage.setImageResource(R.drawable.ic_online);
            mLoginStatus.setText(mActivity.getString(R.string.logged_in_as) + mUsername);
        } else {
            loginStatusImage.setImageResource(R.drawable.ic_offline);
            mLoginStatus.setText(R.string.not_logged_in);
        }

        clearBitmapDir();
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.message_frame, container, false);
//    }

    public void clearBitmapDir() {
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGE, mMessageData.getCurrentPage());
        if (mListView != null) {
            outState.putInt(CURRENT_POSITION, mListView.getFirstVisiblePosition());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.posts_menu, menu);
        menu.findItem(R.id.message_new_post).setVisible(mMessageData.getLogined());
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

                alert.setTitle("Gå till sidan...");
                alert.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mMessageData.getMaxPages() + ")");

                // Set an EditText view to get user input
                final EditText input = new EditText(mActivity);
                alert.setView(input);

                alert.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do something with value!
                        if (HappyUtils.isInteger(input.getText().toString())) {
                            mMessageData.setListPosition(0);
                            mActivity.SetMessageDataItems(null);
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
            case R.id.message_new_post:
                String url = BASE_URL + mMessageData.getThreadId() + "/#REPLY";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void refreshList() {
        mMessageData.setListPosition(0);
        mActivity.SetMessageDataItems(null);
        fetchData();
    }

    public void nextPage() {
        if (mMessageData.getCurrentPage() < mMessageData.getMaxPages()) {
            mMessageData.setListPosition(0);
            mActivity.SetMessageDataItems(null);
            mMessageData.setCurrentPage(mMessageData.getCurrentPage() + 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
            fetchData();
        }
    }

    public void previousPage() {
        if (mMessageData.getCurrentPage() > 1) {
            mMessageData.setListPosition(0);
            mActivity.SetMessageDataItems(null);
            mMessageData.setCurrentPage(mMessageData.getCurrentPage() - 1);
            mSwipeRefreshLayout.setRefreshing(false); // To not show multiple loading spinners
            fetchData();
        }
    }

    private void fetchData() {
        showProgress(true);

        mMessageData = mActivity.GetMessageData();
        if ((mMessageData.getMessages() != null) && (mMessageData.getMessages().size() > 0)) {
            fillList();

            showProgress(false);
        } else {
            mMessageListTask = new MessageListTask();
            mMessageListTask.addMessageListListener(new MessageListListener() {
                    public void success(List<Message> messages) {
                        if (getActivity() != null) {
                            mMessageData.setMessages(messages);
                            fillList();

                            showProgress(false);
                        }
                    }

                    public void fail() {

                        if (getActivity() != null) {
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
        mMessageAdapter = new ListMessagesAdapter(mActivity, mMessageData.getMessages());
        setListAdapter(mMessageAdapter);

        new MessageImageDownloadTask().execute(mMessageData.getMessages(), mMessageAdapter, mActivity);

        mListView = getListView();
        mListView.setSelection(mMessageData.getListPosition());

        mCurrentPage.setText(Integer.toString(mMessageData.getCurrentPage()));
        mMaxPages.setText(Integer.toString(mMessageData.getMessages().get(0).getNumberOfMessagePages()));
        mMessageData.setMaxPages(mMessageData.getMessages().get(0).getNumberOfMessagePages());
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mMessageListTask != null) {
            mMessageListTask.cancel(true);
        }
    }
}
