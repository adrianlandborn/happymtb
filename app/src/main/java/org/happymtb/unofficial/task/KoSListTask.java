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
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.listener.KoSListListener;

public class KoSListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<KoSListListener> mKoSListListenerList;
	private List<KoSListItem> mKoSListItems = new ArrayList<KoSListItem>();
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

	public KoSListItem extractKoSRow(String KoSStr) {
		long id;
		String type;
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
		String titleWithType = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		type = titleWithType.split(": ")[0].equals("S") ? KoSListItem.TYPE_SALJES : KoSListItem.TYPE_KOPES;
		Title = titleWithType.split(": ")[1].trim();
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

		id = Long.parseLong(Link.split("id=")[1]);
		
		return new KoSListItem(id, Time, type, Title, Area, Link, ImgLink, Category, Price, 0, mSelectedCategory, mSelectedRegion);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		mKoSListItems.clear();
		
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
			KoSListItem item;
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
							mKoSListItems.add(item);
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
			
			for(int i = 0; i < mKoSListItems.size(); i++) {
				mKoSListItems.get(i).setNumberOfKoSPages(mNumberOfKoSPages);
			}
			
		} catch (Exception e) {
			// Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return mKoSListItems.size() > 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (KoSListListener l : mKoSListListenerList) {
			if (result) {
				l.success(mKoSListItems);
			} else {
				l.fail();
			}
		}
	}
}
