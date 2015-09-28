package org.happymtb.unofficial.item;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class KoSObjectItem implements Serializable {
	private static final long serialVersionUID = 201112070001L; 
	
	private String mArea;
	private String mType;
	private String mTitle;
	private String mPerson;
	private String mPhone;	
	private String mDate;		
	private String mImgLink;
	private Drawable mObjectImage;	
	private String mText;
	private String mPrice;	
	
	public KoSObjectItem(String Area, String Type, String Title, String Person,
			String Phone, String Date, String ImgLink, String Text, String Price) {
		setArea(Area);
		setType(Type);
		setTitle(Title);
		setPerson(Person);
		setPhone(Phone);	
		setDate(Date);		
		setImgLink(ImgLink);
		setText(Text);
		setPrice(Price);	
	}

	public String getArea() {
		return mArea;
	}

	public void setArea(String mArea) {
		this.mArea = mArea;
	}

	public String getType() {
		return mType;
	}

	public void setType(String mType) {
		this.mType = mType;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getPerson() {
		return mPerson;
	}

	public void setPerson(String mPerson) {
		this.mPerson = mPerson;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String mPhone) {
		this.mPhone = mPhone;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String mDate) {
		this.mDate = mDate;
	}

	public String getImgLink() {
		return mImgLink;
	}

	public void setImgLink(String mImgLink) {
		this.mImgLink = mImgLink;
	}

	public Drawable getObjectImage() {
		return mObjectImage;
	}

	public void setObjectImage(Drawable objectImage) {
		mObjectImage = objectImage;
	}

	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	public String getPrice() {
		return mPrice;
	}

	public void setPrice(String mPrice) {
		this.mPrice = mPrice;
	}
}
