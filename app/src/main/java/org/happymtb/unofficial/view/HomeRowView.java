package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.helpers.HappyUtils;

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

	public HomeRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTextSize = HappyUtils.getTextSize(context);

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
