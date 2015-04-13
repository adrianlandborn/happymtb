package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.Item;

public interface ItemListListener {
	public void success(List<Item> item);
	public void fail();
}
