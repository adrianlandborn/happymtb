package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.VideoRowView;
import org.happymtb.unofficial.item.VideoItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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

		if (convertView == null) {
			videoRowView = new VideoRowView(mContext);
		} else {
			videoRowView = (VideoRowView) convertView;
		}

		videoRowView.setTitle(mVideoItems.get(position).getTitle());
		videoRowView.setDate(mVideoItems.get(position).getDate());
		videoRowView.setUploader(mVideoItems.get(position).getUploader());
		videoRowView.setCategory(mVideoItems.get(position).getCategory());
		videoRowView.setLength(mVideoItems.get(position).getLength());
		videoRowView.setObjectImage(mVideoItems.get(position).getObjectImage());

		return videoRowView;
	}
}