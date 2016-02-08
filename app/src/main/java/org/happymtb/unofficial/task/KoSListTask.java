package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Region;
import android.os.AsyncTask;
import android.util.Log;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.listener.KoSListListener;

public class KoSListTask extends AsyncTask<Object, Void, Boolean> {
    private static final String TAG = "happyride";
	private ArrayList<KoSListListener> mKoSListListenerList;
	private List<KoSListItem> mKoSListItems = new ArrayList<KoSListItem>();
	private int mNumberOfKoSPages = 1;
	private String mSelectedCategory = "Alla Kategorier";
	private String mSelectedRegion = "Hela Sverige";

	public KoSListTask() {
		mKoSListListenerList = new ArrayList<KoSListListener>();
	}

    private int getStart(String searchString, String searchValue, int oldStart) {
        try {
            return searchString.indexOf(searchValue, oldStart) + searchValue.length();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cannot find: " + searchValue);
        }
        return oldStart;
    }

	public void addKoSListListener(KoSListListener l) {
		mKoSListListenerList.add(l);
	}

	public void removeKoSListListener(KoSListListener l) {
		mKoSListListenerList.remove(l);
	}

	public KoSListItem extractKoSRow(String KoSStr) {
		long id;
		String Type ="";
		String Time ="";
		String Title;
		String Area ="";
		String Link;
		String ImgLink;
		String Category ="";
		String Price ="";

		int Start = 0;
		int End = 0;

        //ImageLink
//        Start = getStart(KoSStr, "src=\"", Start);
        Start = KoSStr.indexOf("src=\"", Start) + 5;
        End = KoSStr.indexOf("\" border=\"0\" ", Start);
        ImgLink = "http://happyride.se" + KoSStr.substring(Start, End);
        Start = End;

        //Link
		Start = KoSStr.indexOf("<a href=\"", Start) + 9;
		End = KoSStr.indexOf("\"", Start);
		Link = "http://happyride.se/annonser/" + KoSStr.substring(Start, End);
		Start = End;

        //Title
		Start = Start + 2;
		End = KoSStr.indexOf("</a></h4>", Start);
        Title = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;

        //Type
        Start = Start + 1;
		End = KoSStr.indexOf("<span class=\"visible-xs\">", Start);
		String typeWithRegion = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Type = typeWithRegion.split(" i ")[0].contains("Säljes") ? KoSListItem.TYPE_SALJES : KoSListItem.TYPE_KOPES;

        //Area
		Area = typeWithRegion.split(" i ")[1].trim();
		Start = End;

        //Price
		Start = KoSStr.indexOf("<span class=\"visible-xs\">", Start) + 25;
		End = KoSStr.indexOf("</span></td>", Start);
		Price = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
        Start = End;

        //Category
		Start = KoSStr.indexOf("<td class=\"col-3 hidden-xs\">", Start) + 28;
		End = KoSStr.indexOf("</td>", Start);
		Category = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
		Start = End;

        //Time
        Start = KoSStr.indexOf("<td class=\"hidden-xs\"><nobr>", Start) + 28;
        End = KoSStr.indexOf("</nobr></td>", Start);
        Time = HappyUtils.replaceHTMLChars(KoSStr.substring(Start, End));
        Start = End;

		id = Long.parseLong(Link.split("id=")[1]);
		
		return new KoSListItem(id, Time, Type, Title, Area, Link, ImgLink, Category, Price, 0, mSelectedCategory, mSelectedRegion);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		mKoSListItems.clear();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();


        // //?search=&category=1&county=&type=1&category2=&county2=&type2=&price=3&year=2013&p=1&sortattribute=creationdate&sortorder=DESC
		try {
			String urlStr = "http://happyride.se/annonser/"
                    + "?search="    + params[0]
                    + "&category="  + Integer.toString((Integer) params[1])
                    + "&county="    + Integer.toString((Integer) params[2])
                    + "&type="      + Integer.toString((Integer) params[3])
                    + "&category2=" + params[4]
                    + "&county2="   + params[5]
                    + "&type2="     + params[6]
                    + "&price="     + params[7]
                    + "&year="      + params[8]
                    + "&p="         + Integer.toString((Integer) params[9])
                    + "&sortattribute=" + params[10]
                    + "&sortorder=" + params[11];

			int mCurrentPage = (Integer) params[9];
			HttpGet httpget = new HttpGet(urlStr);

			System.out.println(urlStr);
		
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
				if (lineString.contains("<td class=\"col-1\">")) {
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
					if (lineString.contains("</tr>")) {
						StartReadMessage = false;
                        String row = ksStringBuilder.toString();
						item = extractKoSRow(row);
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
            Log.d("doInBackground", "Error: " + e.getMessage());
            e.printStackTrace();
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
