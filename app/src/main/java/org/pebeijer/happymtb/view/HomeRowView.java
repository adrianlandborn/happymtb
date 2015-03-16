package org.pebeijer.happymtb.view;

import org.pebeijer.happymtb.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeRowView extends LinearLayout {
	TextView mTitle;
	TextView mText;
	TextView mDate;
	LinearLayout compoundView;
	private int mTextSize = 11;
	private SharedPreferences preferences;
	
	public HomeRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 0)]);		
		compoundView = (LinearLayout) inflater.inflate(R.layout.home_row, this);
		
		mTitle = (TextView) compoundView.findViewById(R.id.home_row_title);
		mText = (TextView) compoundView.findViewById(R.id.home_row_text);
		mDate = (TextView) compoundView.findViewById(R.id.home_row_date);

		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mText.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
		mDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);
	}

	public void setBackgroundColor(int color) {
		LinearLayout mRow = (LinearLayout) compoundView
				.findViewById(R.id.home_row);
		mRow.setBackgroundResource(color);
	}

	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setText(String Text) {
		if (mText != null) {
			mText.setText(Text);
		}
	}
	
	public void setDate(String Date) {
		if (mDate != null) {
			mDate.setText(Date);
		}
	}	
}
