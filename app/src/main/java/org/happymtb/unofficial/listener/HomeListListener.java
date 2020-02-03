package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.HomeItem;

import java.util.ArrayList;

public interface HomeListListener {
	void success(ArrayList<HomeItem> homeItem);
	void fail();
}
