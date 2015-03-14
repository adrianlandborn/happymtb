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
import org.pebeijer.happymtb.item.VideoItem;
import org.pebeijer.happymtb.listener.VideoListListener;

public class VideoListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<VideoListListener> mVideoListListenerList;
	private List<VideoItem> mVideoItems = new ArrayList<VideoItem>();
	private int mNumberOfVideoPages = 1;
	private String mSelectedCategory = "Alla";

	public VideoListTask() {
		mVideoListListenerList = new ArrayList<VideoListListener>();
	}

	public void addVideoListListener(VideoListListener l) {
		mVideoListListenerList.add(l);
	}

	public void removeVideoListListener(VideoListListener l) {
		mVideoListListenerList.remove(l);
	}

	public VideoItem ExtractVideoRow(String VideoStr) {
		int Start = VideoStr.indexOf("<a href=\"", 0) + 9;
		int End = VideoStr.indexOf("\">", Start);
		String Link = "http://happymtb.org" + VideoStr.substring(Start, End);
		Start = End;
		
		Start = VideoStr.indexOf("<img src=\"", Start) + 10;
		End = VideoStr.indexOf("\" border", Start);
		String ImgLink = VideoStr.substring(Start, End);
		Start = End;

		Start = VideoStr.indexOf("L�ngd: ", Start) + 7;
		End = VideoStr.indexOf("\" title", Start);
		String Length = "L�ngd: " + VideoStr.substring(Start, End);
		Start = End;
		
		Start = VideoStr.indexOf("\" title=\"", Start) + 9;
		End = VideoStr.indexOf("\" width", Start) - 13;
		String Title = Utilities.ReplaceHTMLChars(VideoStr.substring(Start, End));
		Start = End;		

		Start = VideoStr.indexOf(" - ", Start) + 3;
		End = VideoStr.indexOf("\" width", Start);
		String Date = VideoStr.substring(Start, End);
		Start = End;
		
		Start = VideoStr.indexOf("Uppladdad av ", Start) + 13;
		End = VideoStr.indexOf("\">", Start);
		Start = End + 2;
			
		End = VideoStr.indexOf("</a><br />", Start);
		String Uploader = "Uppladdad av " + Utilities.ReplaceHTMLChars(VideoStr.substring(Start, End));
		Start = End;
		
		String Category = "Kategori: Ingen kategori";
		if (VideoStr.contains("Kategori")) {
			Start = VideoStr.indexOf("\">", Start) + 2;
			End = VideoStr.indexOf("</a><br />", Start);
			Category = "Kategori: " + Utilities.ReplaceHTMLChars(VideoStr.substring(Start, End));
		}
		
		return new VideoItem(Title, Uploader, Category, Length, Date, Link, ImgLink, 1, mSelectedCategory);		
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mVideoItems.clear();
					
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/video/?p="
					+ Integer.toString((Integer) params[0]) + "&c="
					+ Integer.toString((Integer) params[1]) + "&search="
					+ (String) params[2]
					;
					
			HttpGet httpget = new HttpGet(urlStr);
								
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader inputStream = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);

			StringBuilder ksStringBuilder = new StringBuilder();
			String lineString = "";
			VideoItem item;
			boolean StartRead = false;
			boolean StartReadCategory = false;
			boolean StartReadPages = false;
			
			while ((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("<div class=\"videobox")) {
					StartRead = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}
				if (lineString.contains("<div id=\"searchvideo\">")) {
					StartReadCategory = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}			
				if (lineString.contains("<div class=\"pagination\">")) {
					StartReadPages = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}							
				else if (StartRead) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("	</div>")) {
						StartRead = false;
						item = ExtractVideoRow(ksStringBuilder.toString());
						if (item != null) {
							mVideoItems.add(item);
						}
					}
				} else if (StartReadPages) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</div>")) {
						StartReadPages = false;
												
						String str = ksStringBuilder.toString();
						
						int start = 0;
						int end = 0;
						start = str.indexOf("<ul>", 0) + 4;	// Start
						str = str.substring(start, str.length());						
						str = str.replaceAll(" class=\"active\"", "");
						while(str.contains("\">")) {
							start = str.indexOf("\">", 0) + 2;	// Start
							end = str.indexOf("</a>", start);	    // End																					
							
							if (Utilities.isInteger(str.substring(start, end))) {
								if (Integer.parseInt(str.substring(start, end)) > mNumberOfVideoPages) {
									mNumberOfVideoPages = Integer.parseInt(str.substring(start, end));
								}
							}
							str = str.substring(end, str.length());
						}
					}
				} else if (StartReadCategory) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</form>")) {
						StartReadCategory = false;
						
						String str = ksStringBuilder.toString();
						if (str.contains("selected")) {
							int start = str.indexOf("selected>", 0) + 9;	// Start
							int end = str.indexOf("</option>", start);	    // End
							mSelectedCategory = str.substring(start, end);
						}
					}
				}					
			}
					
			for(int i = 0; i < mVideoItems.size(); i++) {
				mVideoItems.get(i).setNumberOfVideoPages(mNumberOfVideoPages);
			}				

		} catch (Exception e) {
			// Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}	
		if (mVideoItems.size() > 0)
			return true;
		else
			return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (VideoListListener l : mVideoListListenerList) {
			if (result) {
				l.Success(mVideoItems);
			} else {
				l.Fail();
			}
		}
	}
}
