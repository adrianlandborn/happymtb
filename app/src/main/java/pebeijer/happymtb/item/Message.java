package pebeijer.happymtb.item;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 201103120001L;
	private String mTitle;
	private String mDate;
	private String mWrittenBy;
	private String mText;
	private int mNumberOfMessagePages;

	public Message(String Title, String Text, String WrittenBy, String Date, int NumberOfMessagePages) {
		mTitle = Title;
		mText = Text;
		mWrittenBy = WrittenBy;
		mDate = Date;
		mNumberOfMessagePages = NumberOfMessagePages;
	}

	public int getNumberOfMessagePages() {
		return mNumberOfMessagePages;
	}

	public void setNumberOfMessagePages(int NumberOfMessagePages) {
		mNumberOfMessagePages = NumberOfMessagePages;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String Title) {
		mTitle = Title;
	}
	
	public String getDate() {
		return mDate;
	}

	public void setDate(String Date) {
		mDate = Date;
	}	
	
	public String getWrittenBy() {
		return mWrittenBy;
	}
	
	public void setWrittenBy(String WrittenBy) {
		mWrittenBy = WrittenBy;
	}	
	
	public void setText(String Text) {
		mText = Text;
	}		

	public String getText() {
		return mText;
	}

}
