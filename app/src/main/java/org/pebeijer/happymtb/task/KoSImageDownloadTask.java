package org.pebeijer.happymtb.task;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.pebeijer.happymtb.adapter.ListKoSAdapter;
import org.pebeijer.happymtb.item.KoSItem;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class KoSImageDownloadTask extends AsyncTask<Object, Void, Boolean> {

	ListKoSAdapter KoSAdapter;
	List<KoSItem> mKoSItems = new ArrayList<KoSItem>();

	private Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");			
			return d;
		} catch (Exception e) {
			// System.out.println("Exc="+e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Boolean doInBackground(Object... params) {

		mKoSItems = (List<KoSItem>) params[0];
		KoSAdapter = (ListKoSAdapter) params[1];		
		for (int i = 0; i < mKoSItems.size(); i++) {
			Drawable d = LoadImageFromWebOperations(mKoSItems.get(i).getImgLink());				
			mKoSItems.get(i).setObjectImage(d);
			publishProgress();
		}
		return null;
	}

	protected void onProgressUpdate(Void... values) {
		KoSAdapter.notifyDataSetChanged();
	}

}
