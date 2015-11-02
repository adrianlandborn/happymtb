package org.happymtb.unofficial.listener;
import java.util.ArrayList;

import org.happymtb.unofficial.item.CalendarItem;

public interface CalendarListListener {
	void success(ArrayList<CalendarItem> calendaritem);
	void fail();
}
