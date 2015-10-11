package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.view.MessageRowView;
import org.happymtb.unofficial.item.Message;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListMessagesAdapter extends BaseAdapter 
{
	private Context mContext;
	private List<Message> mMessages = new ArrayList<Message>();
	
	public ListMessagesAdapter(Context context, List<Message> Messages)
	{
		mContext = context;
		mMessages = Messages;
	}	
	
	@Override
	public int getCount() {
		return mMessages.size();
	}
	
	@Override
	public Object getItem(int position)	{
		return  mMessages.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageRowView messageRowView;
		
        if (convertView == null) {  
        	messageRowView = new MessageRowView(mContext);
        } else {
        	messageRowView = (MessageRowView) convertView;
        }            
		       
        messageRowView.setTitle(mMessages.get(position).getTitle());
        messageRowView.setText(mMessages.get(position).getText());
        messageRowView.setWrittenBy("Postat av: " + mMessages.get(position).getWrittenBy());
        messageRowView.setDate("Datum: " + mMessages.get(position).getDate());
                
        return messageRowView;
	}

	public void setItems(List<Message> items) {
		mMessages = items;
	}
}
