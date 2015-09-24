package org.happymtb.unofficial.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

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
		String fileNameWithoutExtn = "";
		String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );
		System.out.println("HappyUtils: " + fileName);
		if (!TextUtils.isEmpty(fileName) && fileName.contains(".")) {
			fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf("."));
			fileNameWithoutExtn = fileNameWithoutExtn.replace(".", "_");
		}
		return fileNameWithoutExtn;
	}

	public static String replaceHTMLChars(String htmlCode) {
		htmlCode = htmlCode.replaceAll("&aring;", "å").
		replaceAll("&auml;", "ä").
		replaceAll("&ouml;", "ö").
		replaceAll("&Aring;", "Å").
		replaceAll("&Auml;", "Ä").
		replaceAll("&Ouml;", "Ö").
		replaceAll("&eacute;", "é").
		replaceAll("&#228;", "ä").
		replaceAll("&#229;", "⊛").
		replaceAll("&#246;", "ö").
		replaceAll("&#196;", "Ä").
		replaceAll("&#197;", "┼").
		replaceAll("&#214;", "Í").
		replaceAll("'", "&#39;").
		replaceAll("&quot;", "\"").
        replaceAll("&#8243;","\"").
		replaceAll("&gt;", ">").
		replaceAll("&lt;","<").
		replaceAll("&#39;","'").
		replaceAll("&#33;", "!").
		replaceAll("&#8217;", "’").
		replaceAll("&#8211;", "-").
		replaceAll("&ndash;", "-").
		replaceAll("&#160;", " ").
		replaceAll("&#8221;", "\"").
		replaceAll("&#038;", "&").
		replaceAll("&amp;", "&").
		replaceAll("&#8230;", "...");

		return htmlCode;
	}

    public static String getSortAttrNameServer(Context ctx, int pos) {
        String AttributeArrayServer[] = ctx.getResources().getStringArray(R.array.kos_dialog_sort_attribute_position);
        return AttributeArrayServer[pos];
    }

    public static String getSortAttrNameLocal(Context ctx, int pos) {
        String AttributeArray[] = ctx.getResources().getStringArray(R.array.kos_dialog_sort_attribute);
        return AttributeArray[pos];
    }

    public static String getSortOrderNameServer(Context ctx, int pos) {
        String OrderArrayServer[] = ctx.getResources().getStringArray(R.array.kos_dialog_sort_order_position);
        return OrderArrayServer[pos];
    }

    public static String getSortOrderNameLocal(Context ctx, int pos) {
        String OrderArray[] = ctx.getResources().getStringArray(R.array.kos_dialog_sort_order);
        return OrderArray[pos];
    }
}
