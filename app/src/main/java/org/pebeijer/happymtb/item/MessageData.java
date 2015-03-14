package org.pebeijer.happymtb.item;

import java.io.Serializable;
import java.util.List;

public class MessageData implements Serializable {
	private static final long serialVersionUID = 201110070001L; 
	
	private int mCurrentPage;
	private int mMaxPages;
	private boolean mLogined;
	private String mThreadId;
	private List<Message> mMessages;
	private int mListPosition;
	
	public MessageData(int CurrentPage, int MaxPages, boolean mLogined, String ThreadId, List<Message> Messages, int ListPosition) {
		setCurrentPage(CurrentPage);
		setMaxPages(MaxPages);
		setLogined(mLogined);
		setThreadId(ThreadId);
		setMessages(Messages);
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

	public String getThreadId() {
		return mThreadId;
	}

	public void setThreadId(String ThreadId) {
		this.mThreadId = ThreadId;
	}

	public List<Message> getMessages() {
		return mMessages;
	}

	public void setMessages(List<Message> Messages) {
		this.mMessages = Messages;
	}

	public int getListPosition() {
		return mListPosition;
	}

	public void setListPosition(int ListPosition) {
		this.mListPosition = ListPosition;
	}

	public boolean getLogined() {
		return mLogined;
	}

	public void setLogined(boolean Logined) {
		this.mLogined = Logined;
	}
}
