package pebeijer.happymtb.listener;

import pebeijer.happymtb.item.KoSObjectItem;

public interface KoSObjectListener {
	public void Success(KoSObjectItem ksobjectitem);	
	public void Fail();
}
