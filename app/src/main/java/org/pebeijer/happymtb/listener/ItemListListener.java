package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.Item;

public interface ItemListListener {
	public void Success(List<Item> item);	
	public void Fail();
}
