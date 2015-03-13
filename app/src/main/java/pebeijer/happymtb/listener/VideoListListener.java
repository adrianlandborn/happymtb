package pebeijer.happymtb.listener;
import java.util.List;

import pebeijer.happymtb.item.VideoItem;

public interface VideoListListener {
	public void Success(List<VideoItem> VideoItems);	
	public void Fail();
}
