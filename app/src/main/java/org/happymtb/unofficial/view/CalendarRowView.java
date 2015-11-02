package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalendarRowView extends LinearLayout {
	TextView mTitle;
	TextView mDescription;
	TextView mCategory;
	TextView mTime;
	
	LinearLayout compoundView;
	View mRowColor;

	public CalendarRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        compoundView = (LinearLayout) inflater.inflate(R.layout.calendar_row, this);
			
		mRowColor = compoundView.findViewById(R.id.calendar_row_color);
		mTitle = (TextView) compoundView.findViewById(R.id.calendar_row_title);
		mDescription = (TextView) compoundView.findViewById(R.id.calendar_row_description);
		mCategory = (TextView) compoundView.findViewById(R.id.calendar_row_category);
	
		mTime = (TextView) compoundView.findViewById(R.id.calendar_row_time);
	}

	public void setRowBackgroundColor(int id) {
		if (mRowColor != null) {
			mRowColor.setBackgroundResource(id);
		}
	}	
	
	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setTime(String Time) {
		if (mTime != null) {
			mTime.setText(Time);
		}
	}

	public void setDescription(String Description) {
		if (mDescription != null) {
			mDescription.setText(Description);
		}
	}

	public void setCategory(String Category) {
		if (mCategory != null) {
			mCategory.setText(Category);
		}
	}
}
