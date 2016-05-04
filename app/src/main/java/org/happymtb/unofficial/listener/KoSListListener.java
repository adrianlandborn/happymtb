package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.KoSListItem;

public interface KoSListListener {
	void success(List<KoSListItem> ksitem);
	void fail();
}
