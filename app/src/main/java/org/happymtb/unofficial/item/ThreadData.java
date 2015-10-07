package org.happymtb.unofficial.item;

import java.io.Serializable;
import java.util.List;

public class ThreadData implements Serializable {
	private static final long serialVersionUID = 201140070001L; 
	
	private List<Thread> mThreads;
	private int mCurrentPage;
	private int mMaxPages;
	private int mListPosition;
	private Boolean mLogined;
	
	public ThreadData() {
		mThreads = null;
		mCurrentPage = 1;
		mMaxPages = 0;
		mListPosition = 0;
		mLogined = false;
	}

	public List<Thread> getThreads() {
		return mThreads;
	}

	public void setThreads(List<Thread> threads) {
		this.mThreads = threads;
	}

	public int getCurrentPage() {
		return mCurrentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.mCurrentPage = currentPage;
	}

	public int getMaxPages() {
		return mMaxPages;
	}

	public void setMaxPages(int maxPages) {
		this.mMaxPages = maxPages;
	}

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int listPosition) {
		this.mListPosition = listPosition;
	}

	public Boolean getLoggedIn() {
		return mLogined;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.mLogined = loggedIn;
	}

}
