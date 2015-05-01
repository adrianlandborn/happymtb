package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.CalendarRowView;
import org.happymtb.unofficial.item.CalendarItem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListCalendarAdapter extends BaseAdapter {
	private Context mContext;
	private List<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();

	public ListCalendarAdapter(Context context, List<CalendarItem> CalendarItems) {
		mContext = context;
		mCalendarItems = CalendarItems;
	}		
	
	@Override
	public int getCount() {
		return mCalendarItems.size();
	}

	@Override
	public CalendarItem getItem(int position) {
		return mCalendarItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CalendarRowView CalendarRowV = null;

		if (convertView == null) {
			CalendarRowV = new CalendarRowView(mContext);
		} else {
			CalendarRowV = (CalendarRowView) convertView;
		}
		
		CalendarRowV.setTitle(mCalendarItems.get(position).getTitle());
		CalendarRowV.setDescription(mCalendarItems.get(position).getDescription());
		CalendarRowV.setCategory(mCalendarItems.get(position).getCategory());
		CalendarRowV.setTime(mCalendarItems.get(position).getTime() + ", " + mCalendarItems.get(position).getSelectedRegion());
		
		int identifier;
		
		Log.d("test", mCalendarItems.get(position).getCategory());
		
		if (mCalendarItems.get(position).getCategory().contains("Event/M�ssa")) {
			identifier = mContext.getResources().getIdentifier("rowshape_event", "drawable","org.happymtb.unofficial");
		} else if (mCalendarItems.get(position).getCategory().contains("Happyride")) {
			identifier = mContext.getResources().getIdentifier("rowshape_happyride", "drawable","org.happymtb.unofficial");
		} else if (mCalendarItems.get(position).getCategory().contains("Motionslopp/T�vling")) {
			identifier = mContext.getResources().getIdentifier("rowshape_motion", "drawable","org.happymtb.unofficial");
		} else if (mCalendarItems.get(position).getCategory().contains("Svartrejs")) {
			identifier = mContext.getResources().getIdentifier("rowshape_svart_rejs", "drawable","org.happymtb.unofficial");
		} else {
			identifier = mContext.getResources().getIdentifier("rowshape_ovrigt", "drawable","org.happymtb.unofficial");
		}
		
		CalendarRowV.setRowBackgroundColor(identifier);		
		return CalendarRowV;
	}

}