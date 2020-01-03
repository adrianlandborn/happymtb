package org.happymtb.unofficial.volley;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.HomeItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HomeListRequest extends Request<List<HomeItem>> implements Response.Listener<List<HomeItem>>{

    private static final String URL = "https://happyride.se/feed";
    private Response.Listener<List<HomeItem>> mHomeListListener;

	public HomeListRequest(Response.Listener<List<HomeItem>> listener, Response.ErrorListener errorListener) {
		super(Method.GET, URL, errorListener);
        mHomeListListener = listener;
	}

	public void removeListener() {
		mHomeListListener = null;
	}

	private HomeItem extractHomeRow(String str) {
		int start = str.indexOf("<title>") + 7;
		int end = str.indexOf("</title>", start);
		String title = HappyUtils.replaceHTMLChars(str.substring(start, end));
		start = end;

		start = str.indexOf("<link>", start) + 6;
		end = str.indexOf("</link>", start);
		String link = str.substring(start, end);
		start = end;

		start = str.indexOf("creator>\n" +
				"\t\t\t\t<category><![CDATA[", start) + 21;
		end = str.indexOf("]]></category>", start);
		String category = HappyUtils.replaceHTMLChars(str.substring(start, end));

		start = str.indexOf("400px\" />", start) + 9;
		end = str.indexOf("]]></description>", start);
		String text = HappyUtils.replaceHTMLChars(str.substring(start, end));

		start = link.indexOf("se/") + 3;
		end = link.indexOf("/", start);
		String year = link.substring(start, end);
		start = end;
		
		start = link.indexOf("/", start) + 1;
		end = link.indexOf("/", start);
		String month = link.substring(start, end);
		start = end;

		start = link.indexOf("/", start) + 1;
		end = link.indexOf("/", start);
		String day = link.substring(start, end);
				
		String date = year + "-" + month + "-" + day;
		
		return new HomeItem(title, link, category, text, date);
	}

    @Override
    protected Response<List<HomeItem>> parseNetworkResponse(NetworkResponse response) {
        List<HomeItem> homeItems = new ArrayList();
        try {
            String htmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            String[] lines = htmlString.split("\\n");

            StringBuilder ksStringBuilder = new StringBuilder();
            HomeItem homeItem;
            boolean startRead = false;

            // TODO Use StringTokenizer?
            for (String lineString : lines) {
                if (lineString.contains("<item>")) {
                    startRead = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                } else if (startRead) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</item>")) {
                        startRead = false;

                        homeItem = extractHomeRow(ksStringBuilder.toString());
                        if (homeItem != null) {
                            homeItems.add(homeItem);
                        }
                    }
                }
            }

            return Response.success(homeItems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
	}

	@Override
	protected void deliverResponse(List<HomeItem> items) {
        if (items != null || !items.isEmpty()) {
            mHomeListListener.onResponse(items);
        } else {
            getErrorListener().onErrorResponse(new NetworkError());
        }
	}

	@Override
	public void onResponse(List<HomeItem> response) {
        // Subclasses should implement this
	}
}
