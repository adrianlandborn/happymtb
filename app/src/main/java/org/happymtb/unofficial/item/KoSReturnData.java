package org.happymtb.unofficial.item;

import java.util.List;

public class KoSReturnData {
	private int mMaxPages;
	private List<KoSListItem> mItems;


	public KoSReturnData(List<KoSListItem> items, int maxPages) {
		mItems = items;
		mMaxPages = maxPages;
	}

	public int getMaxPages() {
		return mMaxPages;
	}

	public List<KoSListItem> getItems() {
		return mItems;
	}

}
