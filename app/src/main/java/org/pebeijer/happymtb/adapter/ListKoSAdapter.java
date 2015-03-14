package org.pebeijer.happymtb.adapter;

import java.util.ArrayList;
import java.util.List;

import org.pebeijer.happymtb.view.KoSRowView;
import org.pebeijer.happymtb.item.KoSItem;

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
		KoSRowView KSRowV = null;

		if (convertView == null) {
			KSRowV = new KoSRowView(mContext);
		} else {
			KSRowV = (KoSRowView) convertView;
		}
			
		KSRowV.setTitle(mKoSItems.get(position).getTitle());
		KSRowV.setTime(mKoSItems.get(position).getTime());
		KSRowV.setArea(mKoSItems.get(position).getArea());
		KSRowV.setCategory(mKoSItems.get(position).getCategory());
		KSRowV.setPrice(mKoSItems.get(position).getPrice());
		KSRowV.setObjectImage(mKoSItems.get(position).getObjectImage());

		if (mKoSItems.get(position).getTitle().charAt(0) == 'S'){
			int identifier = mContext.getResources().getIdentifier("rowshapegreen", "drawable","org.pebeijer.happymtb");
			KSRowV.setRowBackgroundColor(identifier);
		} else {
			int identifier = mContext.getResources().getIdentifier("rowshapered", "drawable","org.pebeijer.happymtb");
			KSRowV.setRowBackgroundColor(identifier);
		}		
		
		return KSRowV;
	}

}