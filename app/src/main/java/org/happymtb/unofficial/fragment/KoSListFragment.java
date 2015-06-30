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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class KoSListFragment extends ListFragment implements DialogInterface.OnCancelListener, MainActivity.SortListener {
	public final static String SHOW_IMAGES = "kospicturelist";
	public final static String SORT_ORDER_POS = "sort_order_pos";
	public final static String SORT_ORDER = "sort_order";
	public final static String SORT_ATTRIBUTE_POS = "sort_attribute_pos";
	public final static String SORT_ATTRIBUTE = "sort_attribute";
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
                mPreferences.getString(SORT_ATTRIBUTE, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                mPreferences.getString(SORT_ORDER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0));

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
	}


    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).removeSortListener(this);
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

                    mKoSData = new KoSData(mPreferences.getString(SORT_ATTRIBUTE, "creationdate"), mPreferences.getInt(SORT_ATTRIBUTE_POS, 0),
                            mPreferences.getString(SORT_ORDER, "DESC"), mPreferences.getInt(SORT_ORDER_POS, 0));

                    mProgressDialog.dismiss();
                }
            }
        });
		mKoSTask.execute(mKoSData.getCurrentPage() - 1, mKoSData.getType(), mKoSData.getRegion(), mKoSData.getCategory(),
                mKoSData.getSearch(), mKoSData.getSortAttribute(), mKoSData.getSortOrder());
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
        edit.putString(SORT_ATTRIBUTE, sortAttrNameServer);
        edit.putInt(SORT_ORDER_POS, sortOrderPos);
        edit.putString(SORT_ORDER, sortOrderNameServer);
        edit.apply();

        mKoSData.setSortAttributePosition(sortAttributePos);
        mKoSData.setSortOrderPosition(sortOrderPos);

        mKoSData.setSortAttribute(sortAttrNameServer);
        mKoSData.setSortOrder(sortOrderNameServer);

        mKoSData.setCurrentPage(1);
        mKoSData.setListPosition(0);

        fetchData();
    }

	public class KoSSearchDialogFragment extends DialogFragment {
		public DialogFragment newInstace() {
			DialogFragment dialogFragment = new KoSSearchDialogFragment();
			return dialogFragment;
		}		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			LayoutInflater inflater = mActivity.getLayoutInflater();
			final View view = inflater.inflate(R.layout.kos_search, null);
			builder.setView(view);
            builder.setPositiveButton("Sök", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mKoSData.setListPosition(0);

					EditText mSearchString = (EditText) view.findViewById(R.id.kos_dialog_search_text);
					Spinner mSearchCategory = (Spinner) view.findViewById(R.id.kos_dialog_search_category);
					Spinner mSearchRegion = (Spinner) view.findViewById(R.id.kos_dialog_search_region);
					Spinner mSearchType = (Spinner) view.findViewById(R.id.kos_dialog_search_type);
   
					mKoSData.setSearch(mSearchString.getText().toString().trim());
					
					int position = mSearchCategory.getSelectedItemPosition();
					String CategoryArrayPosition [] =  getResources().getStringArray(R.array.kos_dialog_search_category_position);
					String CategoryArray [] =  getResources().getStringArray(R.array.kos_dialog_search_category);
					mKoSData.setCategory(Integer.parseInt(CategoryArrayPosition[position]));
					mKoSData.setCategoryStr(CategoryArray[position]);

					position = mSearchRegion.getSelectedItemPosition();
					String RegionArrayPosition [] =  getResources().getStringArray(R.array.kos_dialog_search_region_position);
					String RegionArray [] =  getResources().getStringArray(R.array.kos_dialog_search_region);
					mKoSData.setRegion(Integer.parseInt(RegionArrayPosition[position]));
					mKoSData.setRegionStr(RegionArray[position]);

					position = mSearchType.getSelectedItemPosition();
					String TypeArrayPosition [] =  getResources().getStringArray(R.array.kos_dialog_search_type_position);
					mKoSData.setType(Integer.parseInt(TypeArrayPosition[position]));					

					mKoSData.setCurrentPage(1);
					fetchData();
				}
			});
			builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					KoSSearchDialogFragment.this.getDialog().cancel();
				}
			});
			Dialog dialog = builder.create();	        
			return dialog;
	    }
	}		
}