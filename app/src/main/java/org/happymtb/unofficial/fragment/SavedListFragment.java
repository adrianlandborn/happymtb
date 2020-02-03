package org.happymtb.unofficial.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.database.MyContentProvider;
import org.happymtb.unofficial.database.MySQLiteHelper;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.volley.KosObjectRequest;
import org.happymtb.unofficial.volley.MyRequestQueue;

import static org.happymtb.unofficial.fragment.KoSListFragment.NO_IMAGE_URL;

public class SavedListFragment extends RefreshListfragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String TAG = "saved_frag";

    public final static int REQUEST_ITEM_MODIFIED 	= 1;
    public static final int RESULT_CANCELED    		= 0;
    public static final int RESULT_MODIFIED     	= -1;

    public final static long ONE_HOUR = 1000 * 60 * 60;
//	public final static long ONE_HOUR = 10 * 1000; //(10 sec)

    private static final int MENU_CONTEXT_DELETE_ID = 0;
    private final static String LAST_UPDATE = "saved_last_update";

	private SavedListCursorAdapter mAdapter;
	private MainActivity mActivity;
    private SharedPreferences mPreferences;

    private KoSItemDataSource mDataSource;
    private int mTotalRequests;
    private int mTotalResponses;

    /** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle(R.string.title_bar_saved);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        fillList();

        mDataSource = new KoSItemDataSource(mActivity);
        mDataSource.open();

        registerForContextMenu(getListView());
	}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saved_frame, container, false);
    }

    @Override
    protected void fetchData() {
        showProgress(false);
    }

    @Override
    protected void reloadCleanList() {
        showList(true);
        updateSoldItems();
    }

    @Override
    public void onResume() {
        mDataSource.open();
        super.onResume();
        if (!getLoaderManager().hasRunningLoaders() & shouldUpdateSoldItems()) {
            updateSoldItems();
        }
    }

    @Override
    public void onPause() {
        mDataSource.close();
        super.onPause();
    }

	private void fillList() {
        getLoaderManager().initLoader(0, null, this);
        mAdapter = new SavedListCursorAdapter(mActivity, null, 0);

        setListAdapter(mAdapter);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = mAdapter.getCursor();
        if (c.moveToPosition(position)) {
            KoSListItem item = KoSItemDataSource.getKoSItemFromCursor(c);
            Intent intent = new Intent(mActivity, KoSObjectActivity.class);
            intent.putExtra(KoSObjectActivity.URL, item.getLink());
            intent.putExtra(KoSObjectActivity.IMAGE_URL, !TextUtils.isEmpty(item.getImgLink()) ?
                    item.getImgLink() : NO_IMAGE_URL);
            intent.putExtra(KoSObjectActivity.AREA, item.getArea());
            intent.putExtra(KoSObjectActivity.TYPE, item.getType());
            intent.putExtra(KoSObjectActivity.TITLE, item.getTitle());
            intent.putExtra(KoSObjectActivity.DATE, item.getTime());
            intent.putExtra(KoSObjectActivity.PRICE, item.getPrice());
            intent.putExtra(KoSObjectActivity.CATEGORY, mAdapter.getItemColumn(position, MySQLiteHelper.COLUMN_CATEGORY));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && !TextUtils.isEmpty(item.getImgLink())
                    && !item.isSold()) {
                intent.putExtra(KoSObjectActivity.TRANSITION, true);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(mActivity, v, "image");
                startActivityForResult(intent, REQUEST_ITEM_MODIFIED, options.toBundle());
            } else {
                startActivityForResult(intent, REQUEST_ITEM_MODIFIED);
            }
        } else {
            Toast.makeText(mActivity, getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mAdapter.getItemColumn(info.position, MySQLiteHelper.COLUMN_TITLE));
        menu.add(Menu.NONE, MENU_CONTEXT_DELETE_ID, Menu.NONE, R.string.remove);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_CONTEXT_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int itemId = Integer.parseInt(mAdapter.getItemColumn(info.position, MySQLiteHelper.COLUMN_ID));

            boolean success = mDataSource.deleteItem(itemId);
            if (success) {
                getLoaderManager().restartLoader(0, null, this);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ITEM_MODIFIED) {
            if (resultCode == RESULT_MODIFIED) {
                // The item has been removed or changed
                getLoaderManager().restartLoader(0, null, this);
            } else if ( resultCode == RESULT_CANCELED){
                // Item unchanged
            }
        }
    }

    @Override
	public Loader onCreateLoader(int id, Bundle args) {
        String[] projection = MySQLiteHelper.ALL_COLUMNS;
        return new CursorLoader(mActivity,
                MyContentProvider.CONTENT_URI, projection, null, null, MySQLiteHelper.COLUMN_ID + " DESC");
	}

	@Override
	public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        showProgress(false);
        if (cursor == null || cursor.getCount() == 0) {
            mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
        } else if (shouldUpdateSoldItems()){
            showProgress(true);
            updateSoldItems();
        }
	}

	@Override
	public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
	}

	private boolean shouldUpdateSoldItems() {
        if (hasNetworkConnection() && mAdapter.getCursor() != null && mTotalRequests == mTotalResponses) {
            return System.currentTimeMillis() > (mPreferences.getLong(LAST_UPDATE, 0) + ONE_HOUR);
        }
        return false;
    }
	private void updateSoldItems() {
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null || (mTotalRequests != mTotalResponses)) {
            return;
        }
        KosObjectRequest request;
        String url;
        boolean isSold;

        mTotalRequests = 0;
        mTotalResponses = 0;

        // Count nbr of requests
        cursor.moveToFirst();
        do {
            isSold = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SOLD)) == 1;
            if (!isSold) {
                mTotalRequests++;
            }
        } while (cursor.moveToNext());

        cursor.moveToFirst();
        do {
            isSold = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SOLD)) == 1;
            if (!isSold) {
                final long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID));
                url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LINK));
                request = new KosObjectRequest(id, url, new Response.Listener<KoSObjectItem>() {
                    @Override
                    public void onResponse(KoSObjectItem item) {
                        mTotalResponses++;
                        mDataSource.updateKosItem(item);
                        checkProgressStatus();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTotalResponses++;
                        if (error != null && error.networkResponse.statusCode == MyRequestQueue.SC_NOT_FOUND) {
                            // TODO enable when tested
                            mDataSource.setItemSold(id, true);
                        }
                        checkProgressStatus();
                    }
                });
                MyRequestQueue.getInstance(getContext()).addRequest(request);
            }
        } while (cursor.moveToNext());
    }

    private void checkProgressStatus() {
        if (mTotalRequests == mTotalResponses) {
            // Update complete
            getLoaderManager().restartLoader(0, null, this);
            showProgress(false);

            // Save update time
            mPreferences.edit().putLong(LAST_UPDATE, System.currentTimeMillis()).apply();
        }
    }

    private void deleteSoldItems() {
        Cursor cursor = mAdapter.getCursor();
        if (cursor == null || (mTotalRequests != mTotalResponses)) {
            return;
        }
        boolean isSold;

        cursor.moveToFirst();
        do {
            isSold = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SOLD)) == 1;
            if (isSold) {
                mDataSource.deleteItem(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            }
        } while (cursor.moveToNext());

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.saved_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saved_delete_all) {
            showDeleteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
        alert.setTitle(R.string.remove);
        alert.setMessage("Ta bort alla sålda annonser?");
        alert.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                deleteSoldItems();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
    }

    public class SavedListCursorAdapter extends CursorAdapter {

        private final LayoutInflater inflater;

        private TextView mTitle;
        private TextView mTime;
        private TextView mArea;
        private View mRowColor;
        private TextView mCategory;
        private TextView mPrice;
        private ImageView mObjectImageView;
        View mKosSoldView;

        // Default constructor
        public SavedListCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);

            inflater = LayoutInflater.from(context);
            //TODO ViewHolder pattern
        }

        public void bindView(final View view, Context context, Cursor cursor) {

            mRowColor = view.findViewById(R.id.kos_picture_row_color);

            mTitle = view.findViewById(R.id.kos_picture_row_title);
            mTime = view.findViewById(R.id.kos_picture_row_time);
            mArea = view.findViewById(R.id.kos_picture_row_area);
            mCategory = view.findViewById(R.id.kos_picture_row_category);
            mPrice = view.findViewById(R.id.kos_picture_row_price);
            mObjectImageView = view.findViewById(R.id.kos_picture_row_image);
            mKosSoldView = view.findViewById(R.id.kos_sold);

            mTitle.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE)));
            mArea.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AREA)));

            mCategory.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY)));
            mTime.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TIME)));
            mPrice.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PRICE)));

            mObjectImageView.setVisibility(View.VISIBLE);
            mKosSoldView.setVisibility(View.INVISIBLE);

            boolean isSold = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SOLD)) == 1;

            String imageUrl = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE_LINK));
            imageUrl = imageUrl.replace("http://", "https://");
            if (!TextUtils.isEmpty(imageUrl)) {
                imageUrl = imageUrl.replace("large.jpg", "medium.jpg");
                Picasso.with(context).load(imageUrl).into(mObjectImageView);
            } else {
                mObjectImageView.setImageResource(R.drawable.no_photo);
                mObjectImageView.setVisibility(View.VISIBLE);
            }

            if (isSold) {
                mObjectImageView.setAlpha(0.3f);
                mKosSoldView.setVisibility(View.VISIBLE);
            } else {
                mObjectImageView.setAlpha(1.0f);
                mKosSoldView.setVisibility(View.INVISIBLE);
            }

            if (cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TYPE)).equals(KoSListItem.TYPE_SALJES)) {
                mRowColor.setBackgroundResource(R.color.kos_green);
            } else {
                mRowColor.setBackgroundResource(R.color.kos_red);
            }
        }

        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            // R.layout.list_row is your xml layout for each row
            return inflater.inflate(R.layout.kos_row, parent, false);
        }

        public String getItemColumn(int position, String column) {
            Cursor cursor = getCursor();
            String value = "";
            if(cursor.moveToPosition(position)) {
                value = cursor.getString(cursor.getColumnIndex(column));
            }
            return value;
        }
    }
}