package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.VideoItem;

public interface VideoListListener {
	public void Success(List<VideoItem> VideoItems);	
	public void Fail();
}
