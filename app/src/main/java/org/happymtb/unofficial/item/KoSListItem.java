package org.happymtb.unofficial.item;

import java.io.Serializable;

public class KoSListItem implements Serializable {
	private static final long serialVersionUID = 201110070001L;

	public static String TYPE_SALJES =  "Säljes";
	public static String TYPE_KOPES =  "Köpes";

	private long mId;
	private String mTime;
	private String mType;
	private String mTitle;
	private String mArea;
	private String mLink;
	private String mImgLink;
	private String mCategory;
	private String mSelectedCategory;
	private String mSelectedRegion;
	private String mPrice;
	private boolean mSold;
	private int mNumberOfKoSPages;

    public KoSListItem() {

    }
	public KoSListItem(long id, String Time, String type, String Title, String Area, String Link,
                       String ImgLink, String Category, String Price, int NumberOfKoSPages,
                       String SelectedCategory, String SelectedRegion) {
		mId = id;
		mTime = Time;
		mType = type;
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

	public void setId(long id) {
		mId = id;
	}

	public long getId() {
		return mId;
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

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		mType = type;
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

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		mTime = time;
	}

	public boolean isSold() {
		return mSold;
	}

	public void setSold(boolean sold) {
		mSold = sold;
	}

}
