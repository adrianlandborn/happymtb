package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Region;
import android.os.AsyncTask;
import android.util.Log;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.listener.KoSListListener;

public class KoSListTask extends AsyncTask<Object, Void, Boolean> {
    private static final String TAG = "happyride";
    private ArrayList<KoSListListener> mKoSListListenerList;
    private List<KoSListItem> mKoSListItems = new ArrayList<KoSListItem>();
    private int mNumberOfKoSPages = 1;
    private String mSelectedCategory;
    private String mSelectedRegion;

    public KoSListTask() {
        mKoSListListenerList = new ArrayList<KoSListListener>();
    }

    public void addKoSListListener(KoSListListener l) {
        mKoSListListenerList.add(l);
    }

    public void removeKoSListListener(KoSListListener l) {
        mKoSListListenerList.remove(l);
    }

    public KoSListItem extractKoSRow(String KoSStr) {
        long id;
        String type = "";
        String time = "";
        String title;
        String area = "";
        String link;
        String imgLink;
        String category = "";
        String price = "";

        int start = 0;
        int end = 0;

        //ImageLink
        start = getStart(KoSStr, "src=\"", start);
//        Start = KoSStr.indexOf("src=\"", Start) + 5;
        end = KoSStr.indexOf("\" border=\"0\" ", start);
        imgLink = ("http://happyride.se" + KoSStr.substring(start, end)).replace("small", "normal");
        start = end;

        //Link
        start = KoSStr.indexOf("<a href=\"", start) + 9;
        end = KoSStr.indexOf("\"", start);
        link = "http://happyride.se/annonser/" + KoSStr.substring(start, end);
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
        start = end;

        id = Long.parseLong(link.split("id=")[1]);

        return new KoSListItem(id, time, type, title, area, link, imgLink, category, price, 0);
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        mKoSListItems.clear();

        DefaultHttpClient httpclient = new DefaultHttpClient();

        // //?search=&category=1&county=&type=1&category2=&county2=&type2=&price=3&year=2013&p=1&sortattribute=creationdate&sortorder=DESC
        try {
            String urlStr = "http://happyride.se/annonser/"
                    + "?search=" + params[0]
                    + "&category=" + Integer.toString((Integer) params[1])
                    + "&county=" + Integer.toString((Integer) params[2])
                    + "&type=" + Integer.toString((Integer) params[3])
                    + "&category2=" + params[4]
                    + "&county2=" + params[5]
                    + "&type2=" + params[6]
                    + "&price=" + params[7]
                    + "&year=" + params[8]
                    + "&p=" + Integer.toString((Integer) params[9])
                    + "&sortattribute=" + params[10]
                    + "&sortorder=" + params[11];

            int mCurrentPage = (Integer) params[9];
            HttpGet httpget = new HttpGet(urlStr);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            InputStreamReader is = new InputStreamReader(entity.getContent());
            LineNumberReader lineNumberReader = new LineNumberReader(is);

            StringBuilder ksStringBuilder = new StringBuilder();
            KoSListItem item;
            boolean startReadMessage = false;
            boolean startReadCategory = false;
            boolean startReadRegion = false;
            boolean startReadPagination = false;
            String lineString = "";

            while ((lineString = lineNumberReader.readLine()) != null) {
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

                    if (mCurrentPage == mNumberOfKoSPages) {
                        // If on last page the last item is nog <span class="bold"> but <span class="bold_lightgray">
                        // Ugly workaround or this bug... :(
                        // +1 is due to offset
                        mNumberOfKoSPages = mCurrentPage + 1;

                    }
                } else if (startReadMessage) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</tr>")) {
                        startReadMessage = false;
                        String row = ksStringBuilder.toString();
                        item = extractKoSRow(row);
                        if (item != null) {
                            mKoSListItems.add(item);
                        }
                    }
                } else if (startReadCategory) {
                    ksStringBuilder.append(lineString);
                    if (lineString.contains("</select>")) {
                        startReadCategory = false;

                        String str = ksStringBuilder.toString();
                        if (str.contains("selected")) {
                            int start = str.indexOf("selected >", 0) + 10;    // Start
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
                            int start = str.indexOf("selected >", 0) + 10;    // Start
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

                        str = str.substring(str.lastIndexOf("p=") + 2, str.length());
                        str = str.substring(0, str.indexOf("\">"));

                        mNumberOfKoSPages = Integer.parseInt(str);
                        if (isLastPage) {
                            mNumberOfKoSPages++;
                        }
                    }
                }
            }

            for (int i = 0; i < mKoSListItems.size(); i++) {
                mKoSListItems.get(i).setNumberOfKoSPages(mNumberOfKoSPages);
            }

        } catch (Exception e) {
            Log.d("doInBackground", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return mKoSListItems.size() > 0;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        for (KoSListListener l : mKoSListListenerList) {
            if (result) {
                l.success(mKoSListItems);
            } else {
                l.fail();
            }
        }
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
