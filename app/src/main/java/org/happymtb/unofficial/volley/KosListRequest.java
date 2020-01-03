package org.happymtb.unofficial.volley;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSReturnData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class KosListRequest extends Request<KoSReturnData> implements Response.Listener<KoSReturnData>{
	private static final String TAG = "KosListRequest";
    private Response.Listener<KoSReturnData> mListener;
    private int mCurrentPage;
    private String mSelectedCategory;
    private String mSelectedRegion;

	public KosListRequest(int currentPage, String url, Response.Listener<KoSReturnData> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);

        mCurrentPage = currentPage;
        mListener = listener;
	}

	public void removeListener() {
		mListener = null;
	}

	public KoSListItem extractKoSRow(String KoSStr) {
		String type;
		String time ;
		String title;
		String area;
		String link;
		String imgLink;
		String category;
		String price;

		int start = 0;
		int end;

		//ImageLink
		start = getStart(KoSStr, "src=\"", start);
		end = KoSStr.indexOf("\" border=\"0\" ", start);
		imgLink = ("https://happyride.se" + KoSStr.substring(start, end)).replace("small", "normal");
		start = end;

		//Link
		start = KoSStr.indexOf("<a href=\"", start) + 9;
		end = KoSStr.indexOf("\"", start);
		link = "https://happyride.se/annonser/" + KoSStr.substring(start, end);
		start = end;

		//Title
		start = start + 2;
		end = KoSStr.indexOf("</a></h4>", start);
		title = HappyUtils.replaceHTMLChars(KoSStr.substring(start, end));
		start = end;

		//Type
		start = start + 1;
		end = KoSStr.indexOf("<span class=\"visible-xs-inline\">", start);
		String typeWithRegion = HappyUtils.replaceHTMLChars(KoSStr.substring(start, end));
		type = typeWithRegion.split(" i ")[0].contains("Säljes") ? KoSListItem.TYPE_SALJES : KoSListItem.TYPE_KOPES;

		//Area
		area = typeWithRegion.split(" i ")[1].trim();
		start = end;

		//Price
		start = KoSStr.indexOf("<span class=\"visible-xs\">", start) + 25;
		end = KoSStr.indexOf("</span></td>", start);
		price = HappyUtils.replaceHTMLChars(KoSStr.substring(start, end));
		price = price.replace(":-", " kr");
		start = end;

		//Category
		start = KoSStr.indexOf("<td class=\"col-3 hidden-xs\">", start) + 28;
		end = KoSStr.indexOf("</td>", start);
		category = HappyUtils.replaceHTMLChars(KoSStr.substring(start, end));
		start = end;

		//Time
		start = KoSStr.indexOf("<td class=\"hidden-xs\"><nobr>", start) + 28;
		end = KoSStr.indexOf("</nobr></td>", start);
		time = HappyUtils.replaceHTMLChars(KoSStr.substring(start, end));

		return new KoSListItem(time, type, title, area, link, imgLink, category, price);
	}

    @Override
    protected Response<KoSReturnData> parseNetworkResponse(NetworkResponse response) {
        List<KoSListItem> kosListItems = new ArrayList();
        int numberOfKoSPages = 1;
        try {
            String htmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            String[] lines = htmlString.split("\\n");

            StringBuilder ksStringBuilder = new StringBuilder();

            KoSListItem item;
            boolean startReadMessage = false;
            boolean startReadCategory = false;
            boolean startReadRegion = false;
            boolean startReadPagination = false;

            // TODO Use StringTokenizer?
            for (String lineString : lines) {
                if (lineString.contains("<td class=\"col-1\">")) {
                    startReadMessage = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                } else if (lineString.contains("<select id=\"category\" class=\"scat\">")) {
                    startReadCategory = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                } else if (lineString.contains("<select id=\"region\" class=\"sreg\">")) {
                    startReadRegion = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                    //TODO Pagination
                } else if (lineString.contains("<ul class=\"pagination\">")) {
                    startReadPagination = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
//                    mNumberOfKoSPages = Integer.parseInt(lineString.substring(
//                            lineString.indexOf("<ul class=\"pagination\">", 0) + 19,   // Start
//                            lineString.indexOf("</span>", 0)));                       // End

                    if (mCurrentPage == numberOfKoSPages) {
                        // If on last page the last item is nog <span class="bold"> but <span class="bold_lightgray">
                        // Ugly workaround or this bug... :(
                        // +1 is due to offset
                        numberOfKoSPages = mCurrentPage + 1;

                    }
                } else if (startReadMessage) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</tr>")) {
                        startReadMessage = false;
                        String row = ksStringBuilder.toString();
                        item = extractKoSRow(row);
                        if (item != null) {
                            kosListItems.add(item);
                        }
                    }
                } else if (startReadCategory) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</select>")) {
                        startReadCategory = false;

                        String str = ksStringBuilder.toString();
                        if (str.contains("selected")) {
                            int start = str.indexOf("selected >") + 10;    // Start
                            int end = str.indexOf("</option>", start);        // End
                            mSelectedCategory = str.substring(start, end);
                        }
                    }
                } else if (startReadRegion) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</select>")) {
                        startReadRegion = false;

                        String str = ksStringBuilder.toString();
                        if (str.contains("selected")) {
                            int start = str.indexOf("selected >") + 10;    // Start
                            int end = str.indexOf("</option>", start);        // End
                            mSelectedRegion = str.substring(start, end);
                        }
                    }
                } else if (startReadPagination) {
                    ksStringBuilder.append(lineString);
                    String str = ksStringBuilder.toString();
                    if (lineString.contains("</ul></nav>") || str.contains("</ul></nav>")) {
                        startReadPagination = false;
                        boolean isLastPage = true;

                        if(str.contains("aria-label=\"Next\"")) {
                            str = str.substring(0, str.lastIndexOf("<a href=\"/annonser/"));
                            isLastPage = false;
                        }

                        str = str.substring(str.lastIndexOf("p=") + 2);
                        str = str.substring(0, str.indexOf("\">"));

                        numberOfKoSPages = Integer.parseInt(str);
                        if (isLastPage) {
                            numberOfKoSPages++;
                        }
                    }
                }
            }

            KoSReturnData returnData = new KoSReturnData(kosListItems, numberOfKoSPages);
            return Response.success(returnData, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
	}

	@Override
	protected void deliverResponse(KoSReturnData data) {
        List items = data.getItems();
        if (items != null || !items.isEmpty()) {
            mListener.onResponse(data);
        } else {
            getErrorListener().onErrorResponse(new NetworkError());
        }
	}

	@Override
	public void onResponse(KoSReturnData response) {
        // Subclasses should implement this
	}

	private int getStart(String searchString, String startString, int from) {
		try {
			return searchString.indexOf(startString, from) + startString.length();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Cannot find START: " + startString);
		}
		return from;
	}

	private int getEnd(String searchString, String endString, int from) {
		try {
			return searchString.indexOf(endString, from);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Cannot find END: " + endString);
		}
		return from;
	}
}
