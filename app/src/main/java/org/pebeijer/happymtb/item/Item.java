package org.pebeijer.happymtb.item;

import java.io.Serializable;

public class Item implements Serializable {
	private static final long serialVersionUID = 201111020001L; 
	
	private String mTitle;
	private String mLink;
	private String mDescription;
	private String mGroup;
	private Boolean mVisible;
	
	public Item(String Title, String Link, String Description, String Group, Boolean Visible) {
		mTitle = Title;
		mLink = Link;
		mDescription = Description;
		mGroup = Group;
		mVisible = Visible;	
	}
	
	public Item(String Group, Boolean Visible) {
		mTitle = "";
		mGroup = Group;
		mVisible = Visible;
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
	
	public void setTitle(String Title) {
		mTitle = Title;
	}	

	public void setVisible(Boolean Visible) {
		mVisible = Visible;
	}		
	
	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String Description) {
		mDescription = Description;
	}
	
	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

}
