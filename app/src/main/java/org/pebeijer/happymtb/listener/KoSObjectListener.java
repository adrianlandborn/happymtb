package org.pebeijer.happymtb.listener;

import org.pebeijer.happymtb.item.KoSObjectItem;

public interface KoSObjectListener {
	public void success(KoSObjectItem ksobjectitem);
	public void fail();
}
