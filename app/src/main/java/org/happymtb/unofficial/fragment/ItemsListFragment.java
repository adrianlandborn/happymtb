package org.happymtb.unofficial.fragment;

import java.util.ArrayList;

import org.happymtb.unofficial.adapter.ListItemsAdapter;
import org.happymtb.unofficial.item.Item;

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
	protected static final String ITEMS = "items";
	protected static final String ALL_ITEMS = "all_items";
    protected ListItemsAdapter mItemsAdapter;
	protected ArrayList<Item> mItems = new ArrayList<Item>();
	protected ArrayList<Item> mAllItems = new ArrayList<Item>();
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mItems = (ArrayList<Item>) savedInstanceState.getSerializable(ITEMS);
            mAllItems = (ArrayList<Item>) savedInstanceState.getSerializable(ALL_ITEMS);
			if (mAllItems != null && !mAllItems.isEmpty()) {
				fillList();
			} else {
				fetchItems();
			}
        } else {
            fetchItems();
        }
	}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ITEMS, mItems);
		outState.putSerializable(ALL_ITEMS, mAllItems);
	}

    protected void fetchItems() {
	}	

	protected void fillList() {
		populateList();
		if (mItemsAdapter == null) {
			mItemsAdapter = new ListItemsAdapter(getActivity(), mItems);
			setListAdapter(mItemsAdapter);
		} else {
			mItemsAdapter.setItems(mItems);
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

	protected void expandGroup(String group) {
        for (int i = 0; i < mAllItems.size(); i++) {
            if (mAllItems.get(i).getGroup() == group) {
                mAllItems.get(i).setVisible(true);
            }
        }
	}
	protected void expandAll() {
		for (int j = 0; j < mAllItems.size(); j++) {
			mAllItems.get(j).setVisible(true);
		}								
		fillList();
	}
	
	protected void collapseAll() {
		for (int j = 0; j < mAllItems.size(); j++) {
			mAllItems.get(j).setVisible(false);
		}								
		fillList();
	}	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (mItems.get(position).getTitle() == "")
		{
			String group = mItems.get(position).getGroup();
			boolean visible = mItems.get(position).getVisible();

			visible = visible != true;
			
			for (int i = 0; i < mAllItems.size(); i++) {
				if (mAllItems.get(i).getGroup() == group) {
					mAllItems.get(i).setVisible(visible);
				}
			}	
			
			fillList();
		} else {
			openLink(mItems.get(position).getLink());
		}
	}

	public void openLink(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        startActivity(intent);
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