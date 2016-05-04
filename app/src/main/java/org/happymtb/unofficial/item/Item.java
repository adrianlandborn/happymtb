package org.happymtb.unofficial.item;

import java.io.Serializable;

public class Item implements Serializable {
	private static final long serialVersionUID = 201111020001L; 
	
	private String mTitle;
	private String mLink;
	private String mDescription;
	private String mGroup;
	private Boolean mVisible;
	
	public Item(String title, String link, String description, String group, Boolean visible) {
		mTitle = title;
		mLink = link;
		mDescription = description;
		mGroup = group;
		mVisible = visible;
	}
	
	public Item(String group, Boolean visible) {
		mTitle = "";
		mGroup = group;
		mVisible = visible;
	}	
	
	public String getGroup() {
		return mGroup;
	}		
	
	public String getTitle() {
		return mTitle;
	}

	public Boolean getVisible() {
		return mVisible;
	}	
	
	public void setTitle(String title) {
		mTitle = title;
	}	

	public void setVisible(Boolean visible) {
		mVisible = visible;
	}		
	
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}
	
	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

}
