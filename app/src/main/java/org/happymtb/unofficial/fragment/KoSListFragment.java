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
import org.happymtb.unofficial.task.KoSImageDownloadTask;
import org.happymtb.unofficial.task.KoSListTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class KoSListFragment extends ListFragment implements DialogInterface.OnCancelListener,
		MainActivity.SortListener, MainActivity.SearchListener {
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

    public final static String CURRENT_PAGE = "current_page";
	private ProgressDialog mProgressDialog = null;
	private KoSListTask mKoSTask;
	private ListKoSAdapter mKoSAdapter;
	private KoSData mKoSData;
	private SharedPreferences mPreferences;
	private boolean mPictureList;
	MainActivity mActivity;
	FragmentManager mFragmentManager;
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        setListShownNoAnimation(true);
        setHasOptionsMenu(true);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);

        mActivity = (MainActivity) getActivity();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPictureList = mPreferences.getBoolean(SHOW_IMAGES, true);

        mKoSData = new KoSData(
                mPreferences.getString(SORT_ATTRIBUTE_SERVER, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                mPreferences.getString(SORT_ORDER_SERVER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0),
				mPreferences.getInt(SEARCH_TYPE_POS, 3),
				mPreferences.getInt(SEARCH_REGION_POS, 0), mPreferences.getString(SEARCH_REGION, "Hela Sverige"),
				mPreferences.getInt(SEARCH_CATEGORY_POS, 0), mPreferences.getString(SEARCH_CATEGORY, "Alla Kategorier"),
				mPreferences.getString(SEARCH_TEXT, ""));

        if (savedInstanceState != null) {
            // Restore Current page.
            mKoSData.setCurrentPage(savedInstanceState.getInt(CURRENT_PAGE, 1));

            Toast.makeText(mActivity, "setCurrentPage: " + savedInstanceState.getInt(CURRENT_PAGE, 1), Toast.LENGTH_LONG).show();
        }

        fetchData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_PAGE, mKoSData.getCurrentPage());
        super.onSaveInstanceState(outState);
    }

//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState){
//         Call at the start
//        super.onRestoreInstanceState(savedInstanceState);
//
//         Retrieve variables
//        isGameFinished = savedInstanceState.getBoolean("isGameFinished");
//    }

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

		MenuItem pictureRowItem = menu.findItem(R.id.kos_picture_row);
		pictureRowItem.setVisible(!mPreferences.getBoolean(SHOW_IMAGES, true));
		MenuItem textRowItem = menu.findItem(R.id.kos_text_row);
		textRowItem.setVisible(mPreferences.getBoolean(SHOW_IMAGES, true));
		super.onCreateOptionsMenu(menu, inflater);
	}		

	public void setPictureList(boolean showImages) {
		mPictureList = showImages;
		Editor editor = mPreferences.edit();
	    editor.putBoolean(SHOW_IMAGES, showImages);
	    editor.apply();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.kos_submenu:
			return true;		
		case R.id.kos_left:
			PreviousPage();
			return true;
		case R.id.kos_refresh:
			RefreshPage();
			return true;
		case R.id.kos_right:
			NextPage();
			return true;
		case R.id.kos_picture_row:		    		
			setPictureList(true);              
			RefreshPage();
			mActivity.invalidateOptionsMenu();
			return true;
		case R.id.kos_text_row:
			setPictureList(false);
			RefreshPage();
			mActivity.invalidateOptionsMenu();
			return true;
		case R.id.kos_sort:			
			mFragmentManager = mActivity.getSupportFragmentManager();
	        KoSSortDialogFragment KoSSortDialog = new KoSSortDialogFragment();
	        KoSSortDialog.show(mFragmentManager, "fragment_edit_name");
			return true;						
		case R.id.kos_search:			
			mFragmentManager = mActivity.getSupportFragmentManager();
			KoSSearchDialogFragment KoSSearchDialog = new KoSSearchDialogFragment();
	        KoSSearchDialog.show(mFragmentManager, "fragment_edit_name");
			return true;			
		case R.id.kos_go_to_page:			
			AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);

			alert.setTitle("Gå till sidan...");
			alert.setMessage("Skriv in sidnummer som du vill hoppa till (1 - " + mKoSData.getMaxPages() + ")");

			// Set an EditText view to get user input 
			final EditText input = new EditText(mActivity);
			alert.setView(input);
			alert.setPositiveButton("Hoppa", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Do something with value!
					if (HappyUtils.isInteger(input.getText().toString()))
					{
						mKoSData.setCurrentPage(Integer.parseInt(input.getText().toString()));
						fetchData();
					}					
				}
			});

			alert.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();	
			return true;	
		case R.id.kos_new_item:
			String url = "http://happymtb.org/annonser/index.php?page=add";
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			startActivity(browserIntent);							
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}	
	
	@Override
	public void onDestroy() {
		mProgressDialog.dismiss();
		super.onDestroy();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void fetchData() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mKoSTask = new KoSListTask();
		mKoSTask.addKoSListListener(new KoSListListener() {
            public void success(List<KoSItem> KoSItems) {
                if (getActivity() != null) {
                    mKoSData.setKoSItems(KoSItems);
                    fillList();
                    if (mPictureList) {
                        KoSImageDownloadTask getKoSImages = new KoSImageDownloadTask();
                        getKoSImages.execute(mKoSData.getKoSItems(), mKoSAdapter);
                    }
                }
                mProgressDialog.dismiss();
            }

            public void fail() {
                if (getActivity() != null) {
                    Toast.makeText(mActivity, mActivity.getString(R.string.no_items_found), Toast.LENGTH_LONG).show();

                    mKoSData = new KoSData(mPreferences.getString(SORT_ATTRIBUTE_SERVER, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                            mPreferences.getString(SORT_ORDER_SERVER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0),
							mPreferences.getInt(SEARCH_TYPE_POS, 3),
							mPreferences.getInt(SEARCH_REGION_POS, 0), mPreferences.getString(SEARCH_REGION, "Hela Sverige"),
							mPreferences.getInt(SEARCH_CATEGORY_POS, 0), mPreferences.getString(SEARCH_CATEGORY, "Alla Kategorier"),
							mPreferences.getString(SEARCH_TEXT, ""));

                    mProgressDialog.dismiss();
                }
            }
        });
		mKoSTask.execute(mKoSData.getCurrentPage() - 1, mKoSData.getType(), mKoSData.getRegion(), mKoSData.getCategory(),
                mKoSData.getSearch(), mKoSData.getSortAttributeServer(), mKoSData.getSortOrderServer());
	}

	private void fillList() {
		mKoSAdapter = new ListKoSAdapter(mActivity, mKoSData.getKoSItems());
		setListAdapter(mKoSAdapter);
		
		getListView().setSelection(mKoSData.getListPosition());
		
		TextView CurrentPage = (TextView) mActivity.findViewById(R.id.kos_current_page);
		CurrentPage.setText(Integer.toString(mKoSData.getCurrentPage()));		
		
		TextView MaxPages = (TextView) mActivity.findViewById(R.id.kos_no_of_pages);
		MaxPages.setText(Integer.toString(mKoSData.getKoSItems().get(0).getNumberOfKoSPages()));
		mKoSData.setMaxPages(mKoSData.getKoSItems().get(0).getNumberOfKoSPages());
				
		TextView Category = (TextView) mActivity.findViewById(R.id.kos_category);
		Category.setText("Kategori: " + mKoSData.getKoSItems().get(0).getSelectedCategory());

		TextView Region = (TextView) mActivity.findViewById(R.id.kos_region);
		Region.setText("Region: " + mKoSData.getKoSItems().get(0).getSelectedRegion());
		
		TextView Search = (TextView) mActivity.findViewById(R.id.kos_search);
		
		String mSearch = mKoSData.getSearch();
		
		if (mSearch.length() > 0) {
			Search.setText(" (Sökord: " + mSearch + ")");
		} else {
			Search.setText("");
		}		
		
		TextView Sort = (TextView) mActivity.findViewById(R.id.kos_sort);
		
		Sort.setText(" (Sortering: "
                + HappyUtils.getSortAttrNameLocal(mActivity, mKoSData.getSortAttributePosition()) + ", "
                + HappyUtils.getSortOrderNameLocal(mActivity, mKoSData.getSortOrderPosition()) + ")");
	}

	public void RefreshPage() {
		mKoSData.setListPosition(0);
//		mActivity.setKoSDataItems(null);
		fetchData();
	}

	public void NextPage() {		
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSData.setListPosition(0);
//			mActivity.setKoSDataItems(null);
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void PreviousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSData.setListPosition(0);
//    		mActivity.setKoSDataItems(null);
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
        mKoSData.setListPosition(0);

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
		mKoSData.setListPosition(0);
		fetchData();
	}
}