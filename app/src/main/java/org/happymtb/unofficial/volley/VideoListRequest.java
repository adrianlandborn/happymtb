package org.happymtb.unofficial.volley;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.VideoItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class VideoListRequest extends Request<List<VideoItem>> implements Response.Listener<List<VideoItem>> {
    private Response.Listener<List<VideoItem>> mListener;
    private int mNumberOfVideoPages = 1;
    private String mSelectedCategory = "Alla";

    public VideoListRequest(String url, Response.Listener<List<VideoItem>> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, errorListener);

        mListener = listener;
    }



    @Override
    protected Response<List<VideoItem>> parseNetworkResponse(NetworkResponse response) {

        List<VideoItem> videoItems = new ArrayList<>();

        try {
            String htmlString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            String[] lines = htmlString.split("\\n");

            StringBuilder ksStringBuilder = new StringBuilder();
            VideoItem item;
            boolean startRead = false;
            boolean startReadCategory = false;
            boolean startReadPages = false;

            for (String lineString : lines) {
                if (lineString.contains("<div class=\"videobox") && !lineString.contains("<div class=\"videobox\"></div>")) {
                    startRead = true;
                    ksStringBuilder = new StringBuilder();
//					ksStringBuilder.append(lineString);
                }
                if (lineString.contains("<div id=\"searchvideo\">")) {
                    startReadCategory = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                }
                if (lineString.contains("<ul class=\"pagination\">")) {
                    startReadPages = true;
                    ksStringBuilder = new StringBuilder();
                    ksStringBuilder.append(lineString);
                } else if (startRead) {
                    ksStringBuilder.append(lineString);
                    if (lineString.equalsIgnoreCase("\t</div>")) {
                        startRead = false;
                        String s = ksStringBuilder.toString();
                        item = ExtractVideoRow(s);
                        if (item != null) {
                            videoItems.add(item);
                        }
                    }
                } else if (startReadPages) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</div>")) {
                        startReadPages = false;

                        String str = ksStringBuilder.toString();

                        int start = 0;
                        int end = 0;
                        start = str.indexOf("<ul>") + 4;    // Start
                        str = str.substring(start);
                        str = str.replaceAll(" class=\"active\"", "");
                        while (str.contains("\">")) {
                            start = str.indexOf("\">") + 2;    // Start
                            end = str.indexOf("</a>", start);        // End

                            if (HappyUtils.isInteger(str.substring(start, end))) {
                                if (Integer.parseInt(str.substring(start, end)) > mNumberOfVideoPages) {
                                    mNumberOfVideoPages = Integer.parseInt(str.substring(start, end));
                                }
                            }
                            str = str.substring(end);
                        }
                    }
                } else if (startReadCategory) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</form>")) {
                        startReadCategory = false;

                        String str = ksStringBuilder.toString();
                        if (str.contains("selected")) {
                            int start = str.indexOf("selected>") + 9;    // Start
                            int end = str.indexOf("</option>", start);        // End
                            mSelectedCategory = str.substring(start, end);
                        }
                    }
                }
            }

            for (int i = 0; i < videoItems.size(); i++) {
                videoItems.get(i).setNumberOfVideoPages(mNumberOfVideoPages);
            }

            return Response.success(videoItems, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new VolleyError(e));
        }
    }

    public VideoItem ExtractVideoRow(String VideoStr) {
        int start = VideoStr.indexOf("<a href=\"") + 9;
        int end = VideoStr.indexOf("\">", start);
        String link = "https://happyride.se" + VideoStr.substring(start, end);
        start = end;

        start = VideoStr.indexOf("<img src=\"", start) + 10;
        end = VideoStr.indexOf("\" border", start);
        String imgLink = VideoStr.substring(start, end);
        start = end;

        start = VideoStr.indexOf("duration\">", start) + 10;
        end = VideoStr.indexOf("</div>", start);
        String length = VideoStr.substring(start, end);
        start = end;

        start = VideoStr.indexOf("class=\"title\">", start) + 14;
        end = VideoStr.indexOf("</a><br />", start);
        String Title = HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
        start = end;

//		start = VideoStr.indexOf(" - ", start) + 3;
//		end = VideoStr.indexOf("\" width", start);
//		String date = VideoStr.substring(start, end);
//		start = end;

        start = VideoStr.indexOf("Uppladdad av ", start) + 13;
        end = VideoStr.indexOf("\">", start) + 2;
        start = end;

        end = VideoStr.indexOf("</a><br />", start);
        String uploader = "Uppladdad av " + HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
        start = end;

        String category = "Ingen kategori";
        if (VideoStr.contains("Kategori")) {
            start = VideoStr.indexOf("\">", start) + 2;
            end = VideoStr.indexOf("</a><br />", start);
            category = /*"Kategori: " +*/ HappyUtils.replaceHTMLChars(VideoStr.substring(start, end));
        }

        return new VideoItem(Title, uploader, category, length, "" /*date*/, link, imgLink, 1, mSelectedCategory);
    }


    @Override
    protected void deliverResponse(List<VideoItem> items) {
        if (items != null || !items.isEmpty()) {
            mListener.onResponse(items);
        } else {
            getErrorListener().onErrorResponse(new NetworkError());
        }
    }

    @Override
    public void onResponse(List<VideoItem> response) {
        // Subclasses should implement this
    }

    public void removeListener() {
        mListener = null;
    }
}
