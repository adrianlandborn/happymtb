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
	
	public ThreadData(int CurrentPage, int MaxPages, List<Thread> Threads, int ListPosition, Boolean Logined) {
		setThreads(Threads);
		setCurrentPage(CurrentPage);
		setMaxPages(MaxPages);
		setListPosition(ListPosition);
		setLoggedIn(Logined);
	}

	public List<Thread> getThreads() {
		return mThreads;
	}

	public void setThreads(List<Thread> Threads) {
		this.mThreads = Threads;
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

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int ListPosition) {
		this.mListPosition = ListPosition;
	}

	public Boolean getLoggedIn() {
		return mLogined;
	}

	public void setLoggedIn(Boolean Logined) {
		this.mLogined = Logined;
	}

}
