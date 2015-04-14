package org.happymtb.unofficial.helpers;

import java.io.File;

import org.happymtb.unofficial.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.Html.ImageGetter;
import android.util.DisplayMetrics;
import android.view.View;

public class URLImageParser implements ImageGetter {
	Context mContext;
	View mView;
	private final FastBitmapDrawable NULL_DRAWABLE = new FastBitmapDrawable(null);		

	public URLImageParser(View t, Context c) {
		mContext = c;
		mView = t;
	}

	public Drawable getDrawable(String source) {		
		DisplayMetrics Metrics = new DisplayMetrics();
		Metrics = mContext.getResources().getDisplayMetrics();
		
		String PATH = "/happymtb/";
		
		Bitmap mDefaultImageBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.no_photo);
		FastBitmapDrawable defaultImage = new FastBitmapDrawable(mDefaultImageBitmap);
			
		String filename = HappyUtils.getFilename(source);
		Drawable d = getCachedBitmap(defaultImage, PATH, filename, Metrics);	
		// next we do some scaling
		int width, height;
		int originalWidthScaled = (int) (d.getIntrinsicWidth() * Metrics.density);
		int originalHeightScaled = (int) (d.getIntrinsicHeight() * Metrics.density);
		if (originalWidthScaled > Metrics.widthPixels) {
			height = d.getIntrinsicHeight() * Metrics.widthPixels / d.getIntrinsicWidth();
			width = Metrics.widthPixels;
		} else {
			height = originalHeightScaled;
			width = originalWidthScaled;
		}
							
		// it's important to call setBounds otherwise the image will
		// have a size of 0px * 0px and won't show at all
		d.setBounds(0, 0, width, height);			
		
		return d;
	}		
	
	// decodes image and scales it to reduce memory consumption
	public static Bitmap decodeBitmapFromFile(File file, DisplayMetrics Metrics) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

		// Find the correct scale value. It should be the power of 2.
		int scale = 1;
		while ((options.outWidth * Metrics.density / scale / 2 >= Metrics.widthPixels)
				&& (options.outHeight * Metrics.density / scale / 2 >= Metrics.heightPixels)) {
			scale *= 2;
		}
		BitmapFactory.Options options2 = new BitmapFactory.Options();
        int w = (int) Math.ceil(options.outWidth / (float)Metrics.widthPixels);
        options2.inSampleSize = w;       
        
		try {
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options2);
			return bitmap;
		 } catch (OutOfMemoryError e) {	 
			 options2.inSampleSize = scale;
			 bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options2);
			 return bitmap; 
		 }
	}	
	
    public Bitmap loadBitmap(String BitmapDirStr, String BitmapFilename, DisplayMetrics Metrics) {
    	File extStore = Environment.getExternalStorageDirectory();
    	File file = new File(extStore.getAbsolutePath() + BitmapDirStr, BitmapFilename + ".png");
       	if (file.exists()) {    
       		Bitmap bitmap = decodeBitmapFromFile(file, Metrics);
       		return bitmap;
       	}else{
       		return null;
       	}
    }	
	
    public FastBitmapDrawable getCachedBitmap(FastBitmapDrawable defaultImage, String BitmapDirStr, String BitmapName, DisplayMetrics Metrics) {
        FastBitmapDrawable drawable = null;
        Bitmap bitmap = loadBitmap(BitmapDirStr, BitmapName, Metrics);
        if (bitmap != null) {  
        	drawable = new FastBitmapDrawable(bitmap);
        } else {
        	drawable = NULL_DRAWABLE;
        }

        return drawable == NULL_DRAWABLE ? defaultImage : drawable;
    } 	
}
