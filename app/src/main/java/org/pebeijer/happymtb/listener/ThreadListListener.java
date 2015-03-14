package org.pebeijer.happymtb.listener;

import java.util.List;

import org.pebeijer.happymtb.item.Thread;

public interface ThreadListListener 
{
	public void Success(List<Thread> threads);	
	public void Fail();
}
