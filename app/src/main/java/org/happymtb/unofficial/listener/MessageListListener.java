package org.happymtb.unofficial.listener;

import java.util.List;

import org.happymtb.unofficial.item.Message;

public interface MessageListListener 
{
	public void success(List<Message> messages);
	public void fail();
}
