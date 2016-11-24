package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.view.CalendarRowView;
import org.happymtb.unofficial.item.CalendarItem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CalendarAdapter extends BaseAdapter {
	private Context mContext;
	private List<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();

	public CalendarAdapter(Context context, List<CalendarItem> CalendarItems) {
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
		CalendarRowView calendarRowView = null;

		if (convertView == null) {
			calendarRowView = new CalendarRowView(mContext);
		} else {
			calendarRowView = (CalendarRowView) convertView;
		}
		
		calendarRowView.setTitle(mCalendarItems.get(position).getTitle());
		calendarRowView.setDescription(mCalendarItems.get(position).getDescription());
		calendarRowView.setCategory(mCalendarItems.get(position).getCategory());
		calendarRowView.setTime(mCalendarItems.get(position).getTime() + ", " + mCalendarItems.get(position).getSelectedRegion());
		
		int indicatorColor;
		
		Log.d("test", mCalendarItems.get(position).getCategory());
		
		if (mCalendarItems.get(position).getCategory().contains("Event/Mässa")) {
			indicatorColor = R.color.calender_event;
		} else if (mCalendarItems.get(position).getCategory().contains("Happyride")) {
			indicatorColor = R.color.calender_happyride;
		} else if (mCalendarItems.get(position).getCategory().contains("Motionslopp/Tävling")) {
			indicatorColor = R.color.calender_motion;
		} else if (mCalendarItems.get(position).getCategory().contains("Svartrejs")) {
			indicatorColor = R.color.calender_svartrejs;
		} else {
			indicatorColor = R.color.calender_ovrigt;
		}
		
		calendarRowView.setRowBackgroundColor(indicatorColor);
		return calendarRowView;
	}

	public void setItems(ArrayList<CalendarItem> items) {
		mCalendarItems = items;
	}
}