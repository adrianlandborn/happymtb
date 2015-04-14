package org.happymtb.unofficial.listener;

import java.util.List;

import org.happymtb.unofficial.item.Thread;

public interface ThreadListListener 
{
	public void success(List<Thread> threads);
	public void fail();
}
