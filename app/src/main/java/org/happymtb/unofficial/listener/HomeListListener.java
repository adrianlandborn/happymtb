package org.happymtb.unofficial.listener;
import java.util.List;

import org.happymtb.unofficial.item.Home;

public interface HomeListListener {
	public void success(List<Home> home);
	public void fail();
}
