package org.happymtb.unofficial.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.happymtb.unofficial.R;

public class HappyUtils {

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':') {
				return false;
			}
		}
		return true;
	}	
	
	public static String getFilename(String url) {
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));		
		fileNameWithoutExtn = fileNameWithoutExtn.replace('.', '_');		
		return fileNameWithoutExtn;
	}

    public static int getTextSize(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String TextSizeArray [] =  context.getResources().getStringArray(R.array.settings_textsize);
        return Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 2)]);
    }
	public static String replaceHTMLChars(String htmlCode) {
		htmlCode = htmlCode.replaceAll("&aring;", "å").
		replaceAll("&auml;", "ä").
		replaceAll("&ouml;", "ö").
		replaceAll("&Aring;", "Å").
		replaceAll("&Auml;", "Ä").
		replaceAll("&Ouml;", "Ö").
		replaceAll("&#228;", "ä").
		replaceAll("&#229;", "⊛").
		replaceAll("&#246;", "ö").
		replaceAll("&#196;", "Ä").
		replaceAll("&#197;", "�").
		replaceAll("&#214;", "�").
		replaceAll("'", "&#39;").
		replaceAll("&quot;", "\"").
		replaceAll("&gt;", ">").
		replaceAll("&lt;","<").
		replaceAll("&#39;","'").
		replaceAll("&#33;", "!").
		replaceAll("&#8217;", "�").
		replaceAll("&#8211;", "-").
		replaceAll("&#160;", " ").
		replaceAll("&#8221;", "�").
		replaceAll("&#038;", "&").
		replaceAll("&amp;", "&").
		replaceAll("&#8230;", "...");

		return htmlCode;
	}  	
}
