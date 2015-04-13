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

import org.pebeijer.happymtb.helpers.HappyUtils;
import org.pebeijer.happymtb.item.Item;
import org.pebeijer.happymtb.listener.ItemListListener;

public class ShopsListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<ItemListListener> mItemListListenerList;
	private List<Item> mShopItems = new ArrayList<Item>();
	private String mGroup;

	public ShopsListTask() {
		mItemListListenerList = new ArrayList<ItemListListener>();
	}

	public void addItemListListener(ItemListListener l) {
		mItemListListenerList.add(l);
	}

	public void removeItemListListener(ItemListListener l) {
		mItemListListenerList.remove(l);
	}

	public Item ExtractItemRow(String ShopStr) {
		int Start = ShopStr.indexOf("<b>", 0) + 3;
		int End = ShopStr.indexOf("</b>", Start);
		String Title = HappyUtils.replaceHTMLChars(ShopStr.substring(Start, End));
		Start = End;

		Start = ShopStr.indexOf("<a href='", Start) + 9;
		End = ShopStr.indexOf("'", Start);
		String Link = "http://happymtb.org/forum/butiker/"	+ ShopStr.substring(Start, End);
		Start = End;

		Start = ShopStr.indexOf("PhorumSmallFont'>", Start) + 17;
		End = ShopStr.indexOf("</span>", Start);
		String Description = HappyUtils.replaceHTMLChars(ShopStr.substring(Start, End));

		return new Item(Title, Link, Description, mGroup, false);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		mShopItems.clear();

		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		try {
			String urlStr = "http://happymtb.org/forum/butiker/";
			HttpGet httpget = new HttpGet(urlStr);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader inputStream = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);
			
			Item shopItem;
			boolean StartRead = false;		
			String lineString = "";
			
			StringBuilder ksStringBuilder = new StringBuilder();
			
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("RowAlt' ")) {
					StartRead = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				} else if (lineString.contains("Header' nowrap='nowrap' w")) {				
					mGroup = HappyUtils.replaceHTMLChars(lineString.substring(
                            lineString.indexOf("=0'>", 0) + 4,
                            lineString.indexOf("</", 0)));

					shopItem = new Item(mGroup, false);
					mShopItems.add(shopItem);					
				} else if (StartRead) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("</tr>")) {
						StartRead = false;
					
						shopItem = ExtractItemRow(ksStringBuilder.toString());
						if (shopItem != null) {
							mShopItems.add(shopItem);
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
		for (ItemListListener l : mItemListListenerList) {
			if (result) {
				l.success(mShopItems);
			} else {
				l.fail();
			}
		}
	}
}
