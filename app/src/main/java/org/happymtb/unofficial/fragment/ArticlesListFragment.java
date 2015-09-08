package org.happymtb.unofficial.fragment;

import java.util.List;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.listener.ItemListListener;
import org.happymtb.unofficial.task.ArticlesListTask;

import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;

public class ArticlesListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	private ArticlesListTask mGetItems;
	
	@Override
	protected void fetchItems() {
		mGetItems = new ArticlesListTask();
		mGetItems.addItemListListener(new ItemListListener() {
            public void success(List<Item> items) {
                if (getActivity() != null) {
                    mAllItems = items;

					expandGroup(items.get(0).getGroup());
                    fillList();
                }
            }

            public void fail() {
//                mProgressDialog.dismiss();
            }
        });

		mGetItems.execute();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.article_submenu:
				return true;			
			case R.id.article_expand_all:
				expandAll();
				return true;	
			case R.id.article_hide_all:
				collapseAll();
				return true;				
		}
		return super.onOptionsItemSelected(item);
	}	
		
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.articles_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}	
}