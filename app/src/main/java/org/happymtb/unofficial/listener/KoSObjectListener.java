package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.KoSObjectItem;

public interface KoSObjectListener {
	public void success(KoSObjectItem ksobjectitem);
	public void fail();
}
