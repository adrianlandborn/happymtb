package org.pebeijer.happymtb.listener;

import java.util.List;

import org.pebeijer.happymtb.item.Message;

public interface MessageListListener 
{
	public void success(List<Message> messages);
	public void fail();
}
