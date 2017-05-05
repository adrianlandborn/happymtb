package org.happymtb.unofficial.volley;

import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.Item;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShopsListRequest extends Request<List<Item>> implements Response.Listener<List<Item>> {
    private static final String BASE_URL = "https://happyride.se/forum/butiker/";
    private Response.Listener<List<Item>> mListener;
    private String mGroup;

    public
    ShopsListRequest(Response.Listener<List<Item>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, BASE_URL, errorListener);

        mListener = listener;
    }

    public void removeListener() {
        mListener = null;
    }

    @Override
    protected Response<List<Item>> parseNetworkResponse(NetworkResponse response) {
        List<Item> shopItems = new ArrayList<>();

        try {
            String htmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            String[] lines = htmlString.split("\\n");

            StringBuilder ksStringBuilder = new StringBuilder();
            Item shopItem;
            boolean startRead = false;

            // TODO Use StringTokenizer?
            for (String lineString : lines) {
                Log.d(ShopsListRequest.class.getSimpleName(), lineString);

                if (lineString.contains("RowAlt' ")) {
                    startRead = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                } else if (lineString.contains("Header' nowrap='nowrap' w")) {
                    mGroup = HappyUtils.replaceHTMLChars(lineString.substring(
                            lineString.indexOf("=0'>", 0) + 4,
                            lineString.indexOf("</", 0)));

                    shopItem = new Item(mGroup, false);
                    shopItems.add(shopItem);
                } else if (startRead) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</tr>")) {
                        startRead = false;
                        shopItem = ExtractItemRow(ksStringBuilder.toString());
                        if (shopItem != null) {
                            shopItems.add(shopItem);
                        }
                    }
                }
            }
            return Response.success(shopItems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
    }

    public Item ExtractItemRow(String ShopStr) {
        int Start = ShopStr.indexOf("<b>", 0) + 3;
        int End = ShopStr.indexOf("</b>", Start);
        String Title = HappyUtils.replaceHTMLChars(ShopStr.substring(Start, End));
        Start = End;

        Start = ShopStr.indexOf("<a href='", Start) + 9;
        End = ShopStr.indexOf("'", Start);
        String Link = BASE_URL + ShopStr.substring(Start, End);
        Start = End;

        Start = ShopStr.indexOf("PhorumSmallFont'>", Start) + 17;
        End = ShopStr.indexOf("</span>", Start);
        String Description = HappyUtils.replaceHTMLChars(ShopStr.substring(Start, End));

        return new Item(Title, Link, Description, mGroup, false);
    }

    @Override
    protected void deliverResponse(List<Item> items) {
        if (items != null || !items.isEmpty()) {
            mListener.onResponse(items);
        } else {
            getErrorListener().onErrorResponse(new NetworkError());
        }
    }

    @Override
    public void onResponse(List<Item> response) {
        // Subclasses should implement this
    }

}
