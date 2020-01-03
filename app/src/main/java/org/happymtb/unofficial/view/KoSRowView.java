package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class KoSRowView extends LinearLayout {
	TextView mTitle;
	TextView mTime;
	TextView mArea;
	TextView mImgLink;
	TextView mCategory;
	TextView mPrice;
	ImageView mObjectImageView;
	LinearLayout compoundView;
	View mRowColor;
	View mBottomPadding;

	public KoSRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		compoundView = (LinearLayout) inflater.inflate(R.layout.kos_row, this);

		mRowColor = compoundView.findViewById(R.id.kos_picture_row_color);
		mTitle = compoundView.findViewById(R.id.kos_picture_row_title);
		mTime = compoundView.findViewById(R.id.kos_picture_row_time);
		mArea = compoundView.findViewById(R.id.kos_picture_row_area);
		mCategory = compoundView.findViewById(R.id.kos_picture_row_category);
		mPrice = compoundView.findViewById(R.id.kos_picture_row_price);
		mObjectImageView = compoundView.findViewById(R.id.kos_picture_row_image);
		mBottomPadding = compoundView.findViewById(R.id.kos_bottom);
	}

	public void setObjectImage(Drawable image) {
		if (mObjectImageView != null) {
			if (image == null) {
				image =  ContextCompat.getDrawable(getContext(), R.drawable.no_photo);
			}
			mObjectImageView.setImageDrawable(image);
		}
	}

	public ImageView getImageView() {
		return mObjectImageView;
	}

	public void setRowBackgroundColor(int id) {
		if (mRowColor != null) {
			mRowColor.setBackgroundResource(id);
		}
	}	
	
	public void setTitle(String title) {
		if (mTitle != null) {
			mTitle.setText(title);
		}
	}

	public void setTime(String time) {
		if (mTime != null) {
			mTime.setText(time);
		}
	}

	public void setArea(String area) {
		if (mArea != null) {
			mArea.setText(area);
		}
	}

	public void setImgLink(String imgLink) {
		if (mImgLink != null) {
			mImgLink.setText(imgLink);
		}
	}

	public void setCategory(String category) {
		if (mCategory != null) {
			mCategory.setText(category);
		}
	}

	public void setPrice(String price) {
		if (mPrice != null) {
			mPrice.setText(price);
		}
	}

	public void setBottomPaddingVisible(boolean visible) {
		if (visible) {
			mBottomPadding.setVisibility(VISIBLE);
		} else {
			mBottomPadding.setVisibility(GONE);
		}

	}
}
