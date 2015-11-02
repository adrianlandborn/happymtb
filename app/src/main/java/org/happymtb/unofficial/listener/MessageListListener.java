package org.happymtb.unofficial.listener;

import java.util.List;

import org.happymtb.unofficial.item.Message;

public interface MessageListListener 
{
	void success(List<Message> messages);
	void fail();
}
