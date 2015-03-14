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
import org.pebeijer.happymtb.item.Home;
import org.pebeijer.happymtb.listener.HomeListListener;

public class HomeListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<HomeListListener> mHomeListListenerList;
	private List<Home> mHomes = new ArrayList<Home>();

	public HomeListTask() {
		mHomeListListenerList = new ArrayList<HomeListListener>();
	}

	public void addHomeListListener(HomeListListener l) {
		mHomeListListenerList.add(l);
	}

	public void removeHomeListListener(HomeListListener l) {
		mHomeListListenerList.remove(l);
	}

	public Home ExtractHomeRow(String Str) {
		int Start = Str.indexOf("<title>", 0) + 7;
		int End = Str.indexOf("</title>", Start);
		String Title = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		Start = Str.indexOf("<description><![CDATA[", Start) + 22;
		End = Str.indexOf("]]></description>", Start);
		String Text = Utilities.ReplaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		Start = Str.indexOf("<link>", Start) + 6;
		End = Str.indexOf("</link>", Start);
		String Link = Str.substring(Start, End);
		
		Start = Link.indexOf("org/", 0) + 4;
		End = Link.indexOf("/", Start);
		String Year = Link.substring(Start, End);
		Start = End;
		
		Start = Link.indexOf("/", Start) + 1;
		End = Link.indexOf("/", Start);
		String Month = Link.substring(Start, End);
		Start = End;

		Start = Link.indexOf("/", Start) + 1;
		End = Link.indexOf("/", Start);
		String Day = Link.substring(Start, End);
				
		String Date = Year + "-" + Month + "-" + Day;
		
		return new Home(Title, Link, Text, Date);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mHomes.clear();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/rss";
			HttpGet httpget = new HttpGet(urlStr);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader inputStream = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);
			
			StringBuilder ksStringBuilder = new StringBuilder();
			Home home;
			boolean StartRead = false;	
			String lineString = "";
			
			while((lineString = lineNumberReader.readLine()) != null) {				
				if (lineString.contains("<item>")) {
					StartRead = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}
				else if (StartRead) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</item>")) {
						StartRead = false;
					
						home = ExtractHomeRow(ksStringBuilder.toString());
						if (home != null) {
							mHomes.add(home);
						}
					}
				}	
			}					
		} catch (Exception e) {
			// Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (HomeListListener l : mHomeListListenerList) {
			if (result) {
				l.Success(mHomes);
			} else {
				l.Fail();
			}
		}
	}
}
