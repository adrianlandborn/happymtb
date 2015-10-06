package org.happymtb.unofficial.listener;
import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.item.Item;

public interface ItemListListener {
	public void success(ArrayList<Item> item);
	public void fail();
}
