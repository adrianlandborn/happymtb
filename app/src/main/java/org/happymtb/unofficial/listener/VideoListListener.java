package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.VideoItem;

public interface VideoListListener {
	public void success(List<VideoItem> VideoItems);
	public void fail();
}
