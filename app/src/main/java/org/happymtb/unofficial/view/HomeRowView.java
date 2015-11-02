package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeRowView extends LinearLayout {
	TextView mTitle;
	TextView mText;
	TextView mDate;
	LinearLayout compoundView;

	public HomeRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        compoundView = (LinearLayout) inflater.inflate(R.layout.home_row, this);
		
		mTitle = (TextView) compoundView.findViewById(R.id.home_row_title);
		mText = (TextView) compoundView.findViewById(R.id.home_row_text);
		mDate = (TextView) compoundView.findViewById(R.id.home_row_date);

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
