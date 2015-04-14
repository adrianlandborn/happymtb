package org.happymtb.unofficial.task;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.adapter.ListVideoAdapter;
import org.happymtb.unofficial.item.VideoItem;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class VideoImageDownloadTask extends AsyncTask<Object, Void, Boolean> {

	ListVideoAdapter mVideoAdapter;
	List<VideoItem> mVideoItems = new ArrayList<VideoItem>();

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

		mVideoItems = (List<VideoItem>) params[0];
		mVideoAdapter = (ListVideoAdapter) params[1];
		for (int i = 0; i < mVideoItems.size(); i++) {
			mVideoItems.get(i).setObjectImage(
					LoadImageFromWebOperations(mVideoItems.get(i).getImgLink()));
			publishProgress();
		}
		return null;
	}

	protected void onProgressUpdate(Void... values) {
		mVideoAdapter.notifyDataSetChanged();
	}

}
