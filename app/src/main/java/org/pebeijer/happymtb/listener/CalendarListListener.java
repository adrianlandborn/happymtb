package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.CalendarItem;

public interface CalendarListListener {
	public void success(List<CalendarItem> calendaritem);
	public void fail();
}
