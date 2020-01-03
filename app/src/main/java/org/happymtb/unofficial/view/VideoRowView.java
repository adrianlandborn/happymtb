package org.happymtb.unofficial.view;

import org.happymtb.unofficial.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VideoRowView extends LinearLayout {
	TextView mTitle;
	TextView mUploader;
	TextView mCategory;
	TextView mLength;
	TextView mDate;
	ImageView mObjectImage;
	LinearLayout compoundView;

	public VideoRowView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		compoundView = (LinearLayout) inflater.inflate(R.layout.video_row, this);

		mTitle = compoundView.findViewById(R.id.video_picture_row_title);
		mDate = compoundView.findViewById(R.id.video_picture_row_date);
		mUploader = compoundView.findViewById(R.id.video_picture_row_uploader);
		mCategory = compoundView.findViewById(R.id.video_picture_row_category);
		mLength = compoundView.findViewById(R.id.video_picture_row_length);
		mObjectImage = compoundView.findViewById(R.id.video_picture_row_image);
	}

	public void setObjectImage(Drawable image) {
		if (mObjectImage != null) {
			if (image == null) {
				image = ContextCompat.getDrawable(getContext(), R.drawable.no_video);
			}			
			mObjectImage.setImageDrawable(image);
		}
	}

	public void setTitle(String Title) {
		if (mTitle != null) {
			mTitle.setText(Title);
		}
	}

	public void setUploader(String Uploader) {
		if (mUploader != null) {
			mUploader.setText(Uploader);
		}
	}

	public void setCategory(String Category) {
		if (mCategory != null) {
			mCategory.setText(Category);
		}
	}
	
	public void setLength(String Length) {
		if (mLength != null) {
			mLength.setText(Length);
		}
	}
	
	public void setDate(String Date) {
		if (mDate != null) {
			mDate.setText(Date);
		}
	}

	public ImageView getImageView() {
		return mObjectImage;
	}
}
