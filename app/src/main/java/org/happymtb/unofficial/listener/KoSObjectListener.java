package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.KoSObjectItem;

public interface KoSObjectListener {
	void success(KoSObjectItem ksobjectitem);
	void fail();
}
