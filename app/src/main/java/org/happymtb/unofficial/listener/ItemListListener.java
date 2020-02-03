package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.Item;

import java.util.ArrayList;

public interface ItemListListener {
	void success(ArrayList<Item> item);
	void fail();
}
