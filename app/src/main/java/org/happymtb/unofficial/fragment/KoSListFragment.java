package org.happymtb.unofficial.fragment;

import java.util.List;

import org.happymtb.unofficial.KoSObjectActivity;
import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListKoSAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSData;
import org.happymtb.unofficial.item.KoSItem;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
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

public class KoSListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener,
		MainActivity.SortListener, MainActivity.SearchListener, GestureDetector.OnGestureListener {
    public static String TAG = "kos_frag";

	public final static String SHOW_IMAGES = "kospicturelist";

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

	private KoSListTask mKoSTask;
	private ListKoSAdapter mKoSAdapter;
	private KoSData mKoSData;
	private SharedPreferences mPreferences;
	private MainActivity mActivity;
	private FragmentManager mFragmentManager;

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
		super.onCreateOptionsMenu(menu, inflater);
	}		

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.kos_submenu:
			return true;		
		case R.id.kos_left:
			PreviousPage();
			return true;
		case R.id.kos_right:
			NextPage();
			return true;
		case R.id.kos_sort:
			mFragmentManager = mActivity.getSupportFragmentManager();
	        KoSSortDialogFragment koSSortDialog = new KoSSortDialogFragment();
	        koSSortDialog.show(mFragmentManager, "kos_sort_dialog");
			return true;						
		case R.id.kos_search:			
			mFragmentManager = mActivity.getSupportFragmentManager();
			KoSSearchDialogFragment koSSearchDialog = new KoSSearchDialogFragment();
	        koSSearchDialog.show(mFragmentManager, "kos_search_dialog");
			return true;			
		case R.id.kos_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

			alert.setTitle(R.string.goto_page);
			alert.setMessage(getString(R.string.enter_page_number, mKoSData.getMaxPages()));

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			alert.setView(input);
			alert.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString())) {
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
			String url = "http://happymtb.org/annonser/index.php?page=add";
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
            public void success(List<KoSItem> KoSItems) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    mKoSData.setKoSItems(KoSItems);
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
		mKoSTask.execute(mKoSData.getCurrentPage() - 1, mKoSData.getType(), mKoSData.getRegion(), mKoSData.getCategory(),
                mKoSData.getSearch(), mKoSData.getSortAttributeServer(), mKoSData.getSortOrderServer());
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
		category.setText("Kategori: " + mKoSData.getKoSItems().get(0).getSelectedCategory());

		TextView region = (TextView) mActivity.findViewById(R.id.kos_region);
		region.setText("Region: " + mKoSData.getKoSItems().get(0).getSelectedRegion());
		
		TextView search = (TextView) mActivity.findViewById(R.id.kos_search);
		
		String mSearch = mKoSData.getSearch();
		
		if (mSearch.length() > 0) {
			search.setText(" (Sökord: " + mSearch + ")");
		} else {
			search.setText("");
		}		
		
		TextView sortView = (TextView) mActivity.findViewById(R.id.kos_sort);
		
		sortView.setText(" (Sortering: "
				+ HappyUtils.getSortAttrNameLocal(mActivity, mKoSData.getSortAttributePosition()) + ", "
				+ HappyUtils.getSortOrderNameLocal(mActivity, mKoSData.getSortOrderPosition()) + ")");
	}

	public void NextPage() {
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void PreviousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSAdapter = null;
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() - 1);
    		fetchData();
    	}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent KoSObject = new Intent(mActivity, KoSObjectActivity.class);
		KoSObject.putExtra("KoSObjectLink", mKoSData.getKoSItems().get(position).getLink());
		startActivity(KoSObject);
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
		mKoSData.setSearch(text);

		// Cyklar, Ramar, Komponenter...
		String categoryArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_category_position);
		String categoryArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_category_name);
		mKoSData.setCategoryPos(Integer.parseInt(categoryArrayPosition[categoryPos]));
		mKoSData.setCategoryName(categoryArrayName[categoryPos]);

		// Skåne, Blekinge, Halland...
		String regionArrayPosition[] = getResources().getStringArray(R.array.kos_dialog_search_region_position);
		String regionArrayName[] = getResources().getStringArray(R.array.kos_dialog_search_region_name);
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
//            NextPage();
//            return true;
//        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//             //"Right Swipe"
//            PreviousPage();
//            return true;
//        }
		return false;
	}
}