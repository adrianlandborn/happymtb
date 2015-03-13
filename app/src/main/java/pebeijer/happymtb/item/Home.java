package pebeijer.happymtb.item;

import java.io.Serializable;

public class Home implements Serializable {
	private static final long serialVersionUID = 201111020001L; 
	
	private String mTitle;
	private String mLink;
	private String mText;
	private String mDate;
	
	public Home(String Title, String Link, String Text, String Date) {
		mTitle = Title;
		mLink = Link;
		mText = Text;
		mDate = Date;	
	}
	
	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String Title) {
		mTitle = Title;
	}		
	
	public String getText() {
		return mText;
	}	
	
	public void setText(String Text) {
		mText = Text;
	}		
	
	public String getDate() {
		return mDate;
	}	
	
	public void setDate(String Date) {
		mDate = Date;
	}			
	
	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}
}
