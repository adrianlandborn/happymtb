package org.happymtb.unofficial.fragment;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.adapter.ListCalendarAdapter;
import org.happymtb.unofficial.task.CalendarListTask;
import org.happymtb.unofficial.item.CalendarItem;
import org.happymtb.unofficial.listener.CalendarListListener;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarListFragment extends RefreshListfragment implements DialogInterface.OnCancelListener {
  	private final static int DIALOG_FETCH_CALENDAR_ERROR = 0;
	public static String TAG = "calendar_frag";

	private CalendarListTask mGetCalendarTask;
	private ListCalendarAdapter mCalendarAdapter;
	private List<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();
	MainActivity mActivity;
    private ListView mListView;
	TextView mCategoryView;
	TextView mRegionView;
	TextView mSearchView;		
	String mCategory = "";
	String mRegion = "";
	String mSearch = "";		
	String mCategoryPosition = "";	
	String mRegionPosition = "";
    int mFirstVisiblePos = 0;
	
	/** Called when the activity is first created. */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);			
		setHasOptionsMenu(true);
		
		mActivity = (MainActivity) getActivity();
		mCategoryView = (TextView) mActivity.findViewById(R.id.calendar_category);
		mRegionView = (TextView) mActivity.findViewById(R.id.calendar_region);
		mSearchView = (TextView) mActivity.findViewById(R.id.calendar_search);

        if (savedInstanceState != null) {
            mFirstVisiblePos = savedInstanceState.getInt(CURRENT_POSITION, 0);
        }
		
		fetchData();

//		getListView().setDivider(null);
//		getListView().setDividerHeight(0);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_frame, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            outState.putInt(CURRENT_POSITION, mListView.getFirstVisiblePosition());
        }
    }

    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.calendar_menu, menu);
		menu.findItem(R.id.calendar_search).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		if (mGetCalendarTask != null) {
			mGetCalendarTask.cancel(true);
		}		
	}		
	
	private void fetchData() {
        showProgress(true);

        mGetCalendarTask = new CalendarListTask();
		mGetCalendarTask.addCalendarListListener(new CalendarListListener() {
			public void success(List<CalendarItem> CalendarItems) {
				if (getActivity() != null) {
					mCalendarItems = CalendarItems;
					fillList();
                    showProgress(false);
				}
			}

			public void fail() {
				if (getActivity() != null) {
					Toast.makeText(mActivity, R.string.calendar_no_items_found, Toast.LENGTH_LONG).show();
					mCalendarItems = new ArrayList<CalendarItem>();
                    showProgress(false);
				}
			}
		});
		mGetCalendarTask.execute(mSearch, mRegionPosition, mCategoryPosition);		
	}

    private void fillList() {
		mCalendarAdapter = new ListCalendarAdapter(mActivity, mCalendarItems);
		setListAdapter(mCalendarAdapter);

        mListView = getListView();
        mListView.setSelection(mFirstVisiblePos);
		
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
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);			
	}
	
	public void refreshList() {
        mFirstVisiblePos = 0;

		fetchData();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.calendar_submenu:
			return true;		
		//TODO Reactivate and fix lifecycle and shared prefs
//		case R.id.calendar_search:
//	        FragmentManager fm = mActivity.getSupportFragmentManager();
//	        CalendarSearchDialogFragment CalendarSearchDialog = new CalendarSearchDialogFragment();
//	        CalendarSearchDialog.show(fm, "fragment_edit_name");
//			return true;
		case R.id.calendar_new_item:
			String url = "http://happymtb.org/kalender/add.php";
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
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
					.setPositiveButton(R.string.OK,
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
		
	public static class CalendarSearchDialogFragment extends DialogFragment {
		public DialogFragment newInstace() {
			DialogFragment dialogFragment = new CalendarSearchDialogFragment();
			return dialogFragment;
		}		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			final MainActivity activity = (MainActivity) getActivity();
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			LayoutInflater inflater = activity.getLayoutInflater();
			final View view = inflater.inflate(R.layout.calendar_search, null);
			builder.setView(view);
			builder.setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
					EditText searchEditText = (EditText) view.findViewById(R.id.calendar_dialog_search_text);
					Spinner searchCategory = (Spinner) view.findViewById(R.id.calendar_dialog_search_category);
					Spinner searchRegion = (Spinner) view.findViewById(R.id.calendar_dialog_search_region);

					String search = searchEditText.getText().toString();
					
					int position = searchCategory.getSelectedItemPosition();
					String CategoryArrayPosition [] =  getResources().getStringArray(R.array.calendar_dialog_search_category_position);
					String CategoryArray [] =  getResources().getStringArray(R.array.calendar_dialog_search_category);
					String categoryPosition = CategoryArrayPosition[position];
					String category = CategoryArray[position];

					position = searchRegion.getSelectedItemPosition();
					String RegionArrayPosition [] =  getResources().getStringArray(R.array.calendar_dialog_search_region_position);
					String RegionArray [] =  getResources().getStringArray(R.array.calendar_dialog_search_region);
					String regionPosition = RegionArrayPosition[position];
					String region = RegionArray[position];

					SharedPreferences.Editor editor = prefs.edit();
					editor.putString("calendar_search_text", search);
					editor.putString("calendar_search_category", category);
					editor.putString("calendar_search_region", region);
//					editor.putString("calendar_search_category_position", categoryPosition);
//					editor.putString("calendar_search_region_position", regionPosition);
					editor.apply();


				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					CalendarSearchDialogFragment.this.getDialog().cancel();
				}
			});
			Dialog dialog = builder.create();	        
			return dialog;
	    }
	}			
}