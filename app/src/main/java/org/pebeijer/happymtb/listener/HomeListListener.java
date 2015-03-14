package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.Home;

public interface HomeListListener {
	public void Success(List<Home> home);	
	public void Fail();
}
