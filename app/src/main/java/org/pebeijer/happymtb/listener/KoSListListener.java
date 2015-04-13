package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.KoSItem;

public interface KoSListListener {
	public void success(List<KoSItem> ksitem);
	public void fail();
}
