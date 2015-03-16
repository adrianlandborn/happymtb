package org.pebeijer.happymtb.item;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class KoSItem implements Serializable {
	private static final long serialVersionUID = 201110070001L; 
	
	private String mTime;
	private String mTitle;
	private String mArea;
	private String mLink;
	private String mImgLink;
	private String mCategory;
	private String mSelectedCategory;
	private String mSelectedRegion;
	private String mPrice;
	private Drawable mObjectImage;
	private int mNumberOfKoSPages;
	
	public KoSItem(String Time, String Title, String Area, String Link,
			String ImgLink, String Category, String Price, int NumberOfKoSPages, String SelectedCategory, String SelectedRegion) {
		mTime = Time;
		mTitle = Title;
		mArea = Area;
		mLink = Link;
		mImgLink = ImgLink;
		mCategory = Category;
		mSelectedCategory = SelectedCategory;
		mSelectedRegion = SelectedRegion;
		mPrice = Price;
		mNumberOfKoSPages = NumberOfKoSPages;
	}

	public int getNumberOfKoSPages() 
	{
		return mNumberOfKoSPages;
	}	
	
	public void setNumberOfKoSPages(int NumberOfKoSPages) 
	{
		mNumberOfKoSPages = NumberOfKoSPages;
	}	
	
	public String getImgLink() {
		return mImgLink;
	}

	public void setImgLink(String imgLink) {
		mImgLink = imgLink;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getArea() {
		return mArea;
	}

	public void setArea(String area) {
		mArea = area;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

	public String getSelectedRegion() {
		return mSelectedRegion;
	}

	public void setSelectedRegion(String selectedRegion) {
		mSelectedRegion = selectedRegion;
	}	
	
	public String getSelectedCategory() {
		return mSelectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		mSelectedCategory = selectedCategory;
	}
	
	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		mPrice = price;
	}

	public Drawable getObjectImage() {
		return mObjectImage;
	}

	public void setObjectImage(Drawable objectImage) {
		mObjectImage = objectImage;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		mTime = time;
	}

}
