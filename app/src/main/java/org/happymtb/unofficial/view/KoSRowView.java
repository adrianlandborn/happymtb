package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;
import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.helpers.HappyUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
	ImageView mObjectImage;
	LinearLayout compoundView;
	LinearLayout mRowColor;
	private Boolean mPictureList;
	private SharedPreferences preferences;

	public KoSRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		mPictureList = preferences.getBoolean(KoSListFragment.SHOW_IMAGES, true);
	
		if (mPictureList) {
			compoundView = (LinearLayout) inflater.inflate(R.layout.kos_picture_row, this);
			
			mRowColor = (LinearLayout) compoundView.findViewById(R.id.kos_picture_row_color);
			mTitle = (TextView) compoundView.findViewById(R.id.kos_picture_row_title);
			mTime = (TextView) compoundView.findViewById(R.id.kos_picture_row_time);
			mArea = (TextView) compoundView.findViewById(R.id.kos_picture_row_area);
			mCategory = (TextView) compoundView.findViewById(R.id.kos_picture_row_category);
			mPrice = (TextView) compoundView.findViewById(R.id.kos_picture_row_price);						
			mObjectImage = (ImageView) compoundView.findViewById(R.id.kos_picture_row_image);
		} else {
			compoundView = (LinearLayout) inflater.inflate(R.layout.kos_row, this);
			
			mRowColor = (LinearLayout) compoundView.findViewById(R.id.kos_row_color);	
			mTitle = (TextView) compoundView.findViewById(R.id.kos_row_title);
			mTime = (TextView) compoundView.findViewById(R.id.kos_row_time);
			mArea = (TextView) compoundView.findViewById(R.id.kos_row_area);
			mCategory = (TextView) compoundView.findViewById(R.id.kos_row_category);
			mPrice = (TextView) compoundView.findViewById(R.id.kos_row_price);		
		}		
	}

	public void setBackgroundColor(int color) {
		LinearLayout mRow = (LinearLayout) compoundView
				.findViewById(R.id.kos_row);
		mRow.setBackgroundResource(color);
	}

	public void setObjectImage(Drawable ObjectImage) {
		if (mObjectImage != null) {
			if (ObjectImage == null) {
				int imageResource = R.drawable.no_photo;
				Drawable d = getResources().getDrawable(imageResource);						
				ObjectImage = d;
			}
			mObjectImage.setImageDrawable(ObjectImage);
		}
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

	public void setArea(String Area) {
		if (mArea != null) {
			mArea.setText(Area);
		}
	}

	public void setImgLink(String ImgLink) {
		if (mImgLink != null) {
			mImgLink.setText(ImgLink);
		}
	}

	public void setCategory(String Category) {
		if (mCategory != null) {
			mCategory.setText(Category);
		}
	}

	public void setPrice(String Price) {
		if (mPrice != null) {
			mPrice.setText(Price);
		}
	}

}