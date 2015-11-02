package org.happymtb.unofficial.listener;
import java.util.ArrayList;

import org.happymtb.unofficial.item.Item;

public interface ItemListListener {
	void success(ArrayList<Item> item);
	void fail();
}
