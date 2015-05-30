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

public class KoSListFragment extends ListFragment implements DialogInterface.OnCancelListener {
	private final static int DIALOG_FETCH_KOS_ERROR = 0;
	public final static String SHOW_IMAGES = "kospicturelist";
	private ProgressDialog mProgressDialog = null;
	private KoSListTask mKoSTask;
	private ListKoSAdapter mKoSAdapter;
	private KoSData mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "DESC", "Fallande", 0);
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
		
		fetchData();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
        mPictureList = mPreferences.getBoolean(SHOW_IMAGES, true);
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

                    mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "ASC", "Stigande", 0);

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
		
		Sort.setText(" (Sortering: " + mKoSData.getSortAttributeStr() + ", " + mKoSData.getSortOrderStr() + ")");			
	}

	public void RefreshPage() {
		mKoSData.setListPosition(0);
		mActivity.setKoSDataItems(null);
		fetchData();
	}

	public void NextPage() {		
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSData.setListPosition(0);
			mActivity.setKoSDataItems(null);
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			fetchData();
		}
	}

	public void PreviousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSData.setListPosition(0);
    		mActivity.setKoSDataItems(null);
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

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_KOS_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel vid hämtning av köp och sälj")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									mActivity.finish();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mKoSTask != null) {
			mKoSTask.cancel(true);
		}
	}
	
	private class KoSSortDialogFragment extends DialogFragment {


        public DialogFragment newInstace() {
			DialogFragment dialogFragment = new KoSSortDialogFragment();
			return dialogFragment;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
            final MainActivity activity = (MainActivity) getActivity();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = activity.getLayoutInflater();
			final View view = inflater.inflate(R.layout.kos_sort, null);
			builder.setView(view);
			builder.setPositiveButton("Sortera", new DialogInterface.OnClickListener() {	            	   
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mKoSData.setListPosition(0);
                    activity.setKoSDataItems(null);
					
					Spinner SortAttribute = (Spinner) view.findViewById(R.id.kos_dialog_sort_attribute);
					Spinner SortOrder = (Spinner) view.findViewById(R.id.kos_dialog_sort_order);
   
					int position = SortAttribute.getSelectedItemPosition();
					String AttributeArrayPosition [] =  getResources().getStringArray(R.array.kos_dialog_sort_attribute_position);
					String AttributeArray [] =  getResources().getStringArray(R.array.kos_dialog_sort_attribute);
					mKoSData.setSortAttribute(AttributeArrayPosition[position]);
					mKoSData.setSortAttributeStr(AttributeArray[position]);
					mKoSData.setSortAttributePosition(position);

					position = SortOrder.getSelectedItemPosition();
					String OrderArrayPosition [] =  getResources().getStringArray(R.array.kos_dialog_sort_order_position);
					String OrderArray [] =  getResources().getStringArray(R.array.kos_dialog_sort_order);
					mKoSData.setSortOrder(OrderArrayPosition[position]);
					mKoSData.setSortOrderStr(OrderArray[position]);
					mKoSData.setSortOrderPosition(position);

					mKoSData.setCurrentPage(1);
					fetchData();
				}
			});
			builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					KoSSortDialogFragment.this.getDialog().cancel();
				}
			});
			Dialog dialog = builder.create();	        
			return dialog;
	    }
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
					mActivity.setKoSDataItems(null);
					
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