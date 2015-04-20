package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.helpers.HappyUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemRowView extends LinearLayout {
	TextView mTitle;
	TextView mHeaderTitle;
	TextView mDescription;
	LinearLayout compoundView;
	LinearLayout mLink;
	LinearLayout mHeader;
	ImageView mHeaderCollapse;
	ImageView mHeaderExpand;

	protected void Init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		compoundView = (LinearLayout) inflater.inflate(R.layout.item_row, this);

		mTitle = (TextView) compoundView.findViewById(R.id.item_title);
		mHeaderTitle = (TextView) compoundView.findViewById(R.id.item_header_title);
		mDescription = (TextView) compoundView.findViewById(R.id.item_description);
		
		mHeaderExpand = (ImageView) compoundView.findViewById(R.id.item_header_expand);
		mHeaderCollapse = (ImageView) compoundView.findViewById(R.id.item_header_collapse);		
		
		mLink = (LinearLayout) compoundView.findViewById(R.id.item_row_link);
		mHeader = (LinearLayout) compoundView.findViewById(R.id.item_row_header);	
	}	
	
	public ItemRowView(Context context) {
		super(context);
		Init(context);
	}

	public void setCollapse(Boolean Collapse) {
		if ((mHeaderExpand != null) && (mHeaderCollapse != null))  {
			if (Collapse == true) {
				mHeaderExpand.setVisibility(GONE);
				mHeaderCollapse.setVisibility(VISIBLE);
			} else {
				mHeaderExpand.setVisibility(VISIBLE);
				mHeaderCollapse.setVisibility(GONE);
			}
		}
	}	
	
	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setHeaderVisible(Boolean Header) {
		if (mHeader != null) {
			if (Header == true)	{
				mHeader.setVisibility(VISIBLE);				
				mLink.setVisibility(GONE);
			} else {
				mHeader.setVisibility(GONE);
				mLink.setVisibility(VISIBLE);
			}
		}
	}
	
	public void setHeaderTitle(String HeaderTitle) {
		if (mHeaderTitle != null) {
			mHeaderTitle.setText(HeaderTitle);
		}
	}	
	
	public void setDescription(String Description) {
		if (mDescription != null) {
			mDescription.setText(Description);
		}
	}
}
