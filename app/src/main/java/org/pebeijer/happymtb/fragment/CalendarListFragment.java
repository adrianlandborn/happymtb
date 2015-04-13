package org.pebeijer.happymtb.fragment;

import java.util.ArrayList;
import java.util.List;

import org.pebeijer.happymtb.MainActivity;
import org.pebeijer.happymtb.R;
import org.pebeijer.happymtb.adapter.ListCalendarAdapter;
import org.pebeijer.happymtb.task.CalendarListTask;
import org.pebeijer.happymtb.item.CalendarItem;
import org.pebeijer.happymtb.listener.CalendarListListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class CalendarListFragment extends ListFragment implements DialogInterface.OnCancelListener {
  	private final static int DIALOG_FETCH_CALENDAR_ERROR = 0;
	private ProgressDialog mProgressDialog = null;
	private CalendarListTask mGetCalendar;
	private ListCalendarAdapter mCalendarAdapter;
	private List<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();
	private SharedPreferences mPreferences;
	private int mTextSize;
	MainActivity mActivity;
	TextView mCategoryView;
	TextView mRegionView;
	TextView mSearchView;		
	String mCategory = "";
	String mRegion = "";
	String mSearch = "";		
	String mCategoryPosition = "";	
	String mRegionPosition = "";	
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);			
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		
		mActivity = (MainActivity) getActivity();
		mCategoryView = (TextView) mActivity.findViewById(R.id.calendar_category);
		mRegionView = (TextView) mActivity.findViewById(R.id.calendar_region);
		mSearchView = (TextView) mActivity.findViewById(R.id.calendar_search);		
		
		fetchData();
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[mPreferences.getInt("textsize", 0)]);			
	    				
		mCategoryView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mRegionView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		mSearchView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.calendar_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mGetCalendar != null) {
			mGetCalendar.cancel(true);
		}		
	}		
	
	private void fetchData() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(mActivity, "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mGetCalendar = new CalendarListTask();
		mGetCalendar.addCalendarListListener(new CalendarListListener() {
			public void success(List<CalendarItem> CalendarItems) {
				if (getActivity() != null) {
                    mCalendarItems = CalendarItems;
                    fillList();
                    mProgressDialog.dismiss();
                }
			}

			public void fail() {
                if (getActivity() != null) {
                    Toast.makeText(mActivity, "Inga objekt hittades", Toast.LENGTH_LONG).show();
                    mCalendarItems = new ArrayList<CalendarItem>();
                    mProgressDialog.dismiss();
                }
			}
		});
		mGetCalendar.execute(mSearch, mRegionPosition, mCategoryPosition);		
	}

	private void fillList() {
		mCalendarAdapter = new ListCalendarAdapter(mActivity, mCalendarItems);
		setListAdapter(mCalendarAdapter);
		
//		getListView().setSelection(mCalendarItems.getListPosition());
		
		mCategoryView.setText("Kategori: " + mCategory);
		mRegionView.setText("Region: " + mRegion);	
		
		if (mSearch.length() > 0) {
			mSearchView.setText(" (Sökord: " + mSearch + ")");
		} else {
			mSearchView.setText("");
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String url = "http://happymtb.org/kalender/" + mCalendarItems.get(position).getId();
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		startActivity(browserIntent);			
	}
	
	public void RefreshPage() {
//		mKoSData.setListPosition(0);
//		mActivity.SetKoSDataItems(null);
		fetchData();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.calendar_submenu:
			return true;		
		case R.id.calendar_refresh:
			RefreshPage();
			return true;
		case R.id.calendar_search:			
	        FragmentManager fm = mActivity.getSupportFragmentManager();
	        CalendarSearchDialogFragment CalendarSearchDialog = new CalendarSearchDialogFragment();
	        CalendarSearchDialog.show(fm, "fragment_edit_name");
			return true;			
		case R.id.calendar_new_item:
			String url = "http://happymtb.org/kalender/add.php";
			Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
			startActivity(browserIntent);							
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}		
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder;
		switch (id) {
		case DIALOG_FETCH_CALENDAR_ERROR:
			builder = new AlertDialog.Builder(mActivity);
			builder.setTitle("Felmeddelande");
			builder.setMessage(
					"Det blev något fel vid hämtning av kalendern")
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
		
	public class CalendarSearchDialogFragment extends DialogFragment {	
		public DialogFragment newInstace() {
			DialogFragment dialogFragment = new CalendarSearchDialogFragment();
			return dialogFragment;
		}		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			LayoutInflater inflater = mActivity.getLayoutInflater();
			final View view = inflater.inflate(R.layout.calendar_search, null);
			builder.setView(view);
			builder.setPositiveButton("Sök", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {

//					mKoSData.setListPosition(0);
//					mActivity.SetKoSDataItems(null);
					
					EditText SearchString = (EditText) view.findViewById(R.id.calendar_dialog_search_text);
					Spinner SearchCategory = (Spinner) view.findViewById(R.id.calendar_dialog_search_category);
					Spinner SearchRegion = (Spinner) view.findViewById(R.id.calendar_dialog_search_region);

					mSearch = SearchString.getText().toString();
					
					int position = SearchCategory.getSelectedItemPosition();
					String CategoryArrayPosition [] =  getResources().getStringArray(R.array.calendar_dialog_search_category_position);
					String CategoryArray [] =  getResources().getStringArray(R.array.calendar_dialog_search_category);
					mCategoryPosition = CategoryArrayPosition[position];
					mCategory = CategoryArray[position];

					position = SearchRegion.getSelectedItemPosition();
					String RegionArrayPosition [] =  getResources().getStringArray(R.array.calendar_dialog_search_region_position);
					String RegionArray [] =  getResources().getStringArray(R.array.calendar_dialog_search_region);
					mRegionPosition = RegionArrayPosition[position];					
					mRegion = RegionArray[position];

					fetchData();
				}
			});
			builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					CalendarSearchDialogFragment.this.getDialog().cancel();
				}
			});
			Dialog dialog = builder.create();	        
			return dialog;
	    }
	}			
}