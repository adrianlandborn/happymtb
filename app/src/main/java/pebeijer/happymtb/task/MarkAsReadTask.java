package pebeijer.happymtb.task;

import java.util.ArrayList;

import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import pebeijer.happymtb.listener.MarkAsReadListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class MarkAsReadTask extends AsyncTask<Object, Void, Boolean>  
{	
	private ArrayList<MarkAsReadListener> MarkAsReadListenerList;
	
	public MarkAsReadTask() {
		MarkAsReadListenerList = new ArrayList<MarkAsReadListener>();
	}
	
	public void addMarkAsReadListener(MarkAsReadListener l) {
		MarkAsReadListenerList.add(l);
	}

	public void removeMarkAsReadListener(MarkAsReadListener l) {
		MarkAsReadListenerList.remove(l);
	}
	
	@Override
	protected Boolean doInBackground(Object... param) {	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((Context)param[0]);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        
		try	{	
    		CookieStore cookieStore = httpclient.getCookieStore();
    		BasicClientCookie cookie = new BasicClientCookie(preferences.getString("cookiename", ""), preferences.getString("cookievalue", ""));
    		cookie.setPath("/");
    		cookie.setDomain("happymtb.org");
    		cookieStore.addCookie(cookie);   		
    		String urlStr = "http://happymtb.org/forum/list.php/1/markread";
    		
            HttpGet httpget = new HttpGet(urlStr);
            //HttpResponse response = httpclient.execute(httpget);
            httpclient.execute(httpget);
            
	    	return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (MarkAsReadListener l : MarkAsReadListenerList) {
			if (result)	{
				l.Success();
			} else {
				l.Fail();						
			}					
		}			
    }  	
}
