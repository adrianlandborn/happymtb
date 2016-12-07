package org.happymtb.unofficial.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.widget.Toast;

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
import org.happymtb.unofficial.item.KoSListItem;

import static org.happymtb.unofficial.fragment.KoSListFragment.NO_IMAGE_URL;

public class SavedListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String TAG = "saved_frag";

    public final static int REQUEST_ITEM_MODIFIED 	= 1;
    public static final int RESULT_CANCELED    		= 0;
    public static final int RESULT_MODIFIED     	= -1;

    private static final int MENU_CONTEXT_DELETE_ID = 0;

    private Tracker mTracker;

	private SavedListCursorAdapter mAdapter;
	private MainActivity mActivity;

    private KoSItemDataSource datasource;

    /** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle(getString(R.string.title_bar_saved));

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
                        makeSceneTransitionAnimation(mActivity, (View) v, "image");
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