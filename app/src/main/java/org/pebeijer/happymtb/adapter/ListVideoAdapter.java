package org.pebeijer.happymtb.adapter;

import java.util.ArrayList;
import java.util.List;

import org.pebeijer.happymtb.view.VideoRowView;
import org.pebeijer.happymtb.item.VideoItem;

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
		VideoRowView VideoRowV = null;

		if (convertView == null) {
			VideoRowV = new VideoRowView(mContext);
		} else {
			VideoRowV = (VideoRowView) convertView;
		}

		VideoRowV.setTitle(mVideoItems.get(position).getTitle());
		VideoRowV.setDate(mVideoItems.get(position).getDate());
		VideoRowV.setUploader(mVideoItems.get(position).getUploader());
		VideoRowV.setCategory(mVideoItems.get(position).getCategory());
		VideoRowV.setLength(mVideoItems.get(position).getLength());
		VideoRowV.setObjectImage(mVideoItems.get(position).getObjectImage());

		return VideoRowV;
	}
}