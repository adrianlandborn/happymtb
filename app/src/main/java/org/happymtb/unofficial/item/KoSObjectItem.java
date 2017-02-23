package org.happymtb.unofficial.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KoSObjectItem implements Serializable {
	private static final long serialVersionUID = 201112070001L; 
	
	private String mArea;
	private String mTown;
	private String mType;
	private String mTitle;
	private Person mPerson;
	private String mDate;
	private ArrayList<String> mImgLinkList;
	private String mText;
	private String mPrice;	
	private int mYearModel;
	private String mUrl;
	private String mCategory;

	public KoSObjectItem(String title) {
		setTitle(title);
	}

	public KoSObjectItem(String area, String town, String type, String title, Person person,
							  String publishDate, List<String> imgLinkList, String description, String price, int yearModel, String url, String category) {
		setArea(area);
		setTown(town);
		setType(type);
		setTitle(title);
		setPerson(person);
		setDate(publishDate);
		setImgLinkList(imgLinkList);
		setText(description);
		setPrice(price);
		setYearmodel(yearModel);
		setUrl(url);
		setCategory(category);
	}

	public String getArea() {
		return mArea;
	}

	public void setArea(String area) {
		this.mArea = area;
	}

	public String getTown() {
		return mTown;
	}

	public void setTown(String town) {
		this.mTown = town;
	}

	public String getType() {
		return mType;
	}

	public void setType(String type) {
		this.mType = type;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public Person getPerson() {
		return mPerson;
	}

	public void setPerson(Person person) {
		this.mPerson = person;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
	}

	public String getImgLink() {
		if (mImgLinkList != null && mImgLinkList.size() > 0) {
			return mImgLinkList.get(0);
		}
		return "";
	}

	public List<String> getImgLinkList() {
		return mImgLinkList;
	}

	public void setImgLinkList(List list) {
		this.mImgLinkList = new ArrayList<String>(list);
	}

	public String getText() {
		return mText;
	}

	public void setText(String text) {
		this.mText = text;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String price) {
		this.mPrice = price;
	}
	public int getYearModel() {
		return mYearModel;
	}

	public void setYearmodel(int yearmodel) {
		this.mYearModel = yearmodel;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}
	public String getCatgegory() {
		return mCategory;
	}

	public void setCategory(String category) {
		this.mCategory = category;
	}
}
