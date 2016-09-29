package org.happymtb.unofficial.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.database.MyContentProvider;
import org.happymtb.unofficial.database.MySQLiteHelper;

public class SavedListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String TAG = "saved_frag";

    public final static int REQUEST_ITEM_MODIFIED 	= 1;
    public static final int RESULT_CANCELED    		= 0;
    public static final int RESULT_MODIFIED     	= -1;

    private static final int MENU_CONTEXT_DELETE_ID = 0;

    private Tracker mTracker;

	private KosItemCursorAdapter mAdapter;
//	private KoSData mKoSData;
	private SharedPreferences mPreferences;
	private MainActivity mActivity;

    private View mProgressView;

    private KoSItemDataSource datasource;
//    ArrayAdapter<KoSItem> mAdapter;

    /** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle("Sparade annonser");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        mProgressView = getActivity().findViewById(R.id.progress_container_id);

        fillList();

        datasource = new KoSItemDataSource(mActivity);
        datasource.open();

        registerForContextMenu(getListView());
	}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        HappyApplication application = (HappyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        // [START Google analytics screen]
        mTracker.setScreenName(GaConstants.Categories.SAVED_LIST);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        // [END Google analytics screen]

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saved_frame, container, false);
    }

    @Override
    public void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }

	private void fillList() {

        getLoaderManager().initLoader(0, null, this);
        mAdapter = new KosItemCursorAdapter(mActivity, null, 0);

        setListAdapter(mAdapter);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent koSObject = new Intent(mActivity, KoSObjectActivity.class);
		koSObject.putExtra(KoSObjectActivity.URL, mAdapter.getItemColumn(position, MySQLiteHelper.COLUMN_LINK));
		koSObject.putExtra(KoSObjectActivity.CATEGORY, mAdapter.getItemColumn(position, MySQLiteHelper.COLUMN_CATEGORY));
		startActivityForResult(koSObject, REQUEST_ITEM_MODIFIED);
	}

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(mAdapter.getItemColumn(info.position, MySQLiteHelper.COLUMN_TITLE));
        menu.add(Menu.NONE, MENU_CONTEXT_DELETE_ID, Menu.NONE, R.string.remove);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CONTEXT_DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int itemId = Integer.parseInt(mAdapter.getItemColumn(info.position, MySQLiteHelper.COLUMN_ID));

                KoSItemDataSource dataSource = new KoSItemDataSource(mActivity);
                dataSource.open();

                boolean success = dataSource.deleteItem(itemId);
                dataSource.close();
                if (success) {
                    getLoaderManager().restartLoader(0, null, this);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
        CursorLoader cursorLoader = new CursorLoader(mActivity,
                MyContentProvider.CONTENT_URI, projection, null, null, MySQLiteHelper.COLUMN_ID + " DESC");
        return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        if (cursor == null || cursor.getCount() == 0) {
            mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
        }
	}

	@Override
	public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
	}

    public class KosItemCursorAdapter extends CursorAdapter {

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
        public KosItemCursorAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);

            inflater = LayoutInflater.from(context);
            //TODO ViewHolder pattern
        }

        public void bindView(final View view, Context context, Cursor cursor) {

            mRowColor = view.findViewById(R.id.kos_picture_row_color);

            mTitle = (TextView) view.findViewById(R.id.kos_picture_row_title);
            mTime = (TextView) view.findViewById(R.id.kos_picture_row_time);
            mArea = (TextView) view.findViewById(R.id.kos_picture_row_area);
            mCategory = (TextView) view.findViewById(R.id.kos_picture_row_category);
            mPrice = (TextView) view.findViewById(R.id.kos_picture_row_price);
            mObjectImageView = (ImageView) view.findViewById(R.id.kos_picture_row_image);
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