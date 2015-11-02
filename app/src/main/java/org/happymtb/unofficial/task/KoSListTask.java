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
import org.happymtb.unofficial.item.KoSItem;
import org.happymtb.unofficial.listener.KoSListListener;

public class KoSListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<KoSListListener> mKoSListListenerList;
	private List<KoSItem> mKoSItems = new ArrayList<KoSItem>();
	private int mNumberOfKoSPages = 1;
	private String mSelectedCategory = "Alla Kategorier";
	private String mSelectedRegion = "Hela Sverige";

	public KoSListTask() {
		mKoSListListenerList = new ArrayList<KoSListListener>();
	}

	public void addKoSListListener(KoSListListener l) {
		mKoSListListenerList.add(l);
	}

	public void removeKoSListListener(KoSListListener l) {
		mKoSListListenerList.remove(l);
	}

	public KoSItem extractKoSRow(String KoSStr) {
		String Time;
		String Title;
		String Area;
		String Link;
		String ImgLink;
		String Category;
		String Price;

		int Start = 0;
		int End = 0;

		Start = KoSStr.indexOf("<span class=\"resultline\">", Start) + 25;
		End = KoSStr.indexOf("</span><br />", Start);
		Time = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;		
		
		if (KoSStr.contains("table")) {
			Start = KoSStr.indexOf("src=\"", Start) + 5;
			End = KoSStr.indexOf("\" border=\"0\" />", Start);
			ImgLink = "http://happymtb.org/annonser/" + KoSStr.substring(Start, End);
			Start = End;
		} else {
			ImgLink = "";
		}		
		
		Start = KoSStr.indexOf("<a href=\"", Start) + 9;
		End = KoSStr.indexOf("\"", Start);
		Link = "http://happymtb.org/annonser/" + KoSStr.substring(Start, End);
		Start = End;
		
		Start = KoSStr.indexOf("class=\"resultline_large\">", Start) + 25;
		End = KoSStr.indexOf("</span></a>", Start);
		Title = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;
		
		Start = KoSStr.indexOf("<span class=\"resultline\">", Start) + 25;
		End = KoSStr.indexOf("</span><br />", Start);
		Area = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;
		
		Start = KoSStr.indexOf("<span class=\"resultline\">", Start) + 25;
		End = KoSStr.indexOf("</span><br />", Start);
		Category = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;
		
		Start = KoSStr.indexOf("<span class=\"resultline\">", Start) + 25;
		End = KoSStr.indexOf("</span><br />", Start);
		Price = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		
		return new KoSItem(Time, Title, Area, Link, ImgLink, Category, Price, 0, mSelectedCategory, mSelectedRegion);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		mKoSItems.clear();		
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/annonser/index.php?adpage="
					+ Integer.toString((Integer) params[0]) + "&type="
					+ Integer.toString((Integer) params[1]) + "&region="
					+ Integer.toString((Integer) params[2]) + "&category="
					+ Integer.toString((Integer) params[3]) + "&freetext="					
					+ params[4] + "&sortattribute="
					+ params[5] + "&sortorder="
					+ params[6];

			int mCurrentPage = (Integer) params[0];
			HttpGet httpget = new HttpGet(urlStr);
		
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader is = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(is);
			
			StringBuilder ksStringBuilder = new StringBuilder();
			KoSItem item;			
			boolean StartReadMessage = false;
			boolean StartReadCategory = false;
			boolean StartReadRegion = false;
			String lineString = "";
			
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("<tr style=\"background-color: #fff;\" onmouseover=\"this.style.backgroundColor='#eee';\" onmouseout=\"this.style.backgroundColor='';\">")) {
					StartReadMessage = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}
				else if (lineString.contains("<select id=\"category\" class=\"scat\">")) {
					StartReadCategory = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}
				else if (lineString.contains("<select id=\"region\" class=\"sreg\">")) {
					StartReadRegion = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				}				
				else if (lineString.contains("<span class=\"bold\">")) {
					mNumberOfKoSPages = Integer.parseInt(lineString.substring(
							lineString.indexOf("<span class=\"bold\">", 0) + 19,   // Start
							lineString.indexOf("</span>", 0)));					   // End

					if (mCurrentPage == mNumberOfKoSPages) {
						// If on last page the last item is nog <span class="bold"> but <span class="bold_lightgray">
						// Ugly workaround or this bug... :(
						// +1 is due to offset
						mNumberOfKoSPages = mCurrentPage + 1;

					}
				}
				else if (StartReadMessage) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("	</tr>")) {
						StartReadMessage = false;
					
						item = extractKoSRow(ksStringBuilder.toString());
						if (item != null) {
							mKoSItems.add(item);
						}
					}
				}	
				else if (StartReadCategory) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</select>")) {
						StartReadCategory = false;
					
						String str = ksStringBuilder.toString();
						if (str.contains("selected")) {
							int start = str.indexOf("selected >", 0) + 10;	// Start
							int end = str.indexOf("</option>", start);	    // End
							mSelectedCategory = str.substring(start, end);
						}
					}
				}	
				else if (StartReadRegion) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</select>")) {
						StartReadRegion = false;
					
						String str = ksStringBuilder.toString();
						if (str.contains("selected")) {
							int start = str.indexOf("selected >", 0) + 10;	// Start
							int end = str.indexOf("</option>", start);	    // End
							mSelectedRegion = str.substring(start, end);
						}
					}
				}					
			}
			
			for(int i = 0; i < mKoSItems.size(); i++) {
				mKoSItems.get(i).setNumberOfKoSPages(mNumberOfKoSPages);
			}
			
		} catch (Exception e) {
			// Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return mKoSItems.size() > 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (KoSListListener l : mKoSListListenerList) {
			if (result) {
				l.success(mKoSItems);
			} else {
				l.fail();
			}
		}
	}
}
