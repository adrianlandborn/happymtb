package org.happymtb.unofficial.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.database.KoSItemDataSource;
import org.happymtb.unofficial.database.MyContentProvider;
import org.happymtb.unofficial.database.MySQLiteHelper;

public class SavedListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static String TAG = "saved_frag";

    public final static int REQUEST_ITEM_MODIFIED 	= 1;
    public static final int RESULT_CANCELED    		= 0;
    public static final int RESULT_MODIFIED     	= -1;

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
                MyContentProvider.CONTENT_URI, projection, null, null, null);
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

            if (!isSold) {
                String imageUrl = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE_LINK));
                if (!TextUtils.isEmpty(imageUrl)) {
                    imageUrl = imageUrl.replace("large.jpg", "medium.jpg");
                    Picasso.with(context).load(imageUrl).into(mObjectImageView);
                } else {
                    mObjectImageView.setVisibility(View.VISIBLE);
                    mKosSoldView.setVisibility(View.INVISIBLE);
                    mObjectImageView.setImageResource(R.drawable.no_photo);
                }
            } else {
                mObjectImageView.setVisibility(View.INVISIBLE);
                mKosSoldView.setVisibility(View.VISIBLE);
            }
        }

//        , new Callback() {
//            @Override
//            public void onSuccess() {
//                mObjectImageView.setVisibility(View.VISIBLE);
//                mKosSoldView.setVisibility(View.INVISIBLE);
//                System.out.println("Callback: OK" );
//            }
//
//            @Override
//            public void onError() {
//                System.out.println("Callback: Fail" );
//                mObjectImageView.setVisibility(View.INVISIBLE);
//                mKosSoldView.setVisibility(View.VISIBLE);
//            }
//        }

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