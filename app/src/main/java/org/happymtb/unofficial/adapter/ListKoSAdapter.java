package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.view.KoSRowView;
import org.happymtb.unofficial.item.KoSItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListKoSAdapter extends BaseAdapter {
	private Context mContext;
	private List<KoSItem> mKoSItems = new ArrayList<KoSItem>();

	public ListKoSAdapter(Context context, List<KoSItem> KoSItems) {
		mContext = context;
		mKoSItems = KoSItems;
	}		
	
	@Override
	public int getCount() {
		return mKoSItems.size();
	}

	@Override
	public KoSItem getItem(int position) {
		return mKoSItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		KoSRowView kosRowView = null;

		if (convertView == null) {
			kosRowView = new KoSRowView(mContext);
		} else {
			kosRowView = (KoSRowView) convertView;
		}
			
		kosRowView.setTitle(mKoSItems.get(position).getTitle());
		kosRowView.setTime(mKoSItems.get(position).getTime());
		kosRowView.setArea(mKoSItems.get(position).getArea());
		kosRowView.setCategory(mKoSItems.get(position).getCategory());
		kosRowView.setPrice(mKoSItems.get(position).getPrice());
		kosRowView.setObjectImage(mKoSItems.get(position).getObjectImage());

		if (mKoSItems.get(position).getTitle().charAt(0) == 'S'){
			kosRowView.setRowBackgroundColor(R.drawable.rowshape_green);
		} else {
			kosRowView.setRowBackgroundColor(R.drawable.rowshape_red);
		}		
		
		return kosRowView;
	}

}