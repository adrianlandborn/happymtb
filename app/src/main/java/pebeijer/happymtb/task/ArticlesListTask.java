package pebeijer.happymtb.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import pebeijer.happymtb.helpers.Utilities;
import pebeijer.happymtb.item.Item;
import pebeijer.happymtb.listener.ItemListListener;

public class ArticlesListTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<ItemListListener> mItemListListenerList;
	private List<Item> mItems = new ArrayList<Item>();
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

	public Item ExtractArticleRow(String Str) {	
		int Start = Str.indexOf("<li>", 0) + 4;
		int End = Str.indexOf(":&nbsp", Start);
		String Description = "Postad den " + Str.substring(Start, End) + " " + mGroup;
		Start = End;
		
		Start = Str.indexOf(":&nbsp;<a href='", Start) + 16;
		End = Str.indexOf("' title='", Start);
		String Link = Str.substring(Start, End);
		Start = End;

		Start = Str.indexOf("&quot;'>", Start) + 8;
		End = Str.indexOf("</a></li>", Start);
		String Title = Utilities.ReplaceHTMLChars(Str.substring(Start, End));

		return new Item(Title, Link, Description, mGroup, false);
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		mItems.clear();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = "http://happymtb.org/arkiv/";
			HttpGet httpget = new HttpGet(urlStr);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader inputStream = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);
		
			Item item;
			String lineString = "";	
			
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("monthtitle")) {
					mGroup = Utilities.ReplaceHTMLChars(lineString.substring(
							lineString.indexOf("<strong>", 0) + 8,
							lineString.indexOf("</strong>", 0)));	
					item = new Item(mGroup, false);
					mItems.add(item);					
				}
				else if (lineString.contains("<li>")) {
					item = ExtractArticleRow(lineString);
					if (item != null) {
						mItems.add(item);
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
				l.Success(mItems);
			} else {
				l.Fail();
			}
		}
	}
}
