package pebeijer.happymtb.item;

import java.io.Serializable;

public class Thread implements Serializable
{
	private static final long serialVersionUID = 201103090001L; 

	private String mTitle;
	private int mNumberOfMessages;
	private String mThreadId;
	private String mStartedBy;
	private String mLastMessageDate;
	private String mLastMessageBy;
	private String mMessageText;
	private int mNumberOfPages;
	private int mNumberOfThreadPages;
	private boolean mNewMsg;
	
	public Thread(String ThreadId, String Title, int NumberOfMessages, String StartedBy, String LastMessageDate, String LastMessageBy, String MessageText, int NumberOfPages, boolean NewMsg, int NumberOfThreadPages)
	{
		mThreadId = ThreadId;
		mTitle = Title;
		mNumberOfMessages = NumberOfMessages;
		mStartedBy = StartedBy;
		mLastMessageDate = LastMessageDate;
		mLastMessageBy = LastMessageBy;
		mMessageText = MessageText;
		mNumberOfPages = NumberOfPages;
		mNewMsg = NewMsg;
		mNumberOfThreadPages = NumberOfThreadPages;
	}	

	public int getNumberOfThreadPages() 
	{
		return mNumberOfThreadPages;
	}
	
	public void setNumberOfThreadPages(int NumberOfThreadPages) 
	{
		mNumberOfThreadPages = NumberOfThreadPages;
	}	
	
	public boolean getNewMsg() 
	{
		return mNewMsg;
	}
	
	public String getThreadId() 
	{
		return mThreadId;
	}
	
	public String getTitle() 
	{
		return mTitle;
	}

	public int getNumberOfMessages() 
	{
		return mNumberOfMessages;
	}
	
	public String getStartedBy() 
	{
		return mStartedBy;
	}
	
	public String getLastMessageBy() 
	{
		return mLastMessageBy;
	}
	
	public String getLastMessageDate() 
	{
		return mLastMessageDate;
	}
	
	public String getMessageText() 
	{
		return mMessageText;
	}
	
	public int getNumberOfPages() 
	{
		return mNumberOfPages;
	}
}
