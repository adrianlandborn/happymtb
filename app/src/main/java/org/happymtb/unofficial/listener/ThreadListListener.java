package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.Thread;

import java.util.List;

public interface ThreadListListener 
{
	void success(List<Thread> threads);
	void fail();
}
