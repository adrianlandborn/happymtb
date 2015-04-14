package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.ItemRowView;
import org.happymtb.unofficial.item.Item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListItemsAdapter extends BaseAdapter {
	private Context mContext;
	private List<Item> mItems = new ArrayList<Item>();

	public ListItemsAdapter(Context context, List<Item> Items) {
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
		ItemRowView ItemRowV = null;

		if (convertView == null) {
			ItemRowV = new ItemRowView(mContext);
		} else {
			ItemRowV = (ItemRowView) convertView;
		}

		if (mItems.get(position).getTitle() != "") {
			ItemRowV.setHeaderVisible(false);
			ItemRowV.setTitle(mItems.get(position).getTitle());
			ItemRowV.setDescription(mItems.get(position).getDescription());						
		} else {
			ItemRowV.setHeaderVisible(true);
			ItemRowV.setHeaderTitle(mItems.get(position).getGroup());
			if (mItems.get(position).getVisible() == true) {
				ItemRowV.setCollapse(true);
			} else {
				ItemRowV.setCollapse(false);
			}
		}

		return ItemRowV;
	}

}