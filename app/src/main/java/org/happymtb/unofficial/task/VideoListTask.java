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
import org.happymtb.unofficial.item.VideoItem;
import org.happymtb.unofficial.listener.VideoListListener;

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
		int start = VideoStr.indexOf("<a href=\"", 0) + 9;
		int end = VideoStr.indexOf("\">", start);
		String link = "https://happyride.se" + VideoStr.substring(start, end);
		start = end;
		
		start = VideoStr.indexOf("<img src=\"", start) + 10;
		end = VideoStr.indexOf("\" border", start);
		String imgLink = VideoStr.substring(start, end);
		start = end;

		start = VideoStr.indexOf("duration\">", start) + 10;
		end = VideoStr.indexOf("</div>", start);
		String length = VideoStr.substring(start, end);
		start = end;
		
		start = VideoStr.indexOf("class=\"title\">", start) + 14;
		end = VideoStr.indexOf("</a><br />", start);
		String Title = HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
		start = end;

//		start = VideoStr.indexOf(" - ", start) + 3;
//		end = VideoStr.indexOf("\" width", start);
//		String date = VideoStr.substring(start, end);
//		start = end;
		
		start = VideoStr.indexOf("Uppladdad av ", start) + 13;
		end = VideoStr.indexOf("\">", start) + 2;
		start = end;
			
		end = VideoStr.indexOf("</a><br />", start);
		String uploader = "Uppladdad av " + HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
		start = end;
		
		String category = "Ingen kategori";
		if (VideoStr.contains("Kategori")) {
			start = VideoStr.indexOf("\">", start) + 2;
			end = VideoStr.indexOf("</a><br />", start);
			category = /*"Kategori: " +*/ HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
		}
		
		return new VideoItem(Title, uploader, category, length, "" /*date*/, link, imgLink, 1, mSelectedCategory);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mVideoItems.clear();
					
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "https://happyride.se/video/?p="
					+ Integer.toString((Integer) params[0]) + "&c="
					+ Integer.toString((Integer) params[1]) + "&search="
					+ params[2];
					
			HttpGet httpget = new HttpGet(urlStr);
								
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader inputStream = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);

			StringBuilder ksStringBuilder = new StringBuilder();
			String lineString = "";
			VideoItem item;
			boolean startRead = false;
			boolean startReadCategory = false;
			boolean startReadPages = false;
			
			while ((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("<div class=\"videobox") && !lineString.contains("<div class=\"videobox\"></div>")) {
					startRead = true;
					ksStringBuilder = new StringBuilder();
//					ksStringBuilder.append(lineString);
				}
				if (lineString.contains("<div id=\"searchvideo\">")) {
					startReadCategory = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}			
				if (lineString.contains("<ul class=\"pagination\">")) {
					startReadPages = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				} else if (startRead) {
					ksStringBuilder.append(lineString);
					if (lineString.equalsIgnoreCase("\t</div>")) {
						startRead = false;
						String s = ksStringBuilder.toString();
						item = ExtractVideoRow(s);
						if (item != null) {
							mVideoItems.add(item);
						}
					}
				} else if (startReadPages) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</div>")) {
						startReadPages = false;
												
						String str = ksStringBuilder.toString();
						
						int start = 0;
						int end = 0;
						start = str.indexOf("<ul>", 0) + 4;	// Start
						str = str.substring(start, str.length());						
						str = str.replaceAll(" class=\"active\"", "");
						while(str.contains("\">")) {
							start = str.indexOf("\">", 0) + 2;	// Start
							end = str.indexOf("</a>", start);	    // End																					
							
							if (HappyUtils.isInteger(str.substring(start, end))) {
								if (Integer.parseInt(str.substring(start, end)) > mNumberOfVideoPages) {
									mNumberOfVideoPages = Integer.parseInt(str.substring(start, end));
								}
							}
							str = str.substring(end, str.length());
						}
					}
				} else if (startReadCategory) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</form>")) {
						startReadCategory = false;
						
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
		return mVideoItems.size() > 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (VideoListListener l : mVideoListListenerList) {
			if (result) {
				l.success(mVideoItems);
			} else {
				l.fail();
			}
		}
	}
}
