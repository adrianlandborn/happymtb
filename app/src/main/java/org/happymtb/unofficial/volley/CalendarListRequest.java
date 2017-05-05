package org.happymtb.unofficial.volley;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.CalendarItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CalendarListRequest extends Request<List<CalendarItem>> implements Response.Listener<List<CalendarItem>> {
	private Response.Listener<List<CalendarItem>> mListener;

	public CalendarListRequest(String url, Response.Listener<List<CalendarItem>> listener, Response.ErrorListener errorListener) {
		super(Method.GET, url, errorListener);

		mListener = listener;
	}

	@Override
	protected Response<List<CalendarItem>> parseNetworkResponse(NetworkResponse response) {
		List<CalendarItem> calendarItems = new ArrayList<>();

		try {
			String htmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			String[] lines = htmlString.split("\\n");

			StringBuilder calendarStringBuilder = new StringBuilder();
			CalendarItem item;			
			boolean startReadMessage = false;

			// TODO Use StringTokenizer?
			for (String lineString : lines) {
				if (lineString.contains("<div style=\"overflow: hidden;\">")) {
					startReadMessage = true;
					calendarStringBuilder = new StringBuilder();
					calendarStringBuilder.append(lineString);
				}
				else if (startReadMessage) {
					calendarStringBuilder.append(lineString);
					if (lineString.contains("<hr>")) {
						startReadMessage = false;
					
						item = ExtractCalendarRow(calendarStringBuilder.toString());
                        if (item != null) {
							calendarItems.add(item);
						}
					}
				}	
			}
			return Response.success(calendarItems, HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return Response.error(new VolleyError(e));
		}
	}

    public void removeListener() {
        mListener = null;
    }

    public CalendarItem ExtractCalendarRow(String Str) {
        String Title;
        String Category;
        String Description;
        String Time;
        String SelectedRegion;
        String Id;

        int Start = 0;
        int End = 0;

        Start = Str.indexOf("<a href=\"./", Start) + 11;
        End = Str.indexOf("/\">", Start);
        Id = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
        Start = End;

        Start = Str.indexOf("<h2>", Start) + 4;
        End = Str.indexOf("</h2>", Start);
        Title = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
        Start = End;

        if (Title.contains("<i class=\"icon-map-marker\"></i>")) {
            Title = HappyUtils.replaceHTMLChars(Title.substring(0, Title.length() - 31));
        }

        Start = Str.indexOf("fc-event-title\">", Start) + 16;
        End = Str.indexOf("</span>", Start);
        Category = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
        Start = End;

        Start = Str.indexOf("</span></span></span> ", Start) + 22;
        End = Str.indexOf("<br />", Start);
        Description = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
        Start = End;

        Start = Str.indexOf("</i> ", Start) + 5;
        End = Str.indexOf("  <i class", Start);
        Time = HappyUtils.replaceHTMLChars(Str.substring(Start, End)).replace("<i class=\"icon-arrow-right\"></i>", "->");
        Start = End;

        Start = Str.indexOf("</i> ", Start) + 5;
        End = Str.indexOf("<br />", Start);
        SelectedRegion = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
        Start = End;

        return new CalendarItem(Title, Description, Category, SelectedRegion, Time, Id);
    }

    @Override
	protected void deliverResponse(List<CalendarItem> items) {
		if (items != null || !items.isEmpty()) {
			mListener.onResponse(items);
		} else {
			getErrorListener().onErrorResponse(new NetworkError());
		}
	}

	@Override
	public void onResponse(List<CalendarItem> response) {
		// Subclasses should implement this
	}
}
