package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.VideoItem;

public interface VideoListListener {
	public void success(List<VideoItem> VideoItems);
	public void fail();
}
