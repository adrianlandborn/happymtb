package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;

import android.content.Context;
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

	protected void init(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		compoundView = (LinearLayout) inflater.inflate(R.layout.item_row, this);

		mTitle = compoundView.findViewById(R.id.item_title);
		mHeaderTitle = compoundView.findViewById(R.id.item_header_title);
		mDescription = compoundView.findViewById(R.id.item_description);
		
		mHeaderExpand = compoundView.findViewById(R.id.item_header_expand);
		mHeaderCollapse = compoundView.findViewById(R.id.item_header_collapse);
		
		mLink = compoundView.findViewById(R.id.item_row_link);
		mHeader = compoundView.findViewById(R.id.item_row_header);
	}	
	
	public ItemRowView(Context context) {
		super(context);
		init(context);
	}

	public void setCollapse(boolean collapse) {
		if ((mHeaderExpand != null) && (mHeaderCollapse != null))  {
			if (collapse) {
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

	public void setHeaderVisible(boolean header) {
		if (mHeader != null) {
			if (header)	{
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
