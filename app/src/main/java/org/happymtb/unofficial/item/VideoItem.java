package org.happymtb.unofficial.item;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

public class VideoItem implements Serializable {
	private static final long serialVersionUID = 201110870001L;

	private String mTitle;
	private String mUploader;
	private String mCategory;
	private String mLength;
	private String mDate;
	private String mLink;
	private String mImgLink;
	private String mSelectedCategory;
	private int mNumberOfVideoPages;

	public VideoItem(String Title, String Uploader, String Category, String Length, String Date, String Link,
					 String ImgLink, int NumberOfVideoPages, String SelectedCategory) {
		mTitle = Title;
		mUploader = Uploader;
		mCategory = Category;
		mLength = Length;
		mDate = Date;
		mLink = Link;
		mImgLink = ImgLink;
		mSelectedCategory = SelectedCategory;
		mNumberOfVideoPages = NumberOfVideoPages;
	}

	public int getNumberOfVideoPages() {
		return mNumberOfVideoPages;
	}

	public void setNumberOfVideoPages(int NumberOfVideoPages) {
		mNumberOfVideoPages = NumberOfVideoPages;
	}

	public String getLength() {
		return mLength;
	}

	public void setLength(String length) {
		mLength = length;
	}

	public String getDate() {
		return mDate;
	}

	public void setDate(String date) {
		mDate = date;
	}

	public String getLink() {
		return mLink;
	}

	public void setLink(String link) {
		mLink = link;
	}

	public String getImgLink() {
		return mImgLink;
	}

	public void setImgLink(String imgLink) {
		mImgLink = imgLink;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getSelectedCategory() {
		return mSelectedCategory;
	}

	public void setSelectedCategory(String selectedCategory) {
		mSelectedCategory = selectedCategory;
	}

	public String getUploader() {
		return mUploader;
	}

	public void setmUploader(String Uploader) {
		mUploader = Uploader;
	}

	public String getCategory() {
		return mCategory;
	}

	public void setCategory(String category) {
		mCategory = category;
	}

}