package org.pebeijer.happymtb.listener;

import java.util.List;

import org.pebeijer.happymtb.item.Message;

public interface MessageListListener 
{
	public void Success(List<Message> messages);	
	public void Fail();
}
