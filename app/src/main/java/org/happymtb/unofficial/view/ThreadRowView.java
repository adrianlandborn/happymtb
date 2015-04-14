package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThreadRowView extends LinearLayout {
	TextView mTitle;
	TextView mNew;
	TextView mPage;
	TextView mPage1;
	TextView mPage2;
	TextView mPage3;
	TextView mPage4;
	TextView mfooter;
	LinearLayout compoundView;
	int mTextSize = 11;
	private SharedPreferences preferences;

	public ThreadRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String TextSizeArray [] =  getResources().getStringArray(R.array.settings_textsize);
		mTextSize = Integer.parseInt(TextSizeArray[preferences.getInt("textsize", 0)]);		
		
		compoundView = (LinearLayout) inflater.inflate(R.layout.thread_row, this);

		mTitle = (TextView) compoundView.findViewById(R.id.thread_title);
		mNew = (TextView) compoundView.findViewById(R.id.thread_new);
		mfooter = (TextView) compoundView.findViewById(R.id.thread_footer);
		
		mPage = (TextView) compoundView.findViewById(R.id.thread_page);
		mPage1 = (TextView) compoundView.findViewById(R.id.thread_page_button_1);
		mPage2 = (TextView) compoundView.findViewById(R.id.thread_page_button_2);
		mPage3 = (TextView) compoundView.findViewById(R.id.thread_page_button_3);
		mPage4 = (TextView) compoundView.findViewById(R.id.thread_page_button_4);
		
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);	
		mfooter.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize - 2);			
		mNew.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPage.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPage1.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPage2.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPage3.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
		mPage4.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize + 2);
	}

	public void setBackgroundColor(int color) {
		compoundView.setBackgroundResource(color);
	}

	public void setPage(int number_of_pages) {			
		mPage1.setText("1");
		if (number_of_pages < 2) {
			mPage.setVisibility(GONE);
			mPage1.setVisibility(GONE);
			mPage2.setVisibility(GONE);
			mPage3.setVisibility(GONE);
			mPage4.setVisibility(GONE);
		} else if (number_of_pages == 2) {
			mPage.setVisibility(VISIBLE);
			mPage1.setVisibility(VISIBLE);
			mPage2.setVisibility(VISIBLE);
			mPage3.setVisibility(GONE);
			mPage4.setVisibility(GONE);			
			mPage2.setText("2");
		} else if (number_of_pages == 3) {
			mPage.setVisibility(VISIBLE);
			mPage1.setVisibility(VISIBLE);
			mPage2.setVisibility(VISIBLE);
			mPage3.setVisibility(VISIBLE);
			mPage4.setVisibility(GONE);			
			mPage2.setText("2");
			mPage3.setText("3");
		} else if (number_of_pages == 4) {
			mPage.setVisibility(VISIBLE);
			mPage1.setVisibility(VISIBLE);
			mPage2.setVisibility(VISIBLE);
			mPage3.setVisibility(VISIBLE);
			mPage4.setVisibility(VISIBLE);			
			mPage2.setText("2");
			mPage3.setText("3");
			mPage4.setText("4");
		} else if (number_of_pages > 4) {
			mPage.setVisibility(VISIBLE);
			mPage1.setVisibility(VISIBLE);
			mPage2.setVisibility(VISIBLE);
			mPage3.setVisibility(VISIBLE);
			mPage4.setVisibility(VISIBLE);			
			mPage2.setText("" + (number_of_pages - 2));
			mPage3.setText("" + (number_of_pages - 1));
			mPage4.setText("" + (number_of_pages));
		}
	}
		
	public void setTitle(String text) {
		if (mTitle != null)
			mTitle.setText(Html.fromHtml(text));
	}
	
	public void setNew(Boolean Value) {
		if (mNew != null) {
			if (Value == true)
				mNew.setVisibility(VISIBLE);
			else
				mNew.setVisibility(GONE);
		}
	}	

	public Boolean getNew() {
		return (mNew.getVisibility() == VISIBLE);
	}	
		
	public void setFooter(String text) {
		if (mfooter != null)
			mfooter.setText(Html.fromHtml(text));
	}
}