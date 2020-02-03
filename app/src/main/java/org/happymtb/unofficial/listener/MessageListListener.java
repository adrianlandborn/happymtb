package org.happymtb.unofficial.listener;

import org.happymtb.unofficial.item.Message;

import java.util.List;

public interface MessageListListener 
{
	void success(List<Message> messages);
	void fail();
}
