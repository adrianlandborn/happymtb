package pebeijer.happymtb.listener;

import java.util.List;

import pebeijer.happymtb.item.Message;

public interface MessageListListener 
{
	public void Success(List<Message> messages);	
	public void Fail();
}
