package org.happymtb.unofficial.fragment;

import java.util.List;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListKoSAdapter;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSData;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.listener.KoSListListener;
import org.happymtb.unofficial.listener.PageTextWatcher;
import org.happymtb.unofficial.task.KoSListTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.text.InputType;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class KoSListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener,
		MainActivity.SortListener, MainActivity.SearchListener, GestureDetector.OnGestureListener {
    public static String TAG = "kos_frag";

	public final static String SORT_ORDER_POS = "sort_order_pos";
	public final static String SORT_ORDER_SERVER = "sort_order";
	public final static String SORT_ATTRIBUTE_POS = "sort_attribute_pos";
	public final static String SORT_ATTRIBUTE_SERVER = "sort_attribute";

    public final static String SEARCH_TEXT = "search_text";
    public final static String SEARCH_CATEGORY = "search_category";
    public final static String SEARCH_CATEGORY_POS = "search_category_pos";
    public final static String SEARCH_REGION = "search_region";
    public final static String SEARCH_REGION_POS = "search_region_pos";
    public final static String SEARCH_TYPE_POS = "search_type";

    public final static String SEARCH_TYPE_SPINNER = "search_type_spinner";
    public final static String SEARCH_REGION_SPINNER = "search_region_spinner";
    public final static String SEARCH_CATEGORY_SPINNER = "search_category_spinner";

	private Tracker mTracker;

	private KoSListTask mKoSTask;
	private ListKoSAdapter mKoSAdapter;
	private KoSData mKoSData;
	private SharedPreferences mPreferences;
	private MainActivity mActivity;
	private FragmentManager fragmentManager;

    protected GestureDetectorCompat mDetector;


    /** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        mActivity = (MainActivity) getActivity();
		mActivity.getSupportActionBar().setTitle("Annonser");

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);

        if (savedInstanceState != null) {
			// Restore Current page.
            mKoSData = (KoSData) savedInstanceState.getSerializable(DATA);

            fillList();
            showProgress(false);
		} else {
            mKoSData = new KoSData(
                    mPreferences.getString(SORT_ATTRIBUTE_SERVER, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                    mPreferences.getString(SORT_ORDER_SERVER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0),
                    mPreferences.getInt(SEARCH_TYPE_POS, KoSData.ALLA),
                    mPreferences.getInt(SEARCH_REGION_POS, 0), mPreferences.getString(SEARCH_REGION, "Hela Sverige"),
                    mPreferences.getInt(SEARCH_CATEGORY_POS, 0), mPreferences.getString(SEARCH_CATEGORY, "Alla Kategorier"),
                    mPreferences.getString(SEARCH_TEXT, ""));
            fetchData();
        }

        mDetector = new GestureDetectorCompat(getActivity(), this);
        getListView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean consumed = mDetector.onTouchEvent(event);
                // Be sure to call the superclass implementation

                if (consumed) {
                    return true;
                } else {
                    return getActivity().onTouchEvent(event);
                }
            }
        });

        mActivity.findViewById(R.id.kos_bottombar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = mActivity.getSupportFragmentManager();
                KoSSearchDialogFragment koSSearchDialog = new KoSSearchDialogFragment();
                koSSearchDialog.show(fragmentManager, "kos_search_dialog");
            }
        });

	}

	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Obtain the shared Tracker instance.
		HappyApplication application = (HappyApplication) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

		// [START Google analytics screen]
		mTracker.setScreenName(GaConstants.Categories.KOS_LIST);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		// [END Google analytics screen]

	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kos_frame, container, false);
    }

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mKoSData != null) {
            outState.putSerializable(DATA, mKoSData);
        }
    }

    @Override
    public void onAttach(Activity activity) {
		super.onAttach(activity);
        ((MainActivity) getActivity()).addSortListener(this);
        ((MainActivity) getActivity()).addSearchListener(this);
	}

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).removeSortListener(this);
        ((MainActivity) getActivity()).removeSearchListener(this);
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_menu, menu);

		// Dont show left arrow for first page
		menu.findItem(R.id.kos_left).setVisible(mKoSData.getCurrentPage() > 1);
		menu.findItem(R.id.kos_right).setVisible(mKoSData.getCurrentPage() < mKoSData.getMaxPages());
		super.onCreateOptionsMenu(menu, inflater);
	}		

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.kos_submenu:
			return true;		
		case R.id.kos_left:
			previousPage();
            sendGaEvent(GaConstants.Actions.PREV_PAGE, GaConstants.Labels.EMPTY);
			return true;
		case R.id.kos_right:
			nextPage();
            sendGaEvent(GaConstants.Actions.NEXT_PAGE, GaConstants.Labels.EMPTY);
			return true;
		case R.id.kos_sort:
			fragmentManager = mActivity.getSupportFragmentManager();
	        KoSSortDialogFragment koSSortDialog = new KoSSortDialogFragment();
	        koSSortDialog.show(fragmentManager, "kos_sort_dialog");
			return true;						
		case R.id.kos_search:			
			fragmentManager = mActivity.getSupportFragmentManager();
			KoSSearchDialogFragment koSSearchDialog = new KoSSearchDialogFragment();
	        koSSearchDialog.show(fragmentManager, "kos_search_dialog");
			return true;			
		case R.id.kos_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
			alert.setTitle(R.string.goto_page);
			alert.setMessage(getString(R.string.enter_page_number, mKoSData.getMaxPages()));

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			alert.setView(input);
			alert.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString())) {
						// [START Google analytics screen]
						mTracker.setScreenName(GaConstants.Categories.KOS_GOTO_DIALOG);
						mTracker.send(new HitBuilders.ScreenViewBuilder().build());
						// [END Google analytics screen]
						mKoSData.setCurrentPage(Integer.parseInt(input.getText().toString()));
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

			input.addTextChangedListener(new PageTextWatcher(dialog, mKoSData.getMaxPages()));

			dialog.show();
			return true;	
		case R.id.kos_new_item:
			String url = "http://happyride.se/annonser/add.php";
            sendGaEvent(GaConstants.Actions.NEW_ADD, GaConstants.Labels.EMPTY);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(browserIntent);							
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}	

	public void refreshList() {
        mKoSData.setCurrentPage(1);
        mKoSAdapter = null;
		fetchData();
	}

	private void fetchData() {
        showProgress(true);
		mKoSTask = new KoSListTask();
		mKoSTask.addKoSListListener(new KoSListListener() {
            public void success(List<KoSListItem> koSListItems) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    mKoSData.setKoSItems(koSListItems);
                    fillList();
                }
                showProgress(false);
            }

            public void fail() {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.kos_no_items_found), Toast.LENGTH_LONG).show();

                    showProgress(false);
                }
            }
        });
		mKoSTask.execute(mKoSData.getSearchString(), mKoSData.getCategory(), mKoSData.getRegion(), mKoSData.getType(),
				"" /*category2*/, ""/*county2*/, ""/*type2*/, ""/*price*/, ""/*year*/, mKoSData.getCurrentPage(),
                mKoSData.getSortAttributeServer(), mKoSData.getSortOrderServer());

		//?search=&category=1&county=&type=1&category2=&county2=&type2=&price=3&year=2013&p=1&sortattribute=creationdate&sortorder=DESC
	}

	private void fillList() {
        if (mKoSData.getKoSItems() == null || mKoSData.getKoSItems().isEmpty()) {
            // Workaround for orientation changes before finish loading
            showProgress(true);
            fetchData();

            return;
        } else if (mKoSAdapter == null) {
			mKoSAdapter = new ListKoSAdapter(mActivity, mKoSData.getKoSItems());
			setListAdapter(mKoSAdapter);
		} else {
			mKoSAdapter.setItems(mKoSData.getKoSItems());
			mKoSAdapter.notifyDataSetChanged();
		}

		TextView currentPage = (TextView) mActivity.findViewById(R.id.kos_current_page);
		currentPage.setText(Integer.toString(mKoSData.getCurrentPage()));
		
		TextView maxPages = (TextView) mActivity.findViewById(R.id.kos_no_of_pages);
		maxPages.setText(Integer.toString(mKoSData.getKoSItems().get(0).getNumberOfKoSPages()));
		mKoSData.setMaxPages(mKoSData.getKoSItems().get(0).getNumberOfKoSPages());
				
		TextView category = (TextView) mActivity.findViewById(R.id.kos_category);
		category.setText("Kategori: " + mKoSData.getCategoryStr());

		TextView region = (TextView) mActivity.findViewById(R.id.kos_region);
		region.setText("Region: " + mKoSData.getRegionStr());

		TextView search = (TextView) mActivity.findViewById(R.id.kos_search);
		
		String searchString = mKoSData.getSearchString();
		
		if (searchString.length() > 0) {
			search.setVisibility(View.VISIBLE);
			search.setText("Sökord: " + searchString);
		} else {
			search.setVisibility(View.GONE);
			search.setText("");
		}
		
//		TextView sortView = (TextView) mActivity.findViewById(R.id.kos_sort);
//		sortView.setText(" (Sortering: "
//				+ HappyUtils.getSortAttrNameLocal(mActivity, mKoSData.getSortAttributePosition()) + ", "
//				+ HappyUtils.getSortOrderNameLocal(mActivity, mKoSData.getSortOrderPosition()) + ")");

		getActivity().invalidateOptionsMenu();
	}

	public void nextPage() {
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void previousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() - 1);
    		fetchData();
    	}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent koSObject = new Intent(mActivity, KoSObjectActivity.class);
		KoSListItem item = mKoSData.getKoSItems().get(position);
		koSObject.putExtra(KoSObjectActivity.URL, item.getLink());
		koSObject.putExtra(KoSObjectActivity.AREA, item.getArea());
		koSObject.putExtra(KoSObjectActivity.TYPE, item.getType());
		koSObject.putExtra(KoSObjectActivity.TITLE, item.getTitle());
		koSObject.putExtra(KoSObjectActivity.DATE, item.getTime());
		koSObject.putExtra(KoSObjectActivity.PRICE, item.getPrice());
		koSObject.putExtra(KoSObjectActivity.CATEGORY, item.getCategory());
		startActivity(koSObject);
	}



	@Override
	public void onCancel(DialogInterface dialog) {
		if (mKoSTask != null) {
			mKoSTask.cancel(true);
        }
    }

    @Override
    public void onSortParamChanged(int sortAttributePos, int sortOrderPos) {

        String sortAttrNameServer = HappyUtils.getSortAttrNameServer(getActivity(), sortAttributePos);
        String sortOrderNameServer = HappyUtils.getSortOrderNameServer(getActivity(), sortOrderPos);

        Editor edit = mPreferences.edit();
        edit.putInt(SORT_ATTRIBUTE_POS, sortAttributePos);
        edit.putString(SORT_ATTRIBUTE_SERVER, sortAttrNameServer);
        edit.putInt(SORT_ORDER_POS, sortOrderPos);
        edit.putString(SORT_ORDER_SERVER, sortOrderNameServer);
        edit.apply();

        mKoSData.setSortAttributePosition(sortAttributePos);
        mKoSData.setSortOrderPosition(sortOrderPos);

        mKoSData.setSortAttributeServer(sortAttrNameServer);
        mKoSData.setSortOrderServer(sortOrderNameServer);

        mKoSData.setCurrentPage(1);
        mKoSAdapter = null;

		fetchData();
    }

	@Override
	public void onSearchParamChanged(String text, int categoryPos, int regionPos, int typePos) {
		// Sökord
        if (text != null) {
            // TODO Should not be null
            mKoSData.setSearch(text);
        }
		// Cyklar, Ramar, Komponenter...
		String categoryArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_category_position);
		String categoryArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_category_name);
		mKoSData.setCategoryPos(Integer.parseInt(categoryArrayPosition[categoryPos]));
		mKoSData.setCategoryName(categoryArrayName[categoryPos]);

		// Skåne, Blekinge, Halland...
		String regionArrayPosition[] = getResources().getStringArray(R.array.dialog_search_region_position);
		String regionArrayName[] = getResources().getStringArray(R.array.dialog_search_region_name);
		mKoSData.setRegionPos(Integer.parseInt(regionArrayPosition[regionPos]));
		mKoSData.setRegionName(regionArrayName[regionPos]);

		// Alla, Säljes, Köpes
		String typeArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_type_position);
		mKoSData.setTypePosServer(Integer.parseInt(typeArrayPosition[typePos]));

		Editor edit = mPreferences.edit();
		edit.putString(SEARCH_TEXT, text);

		edit.putInt(SEARCH_CATEGORY_POS, Integer.parseInt(categoryArrayPosition[categoryPos]));
		edit.putString(SEARCH_CATEGORY, categoryArrayName[categoryPos]);

		edit.putInt(SEARCH_REGION_POS, Integer.parseInt(regionArrayPosition[regionPos]));
		edit.putString(SEARCH_REGION, regionArrayName[regionPos]);

		edit.putInt(SEARCH_TYPE_POS, Integer.parseInt(typeArrayPosition[typePos]));

        // Dialog Spinner selection
        edit.putInt(SEARCH_TYPE_SPINNER, typePos);
        edit.putInt(SEARCH_REGION_SPINNER, regionPos);
        edit.putInt(SEARCH_CATEGORY_SPINNER, categoryPos);

        edit.apply();

		mKoSData.setCurrentPage(1);
		mKoSAdapter = null;
		fetchData();
	}

	private void sendGaEvent(String action, String label) {
		mActivity.getTracker().send(new HitBuilders.EventBuilder()
				.setCategory(GaConstants.Categories.KOS_LIST)
				.setAction(action)
				.setLabel(label)
				.build());
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//TODO A bit buggy. Need to find a better solution
//        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//             //"Left Swipe"
//            nextPage();
//            return true;
//        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//             //"Right Swipe"
//            previousPage();
//            return true;
//        }
		return false;
	}
}