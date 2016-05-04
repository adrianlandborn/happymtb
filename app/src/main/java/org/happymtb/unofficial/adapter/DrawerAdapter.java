package org.happymtb.unofficial.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.happymtb.unofficial.R;

public class DrawerAdapter extends ArrayAdapter<String> {
	private Context mContext;
	private Integer[] mIcons;
	private String[] mLabels;

	public DrawerAdapter(Context context, Integer[] icons, String[] labels) {
		super(context, R.layout.drawer_row, labels);
		mContext = context;
		mIcons = icons;
		mLabels = labels;
	}
	
	@Override
	public int getCount() {
		return mLabels.length;
	}

	@Override
	public String getItem(int position) {
		return mLabels[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drawer_row, null, false);
		} else {
			view = convertView;
		}

		final ImageView iconView = (ImageView) view.findViewById(R.id.drawer_icon);
		final TextView textView = (TextView) view.findViewById(R.id.drawer_label);

		iconView.setImageResource(mIcons[position]);
		textView.setText(mLabels[position]);
		return view;
	}
}