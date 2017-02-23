package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.KoSListItem;
import org.happymtb.unofficial.item.KoSReturnData;

public interface KoSListListener {
	void success(KoSReturnData data);
	void fail();
}
