package org.happymtb.unofficial.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.happymtb.unofficial.item.HomeItem;
import org.happymtb.unofficial.view.HomeRowView;

import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends BaseAdapter {
	private Context mContext;
	private List<HomeItem> mHomeItems = new ArrayList<>();

	public HomeAdapter(Context context, List<HomeItem> homeItems) {
		mContext = context;
		mHomeItems = homeItems;
	}		
	
	@Override
	public int getCount() {
		return mHomeItems.size();
	}

	@Override
	public HomeItem getItem(int position) {
		return mHomeItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HomeRowView homeRowView;

		if (convertView == null) {
			homeRowView = new HomeRowView(mContext);
		} else {
			homeRowView = (HomeRowView) convertView;
		}

		homeRowView.setTitle(mHomeItems.get(position).getTitle());
		homeRowView.setCategory(mHomeItems.get(position).getCategory());
		homeRowView.setText(mHomeItems.get(position).getText());
		homeRowView.setDate(mHomeItems.get(position).getDate());
		
		return homeRowView;
	}

	public void setItems(ArrayList<HomeItem> items) {
		mHomeItems = items;
	}
}