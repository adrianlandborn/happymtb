package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.KoSItem;

public interface KoSListListener {
	public void success(List<KoSItem> ksitem);
	public void fail();
}
