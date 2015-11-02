package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.AsyncTask;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.CalendarItem;
import org.happymtb.unofficial.listener.CalendarListListener;

public class CalendarListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<CalendarListListener> mCalendarListListenerList;
	private ArrayList<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();

	public CalendarListTask() {
		mCalendarListListenerList = new ArrayList<CalendarListListener>();
	}

	public void addCalendarListListener(CalendarListListener l) {
		mCalendarListListenerList.add(l);
	}

	public void removeCalendarListListener(CalendarListListener l) {
		mCalendarListListenerList.remove(l);
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
	protected Boolean doInBackground(Object... params) {
		mCalendarItems.clear();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/kalender/?list=1"
					+ "&search=" + params[0]
					+ "&r=" + params[1]
					+ "&c=" + params[2];
			
			HttpGet httpget = new HttpGet(urlStr);
		
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader is = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(is);
			
			StringBuilder calendarStringBuilder = new StringBuilder();
			CalendarItem item;			
			boolean startReadMessage = false;
			String lineString = "";		
			
			while((lineString = lineNumberReader.readLine()) != null) {
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
							mCalendarItems.add(item);
						}
					}
				}	
			}			
		} catch (Exception e) {
			// Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return mCalendarItems.size() > 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (CalendarListListener l : mCalendarListListenerList) {
			if (result) {
				l.success(mCalendarItems);
			} else {
				l.fail();
			}
		}
	}
}
