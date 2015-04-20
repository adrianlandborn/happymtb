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

public class CalendarRowView extends LinearLayout {
	TextView mTitle;
	TextView mDescription;
	TextView mCategory;
	TextView mTime;
	
	LinearLayout compoundView;
	LinearLayout mRowColor;
	private int mTextSize = 11;

	public CalendarRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mTextSize = HappyUtils.getTextSize(context);

        compoundView = (LinearLayout) inflater.inflate(R.layout.calendar_row, this);
			
		mRowColor = (LinearLayout) compoundView.findViewById(R.id.calendar_row_color);	
		mTitle = (TextView) compoundView.findViewById(R.id.calendar_row_title);
		mDescription = (TextView) compoundView.findViewById(R.id.calendar_row_description);
		mCategory = (TextView) compoundView.findViewById(R.id.calendar_row_category);
	
		mTime = (TextView) compoundView.findViewById(R.id.calendar_row_time);
		
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);		
		mDescription.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);	
		mCategory.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
		mTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);

	}

	public void setBackgroundColor(int color) {
		LinearLayout mRow = (LinearLayout) compoundView
				.findViewById(R.id.kos_row);
		mRow.setBackgroundResource(color);
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
