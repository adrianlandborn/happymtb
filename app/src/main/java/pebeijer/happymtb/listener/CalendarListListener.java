package pebeijer.happymtb.listener;
import java.util.List;

import pebeijer.happymtb.item.CalendarItem;

public interface CalendarListListener {
	public void Success(List<CalendarItem> calendaritem);	
	public void Fail();
}
