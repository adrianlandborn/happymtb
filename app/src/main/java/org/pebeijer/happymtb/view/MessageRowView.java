package org.pebeijer.happymtb.view;

import org.pebeijer.happymtb.R;
import org.pebeijer.happymtb.helpers.URLImageParser;
import android.content.Context;
import android.content.SharedPreferences;

import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageRowView extends LinearLayout {
	TextView mTitle;
	TextView mText;	
	TextView mWrittenBy;
	TextView mDate;	
	LinearLayout mRow;
	LinearLayout compoundView;
	int mTextSize = 11;
	Context mContext;
	private SharedPreferences preferences;

	public MessageRowView(Context context) {
		super(context);
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 0)]);		
		
		compoundView = (LinearLayout)inflater.inflate(R.layout.message_row, this);
		
		mTitle = (TextView) compoundView.findViewById(R.id.message_title);
		mText = (TextView) compoundView.findViewById(R.id.message_text);
		mWrittenBy = (TextView) compoundView.findViewById(R.id.message_written_by);
		mDate = (TextView) compoundView.findViewById(R.id.message_date);
		
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
		mWrittenBy.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);	
		mDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);					
	}
		
	public void setTitle(String Title) {
		if (mTitle != null)	{
			mTitle.setText(Title);		
		}
	}

	public void setText(String text) {	
		if (mText != null) {			
			URLImageParser p = new URLImageParser(mText, mContext);
			Spanned htmlSpan = Html.fromHtml(text, p, null);
			mText.setMovementMethod(LinkMovementMethod.getInstance());			
			mText.setText(htmlSpan);							
		}
	}
	
	public void setWrittenBy(String WrittenBy) {
		if (mWrittenBy != null) {			
			URLImageParser p = new URLImageParser(mWrittenBy, mContext);
			Spanned htmlSpan = Html.fromHtml(WrittenBy, p, null);
			mWrittenBy.setMovementMethod(LinkMovementMethod.getInstance());			
			mWrittenBy.setText(htmlSpan);							
		}		
	}

	public void setDate(String Date) {
		if (mDate != null) {
			mDate.setText(Date);					
		}
	}
}



