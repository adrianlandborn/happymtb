package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.CalendarItem;

import java.util.ArrayList;

public interface CalendarListListener {
	void success(ArrayList<CalendarItem> calendaritem);
	void fail();
}
