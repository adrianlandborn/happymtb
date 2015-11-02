package org.happymtb.unofficial.listener;

import java.util.List;

import org.happymtb.unofficial.item.Thread;

public interface ThreadListListener 
{
	void success(List<Thread> threads);
	void fail();
}
