package org.happymtb.unofficial.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import org.happymtb.unofficial.adapter.ListMessagesAdapter;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

public class MessageImageDownloadTask extends AsyncTask<Object, Void, Void> { 
	SpannableStringBuilder htmlSpannable;
	String mBitmapFolder = "/happymtb/"; 
	ListMessagesAdapter mMessagesAdapter;
	List<Message> mMessages;
	Context mContext;
	
	@Override
	protected Void doInBackground(Object... params) {
		mMessages = (List<Message>) params[0];
		mMessagesAdapter = (ListMessagesAdapter) params[1];
		mContext = (Context) params[2];
		
		for (int i = 0; i < mMessages.size(); i++) {
			Spanned spanned = Html.fromHtml(mMessages.get(i).getText());			
			SpannableStringBuilder htmlSpannable = (SpannableStringBuilder) spanned;
			File extStore = Environment.getExternalStorageDirectory();	
			
			for (ImageSpan img : htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class)) {		
				String filename = HappyUtils.getFilename(img.getSource());
				File bitmapFile = new File(extStore.getAbsolutePath() + mBitmapFolder, filename + ".png");	
				if (!bitmapFile.exists()) {					
					Bitmap bitmap = downloadBitmap(img.getSource());					
					try {
						OutputStream outStream = null;
						outStream = new FileOutputStream(bitmapFile);
						bitmap.compress(Bitmap.CompressFormat.PNG, 85, outStream);
						outStream.flush();
						outStream.close();
					} catch(Exception e) {			
					}
				} else {
					//Log.d("ExtractMessage", "file already exist");				
				}
				publishProgress();
			}   			
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		mMessagesAdapter.notifyDataSetChanged();
	}	
	
    private Bitmap downloadBitmap(String url) {
        //TODO  AndroidHttpClient is deprecated from Lollopop MR1
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { 
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
            	return decodeFile(entity.getContent());
            }
        } catch (Exception e) {
            getRequest.abort();
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }    

    private Bitmap decodeFile(InputStream in){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > -1 ) {
               baos.write(buffer, 0, len);
            }
            baos.flush();
            InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
            InputStream is2 = new ByteArrayInputStream(baos.toByteArray()); 

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FlushedInputStream(is1), null, o);

            float scale = (float) o.inTargetDensity / o.inDensity;
            
            int width = (int) (o.outWidth * scale + 0.5f);
            int height = (int) (o.outHeight * scale + 0.5f);
            
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            if(height > 1 || width > 1) {
            	if(height > width) {
            		o2.inSampleSize = height;
            	}else{
            		o2.inSampleSize = width;
            	}            
            }
            final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(is2), null, o2);             
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }    
    
    // An InputStream that skips the exact number of bytes provided, unless it reaches EOF.    
    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }        
}