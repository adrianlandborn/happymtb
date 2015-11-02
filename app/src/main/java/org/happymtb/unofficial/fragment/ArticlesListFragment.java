package org.happymtb.unofficial.fragment;

import java.util.ArrayList;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.listener.ItemListListener;
import org.happymtb.unofficial.task.ArticlesListTask;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class ArticlesListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	public static String TAG = "articles_frag";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.main_articles));
	}
	
	@Override
	protected void fetchItems() {
		ArticlesListTask getItems = new ArticlesListTask();
		getItems.addItemListListener(new ItemListListener() {
			public void success(ArrayList<Item> items) {
				if (getActivity() != null && !getActivity().isFinishing()) {
					mAllItems = items;

					expandGroup(items.get(0).getGroup());
					fillList();
				}
			}

			public void fail() {
				if (getActivity() != null && !getActivity().isFinishing()) {
					Toast.makeText(getActivity(), R.string.articles_no_items_found, Toast.LENGTH_SHORT).show();
				}
			}
		});

		getItems.execute();
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

	//TODO Reloads on orientation changes. Also no fullscreen option on videos. Remove?
//	@Override
//	public void openLink(String url) {
//		Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
//		browserIntent.putExtra("url", url);
//		startActivity(browserIntent);
//	}
}