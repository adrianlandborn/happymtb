package org.happymtb.unofficial.listener;
import java.util.ArrayList;

import org.happymtb.unofficial.item.HomeItem;

public interface HomeListListener {
	void success(ArrayList<HomeItem> homeItem);
	void fail();
}
