package org.pebeijer.happymtb.fragment;

import java.util.List;

import org.pebeijer.happymtb.KoSObjectActivity;
import org.pebeijer.happymtb.MainActivity;
import org.pebeijer.happymtb.R;
import org.pebeijer.happymtb.adapter.ListKoSAdapter;
import org.pebeijer.happymtb.helpers.HappyUtils;
import org.pebeijer.happymtb.item.KoSData;
import org.pebeijer.happymtb.item.KoSItem;
import org.pebeijer.happymtb.listener.KoSListListener;
import org.pebeijer.happymtb.task.KoSImageDownloadTask;
import org.pebeijer.happymtb.task.KoSListTask;

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
import android.util.TypedValue;
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
	private ProgressDialog mProgressDialog = null;
	private KoSListTask mKoSTask;
	private ListKoSAdapter mKoSAdapter;
	private KoSData mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "DESC", "Fallande", 0);
	private SharedPreferences mPreferences;
	private Boolean mPictureList;
	private int mTextSize;
	MainActivity mActivity;
	FragmentManager mFragmentManager;
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);			
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		mActivity = (MainActivity) getActivity();
		
		FetchData();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[mPreferences.getInt("textsize", 0)]);
		mPictureList = mPreferences.getBoolean("kospicturelist", true);
	    
		TextView Category = (TextView) mActivity.findViewById(R.id.kos_category);
		TextView Region = (TextView) mActivity.findViewById(R.id.kos_region);
		TextView Search = (TextView) mActivity.findViewById(R.id.kos_search);
		TextView Sort = (TextView) mActivity.findViewById(R.id.kos_sort);
		TextView PageText = (TextView) mActivity.findViewById(R.id.kos_page_text);
		TextView CurrentPage = (TextView) mActivity.findViewById(R.id.kos_current_page);
		TextView ByText = (TextView) mActivity.findViewById(R.id.kos_by_text);
		TextView MaxPages = (TextView) mActivity.findViewById(R.id.kos_no_of_pages);
		
		Category.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		Region.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		Search.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		Sort.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		PageText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		CurrentPage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
		ByText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		MaxPages.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);		
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.kos_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}		

	public void setPictureList(Boolean Value) {
		Editor editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
	    editor.putBoolean("kospicturelist", Value);              
	    editor.commit();
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
			mPictureList = true;
			RefreshPage();
			return true;
		case R.id.kos_text_row:
			setPictureList(false);
			mPictureList = false;
			RefreshPage();
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
						FetchData();
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

	private void FetchData() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mKoSTask = new KoSListTask();
		mKoSTask.addKoSListListener(new KoSListListener() {
            public void Success(List<KoSItem> KoSItems) {
                mKoSData.setKoSItems(KoSItems);
                FillList();
                if (mPictureList) {
                    KoSImageDownloadTask getKoSImages = new KoSImageDownloadTask();
                    getKoSImages.execute(mKoSData.getKoSItems(), mKoSAdapter);
                }
                mProgressDialog.dismiss();
            }

            public void Fail() {
                Toast mToast;
                mToast = Toast.makeText(mActivity, "", Toast.LENGTH_LONG);
                mToast.setText(mActivity.getString(R.string.no_items_found));
                mToast.show();

                mKoSData = new KoSData(1, 1, 3, 0, "Hela Sverige", 0, "Alla Kategorier", "", null, 0, "creationdate", "Tid", 0, "ASC", "Stigande", 0);

                mProgressDialog.dismiss();
//				showDialog(DIALOG_FETCH_KOS_ERROR);
            }
        });
		mKoSTask.execute(mKoSData.getCurrentPage() - 1, mKoSData.getType(), mKoSData.getRegion(), mKoSData.getCategory(),
                mKoSData.getSearch(), mKoSData.getSortAttribute(), mKoSData.getSortOrder());
	}

	private void FillList() {
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
		mActivity.SetKoSDataItems(null);
		FetchData();
	}

	public void NextPage() {		
		if (mKoSData.getCurrentPage() < mKoSData.getMaxPages())
		{			
			mKoSData.setListPosition(0);
			mActivity.SetKoSDataItems(null);
			mKoSData.setCurrentPage(mKoSData.getCurrentPage() + 1);
			FetchData();
		}
	}

	public void PreviousPage() {
    	if (mKoSData.getCurrentPage() > 1)
    	{
    		mKoSData.setListPosition(0);
    		mActivity.SetKoSDataItems(null);
    		mKoSData.setCurrentPage(mKoSData.getCurrentPage() - 1);	
    		FetchData();
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
                    activity.SetKoSDataItems(null);
					
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
					FetchData();                	   
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
					mActivity.SetKoSDataItems(null);
					
					EditText mSearchString = (EditText) view.findViewById(R.id.kos_dialog_search_text);
					Spinner mSearchCategory = (Spinner) view.findViewById(R.id.kos_dialog_search_category);
					Spinner mSearchRegion = (Spinner) view.findViewById(R.id.kos_dialog_search_region);
					Spinner mSearchType = (Spinner) view.findViewById(R.id.kos_dialog_search_type);
   
					mKoSData.setSearch(mSearchString.getText().toString());
					
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
					FetchData();                	   
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