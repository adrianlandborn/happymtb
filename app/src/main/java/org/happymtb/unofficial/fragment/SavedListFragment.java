package org.happymtb.unofficial.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.database.MyContentProvider;
import org.happymtb.unofficial.database.MySQLiteHelper;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.listener.KoSObjectListener;
import org.happymtb.unofficial.task.KoSObjectTask;

import java.util.ArrayList;
import java.util.List;

public class SavedListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String TAG = "saved_frag";

    public final static int REQUEST_ITEM_MODIFIED 	= 1;
    public static final int RESULT_CANCELED    		= 0;
    public static final int RESULT_MODIFIED     	= -1;

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

//        mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
//        if (savedInstanceState != null) {
//			// Restore Current page.
//            mKoSData = (KoSData) savedInstanceState.getSerializable(DATA);
//
//            fillList();
//            showProgress(false);
//		} else {
//            fetchData();
//        }

        fillList();

        datasource = new KoSItemDataSource(mActivity);
        datasource.open();
	}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain the shared Tracker instance.
        HappyApplication application = (HappyApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        // [START Google analytics screen]
        mTracker.setScreenName(GaConstants.Screens.SAVED_LIST);
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

    //	@Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (mKoSData != null) {
//            outState.putSerializable(DATA, mKoSData);
//        }
//    }

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		menu.clear();
//		inflater.inflate(R.menu.kos_menu, menu);
//
//		// Dont show left arrow for first page
//		menu.findItem(R.id.kos_left).setVisible(mKoSData.getCurrentPage() > 0);
//		super.onCreateOptionsMenu(menu, inflater);
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.kos_submenu:
//			return true;
//		case R.id.kos_left:
//			previousPage();
//			return true;
//		case R.id.kos_right:
//			nextPage();
//			return true;
//		case R.id.kos_sort:
//			fragmentManager = mActivity.getSupportFragmentManager();
//	        KoSSortDialogFragment koSSortDialog = new KoSSortDialogFragment();
//	        koSSortDialog.show(fragmentManager, "kos_sort_dialog");
//			return true;
//		case R.id.kos_search:
//			fragmentManager = mActivity.getSupportFragmentManager();
//			KoSSearchDialogFragment koSSearchDialog = new KoSSearchDialogFragment();
//	        koSSearchDialog.show(fragmentManager, "kos_search_dialog");
//			return true;
//		case R.id.kos_go_to_page:
//			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
//
//			alert.setTitle(R.string.goto_page);
//			alert.setMessage(getString(R.string.enter_page_number, mKoSData.getMaxPages()));
//
//			// Set an EditText view to get user input
//			final EditText input = new EditText(mActivity);
//			alert.setView(input);
//			alert.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					// Do something with value!
//					if (HappyUtils.isInteger(input.getText().toString())) {
//						mKoSData.setCurrentPage(Integer.parseInt(input.getText().toString()));
//						fetchData();
//					}
//				}
//			});
//
//			alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton) {
//					// Canceled.
//				}
//			});
//			final AlertDialog dialog = alert.create();
//
//			input.addTextChangedListener(new PageTextWatcher(dialog, mKoSData.getMaxPages()));
//
//			dialog.show();
//			return true;
//		case R.id.kos_new_item:
//			String url = "http://happymtb.org/annonser/index.php?page=add";
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//			startActivity(browserIntent);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

//	private void fetchData() {
//        showProgress(true);
//		mKoSTask = new KoSListTask();
//		mKoSTask.addKoSListListener(new KoSListListener() {
//            public void success(List<KoSItem> KoSItems) {
//                if (getActivity() != null && !getActivity().isFinishing()) {
//                    mKoSData.setKoSItems(KoSItems);
//                    fillList();
//                }
//                showProgress(false);
//            }
//
//            public void fail() {
//                if (getActivity() != null && !getActivity().isFinishing()) {
//                    Toast.makeText(mActivity, mActivity.getString(R.string.kos_no_items_found), Toast.LENGTH_LONG).show();
//
//                    showProgress(false);
//                }
//            }
//        });
//		mKoSTask.execute(mKoSData.getCurrentPage() - 1, mKoSData.getType(), mKoSData.getRegion(), mKoSData.getCategory(),
//                mKoSData.getSearch(), mKoSData.getSortAttributeServer(), mKoSData.getSortOrderServer());
//	}

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

//    private class CheckSoldItemsTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            KoSItemDataSource datasource = new KoSItemDataSource(mActivity);
//            datasource.open();
//
//            List<KoSListItem> items = datasource.getAllKoSItems();
//
//
//            return null;
//        }
//
//        private void fetchKoSObject(String objectLink) {
//            mProgressView.setVisibility(View.VISIBLE);
//
//            mKoSObjectTask = new KoSObjectTask();
//            mKoSObjectTask.addKoSObjectListener(new KoSObjectListener() {
//                public void success(KoSObjectItem koSObjectItem) {
//                    mKoSObjectItem = koSObjectItem;
//                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        if (mKoSObjectItem != null) {
//                            if (!mKoSObjectItem.getDate().equals("1970-01-01 01:00")) {
//                                fillList();
//                                //TODO update in database
//                                if (mIsSaved) {
//                                    updateInDatabase();
//                                }
//                            } else {
//                                mScrollView.setVisibility(View.INVISIBLE);
//                                mActivity.findViewById(R.id.no_content).setVisibility(View.VISIBLE);
//
//                                mIsSold = true;
//                                if (mIsSaved) {
//                                    datasource.setItemSold(mActivity.getObjectId(), true);
//                                    mActivity.setResult(SavedListFragment.RESULT_MODIFIED, null);
//                                }
//                                mActivity.invalidateOptionsMenu();
//                            }
//                        }
//                    } else {
//                        // Something went wrong
//                    }
//                    mProgressView.setVisibility(View.INVISIBLE);
//                }
//
//                public void fail() {
//                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
//                        mProgressView.setVisibility(View.INVISIBLE);
//                    }
//                }
//            });
//
//            mKoSObjectTask.execute(objectLink);
//    }
}