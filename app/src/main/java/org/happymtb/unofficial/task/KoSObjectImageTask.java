package org.happymtb.unofficial.task;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class KoSObjectImageTask extends AsyncTask<Void, Void, Bitmap> {

	ImageView mImageView = null;
	String mTag;

	public KoSObjectImageTask(ImageView imageView) {
		mImageView = imageView;
		mTag = (String) mImageView.getTag();
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
	    return downloadImage(mTag);
	}

    @Override
	protected void onPostExecute(Bitmap result) {
	    mImageView.setImageBitmap(result);
	    mImageView.setVisibility(View.VISIBLE);
	}

	private Bitmap downloadImage(String url) {

        Bitmap bmp = null;
        try {
            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return bmp;

            } catch (Exception e){}
        return bmp;		
	}
}	