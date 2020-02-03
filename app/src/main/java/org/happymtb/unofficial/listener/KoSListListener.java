package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.KoSReturnData;

public interface KoSListListener {
	void success(KoSReturnData data);
	void fail();
}
