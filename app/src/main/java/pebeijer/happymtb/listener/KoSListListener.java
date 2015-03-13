package pebeijer.happymtb.listener;
import java.util.List;

import pebeijer.happymtb.item.KoSItem;

public interface KoSListListener {
	public void Success(List<KoSItem> ksitem);	
	public void Fail();
}
