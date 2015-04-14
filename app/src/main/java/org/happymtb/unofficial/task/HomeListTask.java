package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Home;
import org.happymtb.unofficial.listener.HomeListListener;

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

	public Home ExtractHomeRow(String str) {
		int start = str.indexOf("<title>", 0) + 7;
		int end = str.indexOf("</title>", start);
		String title = HappyUtils.replaceHTMLChars(str.substring(start, end));
		start = end;
		
		start = str.indexOf("<description><![CDATA[", start) + 22;
		end = str.indexOf("]]></description>", start);
		String text = HappyUtils.replaceHTMLChars(str.substring(start, end));
		start = end;
		
		start = str.indexOf("<link>", start) + 6;
		end = str.indexOf("</link>", start);
		String link = str.substring(start, end);
		
		start = link.indexOf("org/", 0) + 4;
		end = link.indexOf("/", start);
		String year = link.substring(start, end);
		start = end;
		
		start = link.indexOf("/", start) + 1;
		end = link.indexOf("/", start);
		String month = link.substring(start, end);
		start = end;

		start = link.indexOf("/", start) + 1;
		end = link.indexOf("/", start);
		String day = link.substring(start, end);
				
		String date = year + "-" + month + "-" + day;
		
		return new Home(title, link, text, date);
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
				l.success(mHomes);
			} else {
				l.fail();
			}
		}
	}
}
