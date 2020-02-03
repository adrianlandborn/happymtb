package org.happymtb.unofficial.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoData implements Serializable {
	private static final long serialVersionUID = 2011103070001L; 

	private int mCurrentPage = 1;
	private int mMaxPages = 1;
	private String mSearch = "";
	private int mCategory = 0;
	private List<VideoItem> mVideoItems = new ArrayList<>();
	private int mListPosition;	
	
	public VideoData(int CurrentPage, int MaxPages, String Search, int Category, List<VideoItem> VideoItems, int ListPosition) {
		setCurrentPage(CurrentPage);
		setMaxPages(MaxPages);
		setSearch(Search);
		setCategory(Category);
		setVideoItems(VideoItems);
		setListPosition(ListPosition);
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int CurrentPage) {
		this.mCurrentPage = CurrentPage;
	}

	public int getMaxPages() {
		return mMaxPages;
	}

	public void setMaxPages(int MaxPages) {
		this.mMaxPages = MaxPages;
	}

	public String getSearch() {
		return mSearch;
	}

	public void setSearch(String Search) {
		this.mSearch = Search;
	}

	public int getCategory() {
		return mCategory;
	}

	public void setCategory(int Category) {
		this.mCategory = Category;
	}

	public List<VideoItem> getVideoItems() {
		return mVideoItems;
	}

	public void setVideoItems(List<VideoItem> VideoItems) {
		this.mVideoItems = VideoItems;
	}

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int ListPosition) {
		this.mListPosition = ListPosition;
	}
}
