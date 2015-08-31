package org.happymtb.unofficial.fragment;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.adapter.ListItemsAdapter;
import org.happymtb.unofficial.item.Item;
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
//	protected ProgressDialog mProgressDialog = null;
	protected ListItemsAdapter mItemsAdapter;
	protected List<Item> mAllItems = new ArrayList<Item>();
	protected List<Item> mItems = new ArrayList<Item>();
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		fetchItems();
		
//		getListView().setDivider(null);
//		getListView().setDividerHeight(0);
	}
	
//	@Override
//	public void onDestroy() {
//		mProgressDialog.dismiss();
//		super.onDestroy();
//	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	protected void fetchItems() {
	}	

	protected void fillList() {
		populateList();
		if (mItemsAdapter == null) {
			mItemsAdapter = new ListItemsAdapter(getActivity(), mItems);
			setListAdapter(mItemsAdapter);
		} else {
			mItemsAdapter.notifyDataSetChanged();
		}		
	}	
	
	protected void populateList() {
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
		fillList();
	}
	
	protected void CollapseAll() {
		for (int j = 0; j < mAllItems.size(); j++) {
			mAllItems.get(j).setVisible(false);
		}								
		fillList();
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
			
			fillList();
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
		return false;
	}
}