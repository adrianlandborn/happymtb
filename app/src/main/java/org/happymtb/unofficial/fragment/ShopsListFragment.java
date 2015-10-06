package org.happymtb.unofficial.fragment;

import java.util.List;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.listener.ItemListListener;
import org.happymtb.unofficial.task.ShopsListTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class ShopsListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "shops_frag";

	private ShopsListTask mShopsTask;
	
	@Override
	protected void fetchItems() {
		mShopsTask = new ShopsListTask();
		mShopsTask.addItemListListener(new ItemListListener() {
            public void success(List<Item> Items) {
                if (getActivity() != null) {
                    mAllItems = Items;
                    fillList();
                }
            }

            public void fail() {
				if (getActivity() != null) {
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
				String url = "http://happymtb.org/forum/butiker/add.php?cat=4";
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