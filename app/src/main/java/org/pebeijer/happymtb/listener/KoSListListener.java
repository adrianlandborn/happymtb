package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.KoSItem;

public interface KoSListListener {
	public void Success(List<KoSItem> ksitem);	
	public void Fail();
}
