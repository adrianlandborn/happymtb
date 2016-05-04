package org.happymtb.unofficial.listener;
import java.util.ArrayList;

import org.happymtb.unofficial.item.Home;

public interface HomeListListener {
	void success(ArrayList<Home> home);
	void fail();
}
