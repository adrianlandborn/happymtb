package pebeijer.happymtb.fragment;

import java.util.List;

import pebeijer.happymtb.R;
import pebeijer.happymtb.item.Item;
import pebeijer.happymtb.listener.ItemListListener;
import pebeijer.happymtb.task.ArticlesListTask;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView.OnChildClickListener;

public class ArticlesListFragment extends ItemsListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	private ArticlesListTask getItems;
	
	@Override
	protected void FetchItems() {
		if ((progDialog == null) || (!progDialog.isShowing())) {
			progDialog = ProgressDialog.show(getActivity(), "", "", true, true);
			progDialog.setContentView(R.layout.progresslayout);
			progDialog.setOnCancelListener(this);
		}

		getItems = new ArticlesListTask();
		getItems.addItemListListener(new ItemListListener() {
			public void Success(List<Item> Items) {
				mAllItems = Items;
				FillList();
				progDialog.dismiss();
			}

			public void Fail() {
				progDialog.dismiss();
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
				ExpandeAll();
				return true;	
			case R.id.article_hide_all:
				CollapseAll();
				return true;				
		}
		return super.onOptionsItemSelected(item);
	}	
		
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.articlesmenu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}	
}