package org.pebeijer.happymtb.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import org.pebeijer.happymtb.listener.LoginListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class LoginTask extends AsyncTask<Object, Void, Boolean> {
	private ArrayList<LoginListener> LoginListenerList;

	public LoginTask() {
		LoginListenerList = new ArrayList<LoginListener>();
	}

	public void addLoginListener(LoginListener l) {
		LoginListenerList.add(l);
	}

	public void removeLoginListener(LoginListener l) {
		LoginListenerList.remove(l);
	}

	@Override
	protected Boolean doInBackground(Object... param) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences((Context) param[0]);
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			HttpGet httpget = new HttpGet("http://happymtb.org/forum/login.php");

			httpclient.execute(httpget);

			HttpPost httpost = new HttpPost(
					"http://happymtb.org/forum/login.php");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("forum_id", "1"));
			nvps.add(new BasicNameValuePair("username", preferences.getString("username", "")));
			nvps.add(new BasicNameValuePair("password", preferences.getString("password", "")));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpclient.execute(httpost);

			List<Cookie> cookies = httpclient.getCookieStore().getCookies();
			if (!cookies.isEmpty()) {
				for (Cookie cookie : cookies) {
					if (cookie.toString().contains("phorum_tmp_cookie")) {						
						return false;
					} else if (cookie.toString().contains("phorum_session_v5")) { 
						SharedPreferences settings;
						settings = PreferenceManager
								.getDefaultSharedPreferences((Context) param[0]);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString("cookiename", cookie.getName());
						editor.putString("cookievalue", cookie.getValue());
						editor.commit();
						return true;
					}
				}
			}
		} catch (Exception e) {
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		for (LoginListener l : LoginListenerList) {
			if (result) {
				l.Success();
			} else {
				l.Fail();
			}
		}
	}
}
