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
	private String mImgLink;
	private List<String> mImgLinkList;
	private String mText;
	private String mPrice;	
	private int mYearModel;

	public KoSObjectItem(String title) {
		setTitle(title);
	}

	public KoSObjectItem(String area, String town, String type, String title, Person person,
							  String publishDate, String imgLink, List<String> imgLinkList, String text, String price, int yearModel) {
		setArea(area);
		setTown(town);
		setType(type);
		setTitle(title);
		setPerson(person);
		setDate(publishDate);
		setImgLink(imgLink);
		setImgLinkList(imgLinkList);
		setText(text);
		setPrice(price);
		setYearmodel(yearModel);
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
		return mImgLink;
	}

	public void setImgLink(String imgLink) {
		this.mImgLink = imgLink;
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
}
