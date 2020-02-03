package org.happymtb.unofficial.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.happymtb.unofficial.listener.MarkAsReadListener;

import java.util.ArrayList;

public class MarkAsReadTask extends AsyncTask<Object, Void, Boolean>  
{	
	private ArrayList<MarkAsReadListener> mMarkAsReadListenerList;
	
	public MarkAsReadTask() {
		mMarkAsReadListenerList = new ArrayList<>();
	}
	
	public void addMarkAsReadListener(MarkAsReadListener l) {
		mMarkAsReadListenerList.add(l);
	}

	public void removeMarkAsReadListener(MarkAsReadListener l) {
		mMarkAsReadListenerList.remove(l);
	}
	
	@Override
	protected Boolean doInBackground(Object... param) {	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((Context)param[0]);
//        DefaultHttpClient httpclient = new DefaultHttpClient();
        
//		try	{
//    		CookieStore cookieStore = httpclient.getCookieStore();
//    		BasicClientCookie cookie = new BasicClientCookie(preferences.getString(ForumListFragment.COOKIE_NAME, ""), preferences.getString(ForumListFragment.COOKIE_VALUE, ""));
//    		cookie.setPath("/");
//    		cookie.setDomain("happyride.se");
//    		cookieStore.addCookie(cookie);
//    		String urlStr = "https://happyride.se/forum/list.php/1/markread";
//
//            HttpGet httpget = new HttpGet(urlStr);
//            //HttpResponse response = httpclient.execute(httpget);
//            httpclient.execute(httpget);
//
//	    	return true;
//		} catch (Exception e) {
//		}
			return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (MarkAsReadListener l : mMarkAsReadListenerList) {
			if (result)	{
				l.success();
			} else {
				l.fail();
			}					
		}			
    }  	
}
