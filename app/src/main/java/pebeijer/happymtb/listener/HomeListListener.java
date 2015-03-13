package pebeijer.happymtb.listener;
import java.util.List;

import pebeijer.happymtb.item.Home;

public interface HomeListListener {
	public void Success(List<Home> home);	
	public void Fail();
}
