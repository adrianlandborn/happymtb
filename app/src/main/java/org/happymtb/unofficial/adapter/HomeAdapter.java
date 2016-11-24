package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.HomeRowView;
import org.happymtb.unofficial.item.Home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HomeAdapter extends BaseAdapter {
	private Context mContext;
	private List<Home> mHomes = new ArrayList<Home>();

	public HomeAdapter(Context context, List<Home> Homes) {
		mContext = context;
		mHomes = Homes;
	}		
	
	@Override
	public int getCount() {
		return mHomes.size();
	}

	@Override
	public Home getItem(int position) {
		return mHomes.get(position);
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

		homeRowView.setTitle(mHomes.get(position).getTitle());
		homeRowView.setText(mHomes.get(position).getText());
		homeRowView.setDate(mHomes.get(position).getDate());
		
		return homeRowView;
	}

	public void setItems(ArrayList<Home> items) {
		mHomes = items;
	}
}