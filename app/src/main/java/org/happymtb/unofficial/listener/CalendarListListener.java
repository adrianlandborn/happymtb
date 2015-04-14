package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.CalendarItem;

public interface CalendarListListener {
	public void success(List<CalendarItem> calendaritem);
	public void fail();
}
