package org.pebeijer.happymtb.listener;

import org.pebeijer.happymtb.item.KoSObjectItem;

public interface KoSObjectListener {
	public void Success(KoSObjectItem ksobjectitem);	
	public void Fail();
}
