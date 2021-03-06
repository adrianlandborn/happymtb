package org.happymtb.unofficial.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Message;
import org.happymtb.unofficial.listener.MessageListListener;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class MessageListTask extends AsyncTask<Object, Void, Boolean>  
{
	private Context mContext;
	private ArrayList<MessageListListener> mMessageListListenerList;
	private List<Message> mMessages = new ArrayList<>();
	int mNumberOfMessagePages = 1;
	private static String mBitmapFolder = "/happymtb/";
	
	public MessageListTask() {
		mMessageListListenerList = new ArrayList<>();
	}
	
	public void addMessageListListener(MessageListListener l) {
		mMessageListListenerList.add(l);
	}

	public void removeMessageListListener(MessageListListener l) {
		mMessageListListenerList.remove(l);
	}		
	
	private Message ExtractMessage(String Str) {
		int Start;
		int End;
		
      	if (Str.contains("PhorumReadBodySubject")) {
    		Start = Str.indexOf("PhorumReadBodySubject\">") + 23;
    		End = Str.indexOf("<span", Start);      		
      	} else {
    		Start = Str.indexOf("<strong>") + 8;
    		End = Str.indexOf("</strong>", Start);      	      	
      	}
      		
		String Title = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
	
		Start = Str.indexOf("<strong>", Start) + 8;
		End = Str.indexOf("</strong>", Start);        	
      	String By = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
      	Start = End;

/*      	
      	if (By.contains("<a href=")) {
      		int SubStart = By.indexOf("\">", 0) + 2;
      		int SubEnd = By.indexOf("</a>", 0);
      		By = Utilities.replaceHTMLChars(By.substring(SubStart, SubEnd).trim());
      	}         	      	
*/
		Start = Str.indexOf("Datum: ", Start) + 7;
		End = Str.indexOf("</div>", Start);        	
      	String Date = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
      	Start = End;
      	
		Start = Str.indexOf("PhorumReadBodyText\">", Start) + 20;
		End = Str.indexOf("</div>", Start);        	
      	String Text = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
	/*	      	
		Spanned spanned = Html.fromHtml(Text);			
		SpannableStringBuilder htmlSpannable = (SpannableStringBuilder) spanned;
		File extStore = Environment.getExternalStorageDirectory();
		
		for (ImageSpan img : htmlSpannable.getSpans(0, htmlSpannable.length(), ImageSpan.class)) {		
			String filename = Utilities.getFilename(img.getSource());
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
				Log.d("ExtractMessage", "file already exist");				
			}
		}      	
*/      	      	      
		return new Message(Title, Text, By, Date, mNumberOfMessagePages);
	}	
	
	@Override
	protected Boolean doInBackground(Object... param) {  
		mContext = (Context)param[0];
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

//		DefaultHttpClient httpclient = new DefaultHttpClient();
    	try	{
//    		CookieStore cookieStore = httpclient.getCookieStore();
//    		BasicClientCookie cookie = new BasicClientCookie(preferences.getString(ForumListFragment.COOKIE_NAME, ""),
//					preferences.getString(ForumListFragment.COOKIE_VALUE, ""));
//    		cookie.setPath("/");
//    		cookie.setDomain("happyride.se");
//    		cookieStore.addCookie(cookie);
//
//        	File extStore = Environment.getExternalStorageDirectory();
//    		File bitmapDirectory = new File(extStore.getAbsolutePath() + mBitmapFolder);
//    		if(!bitmapDirectory.exists() || !bitmapDirectory.isDirectory()) {
//    			bitmapDirectory.mkdirs();
//    		}
//
//    		String urlStr = "https://happyride.se/forum/read.php/1/" + param[1] + "/page=" + param[2];
//            HttpGet httpget = new HttpGet(urlStr);
//
//            HttpResponse response = httpclient.execute(httpget);
//            HttpEntity entity = response.getEntity();
//
//			InputStreamReader inputStream = new InputStreamReader(entity.getContent());
//			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);

			LineNumberReader lineNumberReader = null;
			Message item;
			StringBuilder ksStringBuilder = new StringBuilder();
			String lineString = "";
    		boolean read = false;    		
    		
    		while((lineString = lineNumberReader.readLine()) != null) {
   			
    			if (lineString.contains("Aktuell sida: </span>")) {    	    
    				mNumberOfMessagePages = Integer.parseInt(lineString.substring(lineString.indexOf("av ") + 3));
    			} else if (lineString.contains("PhorumReadMessageBlock")) {
    				read = true;   
    				ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
    			} else if (read) {
    				ksStringBuilder.append(lineString);
    				if (lineString.contains("PhorumReadNavBlock")) {
    					item = ExtractMessage(ksStringBuilder.toString());
    	    			if (item != null) {
    	    				mMessages.add(item);
    	    			}
    					read = false;
    				}
    			}    			        			
    		}
        } catch (Exception e) {
    		//Log.d("doInBackground", "Error: " + e.getLocalizedMessage());
		} finally {
//    		httpclient.getConnectionManager().shutdown();
        }

		return mMessages.size() != 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (MessageListListener l : mMessageListListenerList) {
			if (result) {
				l.success(mMessages);
			} else {
				l.fail();
			}					
		}
    }
	
/*	
    private Bitmap downloadBitmap(String url) {
		DisplayMetrics Metrics = new DisplayMetrics();
		Metrics = mContext.getResources().getDisplayMetrics();
		
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
        		BitmapFactory.Options options = new BitmapFactory.Options();
        		options.inJustDecodeBounds = true;        		
            	BitmapFactory.decodeFile(url, options);        		
                int w = (int) Math.ceil(options.outWidth / (float)Metrics.widthPixels);
                
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = w;                     
            	
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent(); 
                    final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream), null, options2);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();  
                    }
                    entity.consumeContent();
                }
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
    */
}
