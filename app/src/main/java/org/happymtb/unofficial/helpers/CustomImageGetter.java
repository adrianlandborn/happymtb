package org.happymtb.unofficial.helpers;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.text.Html;

public class CustomImageGetter implements Html.ImageGetter {
	@Override
	public Drawable getDrawable(String source) {
		Drawable d = null;
		try {
			URL myFileUrl = new URL(source);
			int i = 0;
			while ((d == null) && (i < 20)) {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();

				if (i > 0) {
					ResponseCache.setDefault(null);
					conn.setUseCaches(false);
				} else {
					conn.setUseCaches(true);
				}
				conn.connect();
				InputStream is = conn.getInputStream();
				d = Drawable.createFromStream(is, "src name");
				i++;
			}
		} catch (Exception e) {
		}

		if (d != null) {
			int Width = d.getIntrinsicWidth();
			int Height = d.getIntrinsicHeight();			
			d.setBounds(0, 0, Width, Height);			
		}
		ResponseCache.setDefault(new CCResponseCache());
		return d;
	}
}