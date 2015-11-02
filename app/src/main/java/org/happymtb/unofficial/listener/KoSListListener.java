package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.KoSItem;

public interface KoSListListener {
	void success(List<KoSItem> ksitem);
	void fail();
}
