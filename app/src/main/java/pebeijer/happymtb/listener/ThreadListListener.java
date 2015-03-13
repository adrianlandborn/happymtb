package pebeijer.happymtb.listener;

import java.util.List;

import pebeijer.happymtb.item.Thread;

public interface ThreadListListener 
{
	public void Success(List<Thread> threads);	
	public void Fail();
}
