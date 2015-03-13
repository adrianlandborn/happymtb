package pebeijer.happymtb.fragment;

import java.util.ArrayList;
import java.util.List;

import pebeijer.happymtb.adapter.ListItemsAdapter;
import pebeijer.happymtb.item.Item;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;

public class ItemsListFragment extends ListFragment implements DialogInterface.OnCancelListener, OnChildClickListener {
	protected ProgressDialog progDialog = null;
	protected ListItemsAdapter ItemsAdapter;
	protected List<Item> mAllItems = new ArrayList<Item>();
	protected List<Item> mItems = new ArrayList<Item>();
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListShownNoAnimation(true);
		setHasOptionsMenu(true);
		FetchItems();		
		
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
	}
	
	@Override
	public void onDestroy() {
		progDialog.dismiss();
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
	
	protected void FetchItems() {
	}	
	
	protected void FillList() {
		PopulateList();
		if (ItemsAdapter == null) {
			ItemsAdapter = new ListItemsAdapter(getActivity(), mItems);
			setListAdapter(ItemsAdapter);
		} else {
			ItemsAdapter.notifyDataSetChanged();
		}		
	}	
	
	protected void PopulateList() {
		mItems.clear();
		for(int i=0; i < mAllItems.size(); i++)	{
			if ((mAllItems.get(i).getVisible() == true) || (mAllItems.get(i).getTitle() == ""))	{
				mItems.add(mAllItems.get(i));
			}			
		}
	}
	
	protected void ExpandeAll() {
		for (int j = 0; j < mAllItems.size(); j++) {
			mAllItems.get(j).setVisible(true);
		}								
		FillList();	
	}
	
	protected void CollapseAll() {
		for (int j = 0; j < mAllItems.size(); j++) {
			mAllItems.get(j).setVisible(false);
		}								
		FillList();	
	}	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mItems.get(position).getTitle() == "")
		{
			String Group = mItems.get(position).getGroup();
			Boolean visible = mItems.get(position).getVisible();
			
			if (visible == true) {
				visible = false;
			} else {
				visible = true;
			}
			
			for (int i = 0; i < mAllItems.size(); i++) {
				if (mAllItems.get(i).getGroup() == Group) {					
					mAllItems.get(i).setVisible(visible);
				}
			}	
			
			FillList();	
		} else {
			final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mItems.get(position).getLink()));
			startActivity(intent);
		}
	}	
	
	@Override
	public void onCancel(DialogInterface dialog) {
		//getActivity().finish();
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		// TODO Auto-generated method stub
		return false;
	}
}