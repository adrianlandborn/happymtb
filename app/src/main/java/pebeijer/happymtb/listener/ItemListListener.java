package pebeijer.happymtb.listener;
import java.util.List;

import pebeijer.happymtb.item.Item;

public interface ItemListListener {
	public void Success(List<Item> item);	
	public void Fail();
}
