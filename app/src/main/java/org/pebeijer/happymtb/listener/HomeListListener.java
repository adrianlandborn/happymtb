package org.pebeijer.happymtb.listener;
import java.util.List;

import org.pebeijer.happymtb.item.Home;

public interface HomeListListener {
	public void success(List<Home> home);
	public void fail();
}
