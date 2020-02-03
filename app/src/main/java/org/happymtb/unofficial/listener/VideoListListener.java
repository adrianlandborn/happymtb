package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.VideoItem;

import java.util.List;

public interface VideoListListener {
	void success(List<VideoItem> VideoItems);
	void fail();
}
