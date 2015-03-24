package org.pebeijer.happymtb.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import android.os.AsyncTask;
import org.pebeijer.happymtb.helpers.Utilities;
import org.pebeijer.happymtb.item.CalendarItem;
import org.pebeijer.happymtb.listener.CalendarListListener;

public class CalendarListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<CalendarListListener> mCalendarListListenerList;
	private List<CalendarItem> mCalendarItems = new ArrayList<CalendarItem>();

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
		Id = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;			
		
		Start = Str.indexOf("<h2>", Start) + 4;
		End = Str.indexOf("</h2>", Start);
		Title = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;		
		
		if (Title.contains("<i class=\"icon-map-marker\"></i>")) {
			Title = Utilities.ReplaceHTMLChars(Title.substring(0, Title.length() - 31));
		}
		
		Start = Str.indexOf("fc-event-title\">", Start) + 16;
		End = Str.indexOf("</span>", Start);
		Category = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;		

		Start = Str.indexOf("</span></span></span> ", Start) + 22;
		End = Str.indexOf("<br />", Start);
		Description = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;		

		Start = Str.indexOf("</i> ", Start) + 5;
		End = Str.indexOf("  <i class", Start);
		Time = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;		
		
		Start = Str.indexOf("</i> ", Start) + 5;
		End = Str.indexOf("<br />", Start);
		SelectedRegion = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;			
		
		return new CalendarItem(Title, Description, Category, SelectedRegion, Time, Id);
	}	
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mCalendarItems.clear();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/kalender/?list=1"
					+ "&search=" + (String) params[0]
					+ "&r=" + (String) params[1] 
					+ "&c=" + (String) params[2];					
			
			HttpGet httpget = new HttpGet(urlStr);
		
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader is = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(is);
			
			StringBuilder CalendarStringBuilder = new StringBuilder();
			CalendarItem item;			
			boolean StartReadMessage = false;
			String lineString = "";		
			
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("<div style=\"overflow: hidden;\">")) {
					StartReadMessage = true;
					CalendarStringBuilder = new StringBuilder();
					CalendarStringBuilder.append(lineString);
				}
				else if (StartReadMessage) {
					CalendarStringBuilder.append(lineString);
					if (lineString.contains("<hr>")) {
						StartReadMessage = false;
					
						item = ExtractCalendarRow(CalendarStringBuilder.toString());
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
		if (mCalendarItems.size() > 0)
			return true;
		else
			return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (CalendarListListener l : mCalendarListListenerList) {
			if (result) {
				l.Success(mCalendarItems);
			} else {
				l.Fail();
			}
		}
	}
}