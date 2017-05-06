package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.fragment.ForumListFragment;
import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Thread;
import org.happymtb.unofficial.listener.ThreadListListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class ThreadListTask extends AsyncTask<Object, Void, Boolean>  
{
	private ArrayList<ThreadListListener> mThreadListListenerList;
	private List<Thread> mThreads = new ArrayList<Thread>();
	private int mNumberOfThreadPages;
	
	public ThreadListTask() {
		mThreadListListenerList = new ArrayList<ThreadListListener>();
	}
	
	public void addThreadListListener(ThreadListListener l) {
		mThreadListListenerList.add(l);
	}

	public void removeThreadListListener(ThreadListListener l) {
		mThreadListListenerList.remove(l);
	}

	public Thread ExtractThreadRow(String Str) {
		Boolean NewMsg = false;
		//Log.d("ExtractThreadRow", "" + Str);
		if (Str.contains("gotonewpost")) {
			NewMsg = true;
			//Log.d("ExtractThreadRow", "NewMsg = True");
		} else {
			//Log.d("ExtractThreadRow", "NewMsg = False");
		}

		int Start = Str.indexOf("<a onmouseover=\"return escape('", 0) + 31;
		int End = Str.indexOf("')\" href=\"", Start);
		String MessageText = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		Start = Str.indexOf("href=\"https://happyride.se/forum/read.php/1/", Start) + 43;
		End = Str.indexOf("\">", Start);     
		String ThreadId = Str.substring(Start, End); 
		Start = End;
		
		Start = Str.indexOf("\">", Start) + 2;
		End = Str.indexOf("</a>", Start);        	
      	String Title = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
		int NumberOfPages = 1;
      	if (Str.contains("Sida:")) {
      			End = Str.indexOf("</a></span>", Start); 
      			Start = Str.indexOf("\">", End - 20) + 2;
      			NumberOfPages = Integer.parseInt(Str.substring(Start, End));
      			Start = End;
      	}		
      	
		Start = Str.indexOf("nowrap=\"nowrap\">", Start) + 16;
		End = Str.indexOf("&nbsp;</td>", Start);        	
      	int NumberOfMessages = Integer.parseInt(Str.substring(Start, End));  
		Start = End;
		
		Start = Str.indexOf("\"nowrap\">", Start) + 9;
      	End = Str.indexOf("&nbsp;</td>", Start);  
		String StartedBy = HappyUtils.replaceHTMLChars(Str.substring(Start, End));
		Start = End;
		
      	if (StartedBy.contains("<a href="))	{
      		int SubStart = StartedBy.indexOf("\">", 0) + 2;
      		int SubEnd = StartedBy.indexOf("</a>", 0);
          	StartedBy = HappyUtils.replaceHTMLChars(StartedBy.substring(SubStart, SubEnd));
      	}   				      	
      	
		Start = Str.indexOf("\"nowrap\">", Start) + 9;
      	End = Str.indexOf("</a>", Start);  
		String LastMessageTime = Str.substring(Start, End);
		Start = End;      	
      			
      	if (LastMessageTime.contains("<a href=")) {
      		int SubStart = LastMessageTime.indexOf("\">", 0) + 2;
          	LastMessageTime = HappyUtils.replaceHTMLChars(LastMessageTime.substring(SubStart, LastMessageTime.length()));
      	} 

		Start = Str.indexOf("\">", Start) + 2;
      	End = Str.indexOf("</td>", Start);  
      	String LastMessageFrom = HappyUtils.replaceHTMLChars(Str.substring(Start, End).trim());
      	
      	return new Thread(ThreadId, Title, NumberOfMessages, StartedBy, LastMessageTime, LastMessageFrom, MessageText, NumberOfPages, NewMsg, 1);	
	}
	
	@Override
	protected Boolean doInBackground(Object... param) {	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences((Context)param[1]);
//        DefaultHttpClient httpclient = new DefaultHttpClient();
            	       
    	try {	
//    		CookieStore cookieStore = httpclient.getCookieStore();
//    		BasicClientCookie cookie = new BasicClientCookie(preferences.getString(ForumListFragment.COOKIE_NAME, ""),
//					preferences.getString(ForumListFragment.COOKIE_VALUE, ""));
//    		cookie.setPath("/");
//    		cookie.setDomain("happyride.se");
//    		cookieStore.addCookie(cookie);
//
//    		String urlStr = "https://happyride.se/forum/list.php/1/page=" + Integer.toString((Integer)param[0]);
//
//            HttpGet httpget = new HttpGet(urlStr);
//
//            HttpResponse response = httpclient.execute(httpget);
//            HttpEntity entity = response.getEntity();
//
//			InputStreamReader inputStream = new InputStreamReader(entity.getContent());
//			LineNumberReader lineNumberReader = new LineNumberReader(inputStream);
			LineNumberReader lineNumberReader = null;

	    	StringBuilder ksStringBuilder = new StringBuilder();
	    	Thread item;
			boolean read = false;
			String lineString = "";
			
			while((lineString = lineNumberReader.readLine()) != null) {
  		
    			if (lineString.contains("<tr>")) {
    				read = true;   
    				ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
    			} else if (lineString.contains("Aktuell sida: </span>")) {    	    
    				mNumberOfThreadPages = Integer.parseInt(lineString.substring(lineString.indexOf("av ", 0) + 3, lineString.length()));
    			} else if (read) {
    				ksStringBuilder.append(lineString);
    				if (lineString.contains("PhorumTableHeader")) {
    					read = false;
    				} else if (lineString.contains("</tr>")) {
    					item = ExtractThreadRow(ksStringBuilder.toString());
    	    			if (item != null) {
    	    				mThreads.add(item);
    	    			}
    					read = false;
    				}
    			}    			    			
    		}
			
			for(int i = 0; i < mThreads.size(); i++) {
				mThreads.get(i).setNumberOfThreadPages(mNumberOfThreadPages);
			}
       	} catch (Exception e) {
    		//Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
//    		httpclient.getConnectionManager().shutdown();
        }

		return mThreads.size() != 0;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (ThreadListListener l : mThreadListListenerList) {
			if (result) {
				l.success(mThreads);
			} else {
				l.fail();
			}					
		}
    }  	
}
