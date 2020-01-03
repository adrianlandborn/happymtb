package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import android.os.AsyncTask;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Item;
import org.happymtb.unofficial.listener.ItemListListener;

public class ArticlesListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<ItemListListener> mItemListListenerList;
	private ArrayList<Item> mItems = new ArrayList<Item>();
	private String mGroup;

	public ArticlesListTask() {
		mItemListListenerList = new ArrayList<ItemListListener>();
	}

	public void addItemListListener(ItemListListener l) {
		mItemListListenerList.add(l);
	}

	public void removeItemListListener(ItemListListener l) {
		mItemListListenerList.remove(l);
	}

	public Item ExtractArticleRow(String str) {
		int start = str.indexOf("<li>") + 4;
		int end = str.indexOf(":&nbsp", start);
		String description = "Postad den " + str.substring(start, end) + " " + mGroup;
		start = end;
		
		start = str.indexOf(":&nbsp;<a href='", start) + 16;
		end = str.indexOf("' title='", start);
		String link = str.substring(start, end);
		start = end;

		start = str.indexOf("&quot;'>", start) + 8;
		end = str.indexOf("</a></li>", start);
		String title = HappyUtils.replaceHTMLChars(str.substring(start, end));

		return new Item(title, link, description, mGroup, false);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mItems.clear();
		
//		DefaultHttpClient httpclient = new DefaultHttpClient();
//
//		try {
//			String urlStr = "https://happyride.se/arkiv/";
//			HttpGet httpget = new HttpGet(urlStr);
//
//			HttpResponse response = httpclient.execute(httpget);
//			HttpEntity entity = response.getEntity();
//
//			InputStreamReader inputStream = new InputStreamReader(entity.getContent());
//			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);
//
//			Item item;
//			String lineString = "";
//
//			while((lineString = lineNumberReader.readLine()) != null) {
//				if (lineString.contains("monthtitle")) {
//					mGroup = HappyUtils.replaceHTMLChars(lineString.substring(
//                            lineString.indexOf("<strong>", 0) + 8,
//                            lineString.indexOf("</strong>", 0)));
//					item = new Item(mGroup, false);
//					mItems.add(item);
//				}
//				else if (lineString.contains("<li>")) {
//					item = ExtractArticleRow(lineString);
//					if (item != null) {
//						mItems.add(item);
//					}
//				}
//			}
//		} catch (Exception e) {
//			// Log.d("doInBackground", "Error: " + e.getMessage());
//			return false;
//		} finally {
//			httpclient.getConnectionManager().shutdown();
//		}
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (ItemListListener l : mItemListListenerList) {
			if (result) {
				l.success(mItems);
			} else {
				l.fail();
			}
		}
	}
}
