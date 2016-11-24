package org.happymtb.unofficial.task;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.happymtb.unofficial.helpers.HappyUtils;
import org.happymtb.unofficial.item.KoSObjectItem;
import org.happymtb.unofficial.item.Person;
import org.happymtb.unofficial.listener.KoSObjectListener;

public class KoSObjectTask extends AsyncTask<Object, Void, Boolean> {
    public static final String BASE_URL = "https://happyride.se";
    public static final String ITEM_REMOVED = "Hittade ingen annons";
    private ArrayList<KoSObjectListener> mKoSObjectListenerList;
	private KoSObjectItem mKoSObjectItem;
	private static final String TAG = "happyride";

	public KoSObjectTask() {
		mKoSObjectListenerList = new ArrayList<KoSObjectListener>();
	}

	public void addKoSObjectListener(KoSObjectListener l) {
		mKoSObjectListenerList.add(l);
	}

	public void removeKoSObjectListener(KoSObjectListener l) {
		mKoSObjectListenerList.remove(l);
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

	public KoSObjectItem extractKoSObject(String str) {
		List<String> imgLinkList = new ArrayList<String>();

        int start = getStart(str, "<h1>", 0);
        int end = getEnd(str, "</h1>", start);
        String titleTemp = HappyUtils.replaceHTMLChars(str.substring(start, end));

		if (titleTemp.equals(ITEM_REMOVED)) {
			return new KoSObjectItem(titleTemp);
		}

		String[] titleSplit = titleTemp.split(": ", 2);
        String type = "Säljes";
        if (titleSplit[0].equals("Köpes")) {
            type = "Köpes";
        }
        String title = titleSplit[1];
        start = end;

		if (str.contains("<div class=\"carousel-inner\"")) {
			start = getStart(str, "<div class=\"item active\">", start);
			start = getStart(str, "<a href=\"/img/admarket/large/", start);
			end = getEnd(str, "\" rel=\"lightbox[gallery1]\">", start);
			imgLinkList.add(BASE_URL + "/img/admarket/normal/" + str.substring(start, end));
			start = end;

            while (str.substring(start).contains("<div class=\"item\">")) {
                start = getStart(str, "<a href=\"/img/admarket/large/", start);
                end = getEnd(str, "\" rel=\"lightbox[gallery1]\">", start);
                imgLinkList.add(BASE_URL + "/img/admarket/normal/" + str.substring(start, end));
                start = end;
            }
        }
        end = getEnd(str, "<div class=\"well\"", start);
        String description = str.substring(start, end);
        while (description.contains("</div>")) {
            description = description.substring(description.indexOf("</div>") + 6);
        }

		int linkStart = 0;
		String linkTemp;
		while (description.substring(linkStart).contains(">länk<") && description.toLowerCase().substring(linkStart).contains("<a href=\"http")) {
			linkStart = description.toLowerCase().indexOf("<a href=\"http", linkStart) + 9;
			linkTemp = description.substring(linkStart);
			int linkEnd = linkTemp.indexOf("\" target=\"_blank\">");
			linkTemp = linkTemp.substring(0,linkEnd);
			if (linkTemp.endsWith("/")) {
				linkTemp = linkTemp.substring(0,linkTemp.length() - 1);
			}
			description = description.replaceFirst(">länk<", ">" + linkTemp + "<");
			linkStart = description.indexOf("</a>", linkStart) + 4;
		}

		description = HappyUtils.replaceHTMLChars(description);

        // Fakta
        start = getStart(str, "<strong>Pris:</strong>", end);
        end = getEnd(str, "<br />", start);
        String price = HappyUtils.replaceHTMLChars(str.substring(start, end));

        start = getStart(str, "<strong>Årsmodell:</strong>", end);
        end = getEnd(str, "<br />", start);

        int yearModel = -1;
        try {
            yearModel = Integer.parseInt(HappyUtils.replaceHTMLChars(str.substring(start, end)));
        } catch (NumberFormatException e) {
            //
        }

        start = getStart(str, "Län:</strong>", end);
        end = getEnd(str, "<br />", start);
        String area = HappyUtils.replaceHTMLChars(str.substring(start, end));

        start = getStart(str, "<strong>Plats:</strong>", end);
        end = getEnd(str, "<br />", start);
        String town = HappyUtils.replaceHTMLChars(str.substring(start, end));

        start = getStart(str, "<strong>Publicerad:</strong>", end);
        end = getEnd(str, "<br />", start);
        String publishDate = HappyUtils.replaceHTMLChars(str.substring(start, end));

        //Annonsör
        Person person;
        start = getStart(str, "<a href=\"", end);
        end = getEnd(str, "\">", start);
        String personIdLink = str.substring(start, end);
        start = start + personIdLink.length() + 2;
        personIdLink = BASE_URL + personIdLink;

        end = getEnd(str, "</a>)<br />", end + 2);
        String personName = HappyUtils.replaceHTMLChars(str.substring(start, end));

        start = getStart(str, "<strong>Medlem sedan:</strong>", end);
        end = getEnd(str, "<br />", start);
        String personMemberSince = HappyUtils.replaceHTMLChars(str.substring(start, end));

		String personPhone = "";
		if (str.indexOf("<strong>Telefon/Mobilnummer:</strong>", end) > -1) {
			start = getStart(str, "<strong>Telefon/Mobilnummer:</strong>", end);
			end = getEnd(str, "<br />", start);
			personPhone = str.substring(start, end).trim();
		}


        //TODO Contact:  PM, Mail, Phone
        String personPM = "";
        String personEmail = "";

        start = getStart(str, "<div class=\"ad-contact-buttons\">", end);
        end = getEnd(str, "</div>", start);

		String contact = str.substring(start);
		start = 0;
		if (contact.indexOf("Skicka PM") > -1) {
			// Skicka PM
			start = getStart(contact, "<a href=\"", start);
			end = getEnd(contact, "\" class=\"btn btn-primary\"", start);
			personPM = BASE_URL + HappyUtils.replaceHTMLChars(contact.substring(start, end));
		}

		if (contact.indexOf("Skicka E-post", end) > -1) {
			// Skicka Epost
			start = getStart(contact, "<a href=\"", end);
			end = getEnd(contact, "\" class=\"btn btn-primary\"", start);
			personEmail = BASE_URL + "/annonser/" + HappyUtils.replaceHTMLChars(contact.substring(start, end));
		}

        person = new Person(personName, personPhone, personMemberSince, personIdLink, personPM, personEmail);

		return new KoSObjectItem(area, town, type, title, person, publishDate, imgLinkList, description, price, yearModel);
	}

	@Override
	protected Boolean doInBackground(Object... params) {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		try {
			String urlStr = (String) params[0];
			if (TextUtils.isEmpty(urlStr)) {
				return false;
			}
			HttpGet httpget = new HttpGet(urlStr);
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			InputStreamReader is = new InputStreamReader(entity.getContent()); 				
			LineNumberReader lineNumberReader = new LineNumberReader(is);
			
			StringBuilder ksStringBuilder = new StringBuilder();
			boolean startRead = false;
			String lineString = "";
				
			while((lineString = lineNumberReader.readLine()) != null) {
				if (lineString.contains("<div id=\"content\">")) {
					startRead = true;
					ksStringBuilder = new StringBuilder();
					ksStringBuilder.append(lineString);
				} else if (startRead) {
					ksStringBuilder.append(lineString);
					if (lineString.contains("<a href=\"report.php") || lineString.contains("<h1>Hittade ingen annons</h1>")) {
						startRead = false;
						mKoSObjectItem = extractKoSObject(ksStringBuilder.toString());
					}
				}								
			}						
		} catch (Exception e) {
			 Log.d("doInBackground", "Error: " + e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return true;	
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		for (KoSObjectListener l : mKoSObjectListenerList) {
			if (result) {
				l.success(mKoSObjectItem);
			} else {
				l.fail();
			}
		}
	}
}
