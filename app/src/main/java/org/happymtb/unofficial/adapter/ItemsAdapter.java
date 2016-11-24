package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.ItemRowView;
import org.happymtb.unofficial.item.Item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemsAdapter extends BaseAdapter {
	private Context mContext;
	private List<Item> mItems = new ArrayList<Item>();

	public ItemsAdapter(Context context, List<Item> Items) {
		mContext = context;
		mItems = Items;
	}		
	
	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Item getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemRowView itemRowView;

		if (convertView == null) {
			itemRowView = new ItemRowView(mContext);
		} else {
			itemRowView = (ItemRowView) convertView;
		}

		if (mItems.get(position).getTitle() != "") {
			itemRowView.setHeaderVisible(false);
			itemRowView.setTitle(mItems.get(position).getTitle());
			itemRowView.setDescription(mItems.get(position).getDescription());
		} else {
			itemRowView.setHeaderVisible(true);
			itemRowView.setHeaderTitle(mItems.get(position).getGroup());
			if (mItems.get(position).getVisible()) {
				itemRowView.setCollapse(true);
			} else {
				itemRowView.setCollapse(false);
			}
		}

		return itemRowView;
	}

	public void setItems(ArrayList<Item> items) {
		mItems = items;
	}
}