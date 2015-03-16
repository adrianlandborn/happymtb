package org.pebeijer.happymtb.fragment;

import java.util.List;

import org.pebeijer.happymtb.R;
import org.pebeijer.happymtb.item.Item;
import org.pebeijer.happymtb.listener.ItemListListener;
import org.pebeijer.happymtb.task.ShopsListTask;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;

public class ShopsListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	
	private ShopsListTask mShopsTask;
	
	@Override
	protected void FetchItems() {
		if ((mProgressDialog == null) || (!mProgressDialog.isShowing())) {
			mProgressDialog = ProgressDialog.show(getActivity(), "", "", true, true);
			mProgressDialog.setContentView(R.layout.progress_layout);
			mProgressDialog.setOnCancelListener(this);
		}
		
		mShopsTask = new ShopsListTask();
		mShopsTask.addItemListListener(new ItemListListener() {
            public void Success(List<Item> Items) {
                mAllItems = Items;
                FillList();
                mProgressDialog.dismiss();
            }

            public void Fail() {
                mProgressDialog.dismiss();
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
				ExpandeAll();
				return true;	
			case R.id.shops_hide_all:
				CollapseAll();
				return true;					
			case R.id.shops_add:
				String url = "http://happymtb.org/forum/butiker/add.php?cat=4";
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
				startActivity(browserIntent);							
				return true;			
		}
		return super.onOptionsItemSelected(item);
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();		
		inflater.inflate(R.menu.shopsmenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}			
}