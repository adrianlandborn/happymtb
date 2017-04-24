package org.happymtb.unofficial.fragment;

import java.util.ArrayList;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.analytics.GaConstants;
import org.happymtb.unofficial.analytics.HappyApplication;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.listener.ItemListListener;
import org.happymtb.unofficial.task.ShopsListTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ShopsListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "shops_frag";

	private ShopsListTask mShopsTask;
	private Tracker mTracker;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_shops));
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Obtain the shared Tracker instance.
		HappyApplication application = (HappyApplication) getActivity().getApplication();
		mTracker = application.getDefaultTracker();

		// [START Google analytics screen]
		mTracker.setScreenName(GaConstants.Categories.SHOPS);
		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
		// [END Google analytics screen]
	}

	@Override
	protected void fetchItems() {
		mShopsTask = new ShopsListTask();
		mShopsTask.addItemListListener(new ItemListListener() {
            public void success(ArrayList<Item> Items) {
                if (getActivity() != null && !getActivity().isFinishing()) {
                    mAllItems = Items;
                    fillList();
                }
            }

            public void fail() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					Toast.makeText(getActivity(), R.string.shops_no_items_found, Toast.LENGTH_SHORT).show();
				}
            }
        });

		mShopsTask.execute();
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.shops_submenu:
				return true;
			case R.id.shops_expand_all:
				expandAll();
				return true;	
			case R.id.shops_hide_all:
				collapseAll();
				return true;					
			case R.id.shops_add:
				String url = "https://happyride.se/forum/butiker/add.php?cat=4";
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);							
				return true;			
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.shops_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}			
}