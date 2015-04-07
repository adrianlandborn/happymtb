package org.pebeijer.happymtb.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import org.pebeijer.happymtb.helpers.HappyUtils;
import org.pebeijer.happymtb.item.KoSObjectItem;
import org.pebeijer.happymtb.listener.KoSObjectListener;

public class KoSObjectTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<KoSObjectListener> mKoSObjectListenerList;
	private KoSObjectItem mKoSObjectItem;

	public KoSObjectTask() {
		mKoSObjectListenerList = new ArrayList<KoSObjectListener>();
	}

	public void addKoSObjectListener(KoSObjectListener l) {
		mKoSObjectListenerList.add(l);
	}

	public void removeKoSObjectListener(KoSObjectListener l) {
		mKoSObjectListenerList.remove(l);
	}

	public KoSObjectItem ExtractKoSObject(String Str) {
		String ImgLink = null;		
		
		int Start = Str.indexOf("\"header\">", 0) + 9;
		int End = Str.indexOf("</span>", Start);
		String Area = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		Start = Str.indexOf("\"header\">", Start) + 9;
		End = Str.indexOf("</span>", Start);
		Start = End;		
		
		Start = Str.indexOf("\"header\">", Start) + 9;
		End = Str.indexOf("</span>", Start);		
		String Type = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		Start = Str.indexOf("\"header\">", Start) + 9;
		End = Str.indexOf("</span>", Start);
		Start = End;
		
		Start = Str.indexOf("\"header\">", Start) + 9;
		End = Str.indexOf("</span>", Start);		
		String Title = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;		
		
		Start = Str.indexOf("\"bold\">", Start) + 7;
		End = Str.indexOf("</span>", Start);		
		String Person = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;		
		
		Start = Str.indexOf("<strong>", Start) + 8;
		End = Str.indexOf("</strong>", Start);		
		String Phone = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;			
			
		Start = Str.indexOf("\"bold\">", Start) + 7;
		End = Str.indexOf("</span>", Start);		
		String Date = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;	
		
		if (Str.contains("<img")) {
			Start = Str.indexOf("src=\"", Start) + 5;
			End = Str.indexOf("\" border=", Start);		
			ImgLink = "http://happymtb.org/annonser/" + Str.substring(Start, End);
			Start = End;				
		}
			
		Start = Str.indexOf("\"plain\">", Start) + 8;
		End = Str.indexOf("</span>", Start);		
		String Text = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;			
		
		String Price = "";		
		if (Str.contains("Prisuppgift saknas.")) {
			Price = "Prisuppgift saknas.";
		} else {
			Start = Str.indexOf("\"bold\">", Start) + 7;
			End = Str.indexOf("</span>", Start);		
			Price = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		}

		return new KoSObjectItem(Area, Type, Title, Person, Phone, Date, ImgLink, Text, Price);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = (String) params[0];
			if (urlStr == "")
				return false;
			HttpGet httpget = new HttpGet(urlStr);
				
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader is = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(is);
			
			StringBuilder ksStringBuilder = new StringBuilder();
			boolean StartRead = false;
			String lineString = "";
				
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("viewheader")) {
					StartRead = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				} else if (StartRead) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("<span class=\"link\">")) {
						StartRead = false;
						mKoSObjectItem = ExtractKoSObject(ksStringBuilder.toString());
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
		for (KoSObjectListener l : mKoSObjectListenerList) {
			if (result) {
				l.success(mKoSObjectItem);
			} else {
				l.fail();
			}
		}
	}
}
