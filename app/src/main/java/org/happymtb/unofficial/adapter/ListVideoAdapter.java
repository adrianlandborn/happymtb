package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.VideoRowView;
import org.happymtb.unofficial.item.VideoItem;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

public class ListVideoAdapter extends BaseAdapter {
	private Context mContext;
	private List<VideoItem> mVideoItems = new ArrayList<VideoItem>();

	public ListVideoAdapter(Context context, List<VideoItem> VideoItems) {
		mContext = context;
		mVideoItems = VideoItems;
	}		
	
	@Override
	public int getCount() {
		return mVideoItems.size();
	}

	@Override
	public VideoItem getItem(int position) {
		return mVideoItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		VideoRowView videoRowView;
		VideoItem item = mVideoItems.get(position);

		if (convertView == null) {
			videoRowView = new VideoRowView(mContext);
		} else {
			videoRowView = (VideoRowView) convertView;
		}

		videoRowView.setTitle(item.getTitle());
		videoRowView.setDate(item.getDate());
		videoRowView.setUploader(item.getUploader());
		videoRowView.setCategory(item.getCategory());
		videoRowView.setLength(item.getLength());
		if (!TextUtils.isEmpty(item.getImgLink())) {
			Picasso.with(mContext).load(item.getImgLink()).into(videoRowView.getImageView());
		} else {
			videoRowView.setObjectImage(null);
		}

		return videoRowView;
	}

	public void setItems(List<VideoItem> items) {
		mVideoItems = items;
	}
}