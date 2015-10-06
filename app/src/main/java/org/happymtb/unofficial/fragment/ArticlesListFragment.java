package org.happymtb.unofficial.fragment;

import java.util.ArrayList;
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
import android.widget.Toast;

public class ArticlesListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "articles_frag";
	private ArticlesListTask mGetItems;
	
	@Override
	protected void fetchItems() {
		mGetItems = new ArticlesListTask();
		mGetItems.addItemListListener(new ItemListListener() {
            public void success(ArrayList<Item> items) {
                if (getActivity() != null) {
                    mAllItems = items;

					expandGroup(items.get(0).getGroup());
                    fillList();
                }
            }

            public void fail() {
				if (getActivity() != null) {
					Toast.makeText(getActivity(), R.string.articles_no_items_found, Toast.LENGTH_SHORT).show();
				}
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