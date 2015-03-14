package org.pebeijer.happymtb.item;

import java.io.Serializable;

public class CalendarItem implements Serializable {
	private static final long serialVersionUID = 201110070201L; 
	
	private String mTitle;
	private String mDescription;
	private String mCategory;
	private String mSelectedRegion;
	private String mTime;
		private String mId;
	
	public CalendarItem(String Title, String Description, String Category, String SelectedRegion, String Time, String Id) {
		setTitle(Title);
		setDescription(Description);
		setCategory(Category);
		setSelectedRegion(SelectedRegion);
		setTime(Time);
		setId(Id);		
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String mCategory) {
		this.mCategory = mCategory;
	}

	public String getSelectedRegion() {
		return mSelectedRegion;
	}

	public void setSelectedRegion(String mSelectedRegion) {
		this.mSelectedRegion = mSelectedRegion;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String mTime) {
		this.mTime = mTime;
	}

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
}