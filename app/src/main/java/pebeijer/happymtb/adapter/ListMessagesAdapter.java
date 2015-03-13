package pebeijer.happymtb.adapter;

import java.util.ArrayList;
import java.util.List;

import pebeijer.happymtb.view.MessageRowView;
import pebeijer.happymtb.item.Message;

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
		MessageRowView MessageRowV = null;
		
        if (convertView == null) {  
        	MessageRowV = new MessageRowView(mContext);
        } else {
        	MessageRowV = (MessageRowView) convertView;
        }            
		       
        MessageRowV.setTitle(mMessages.get(position).getTitle());
        MessageRowV.setText(mMessages.get(position).getText());
        MessageRowV.setWrittenBy("Postat av: " + mMessages.get(position).getWrittenBy());
        MessageRowV.setDate("Datum: " + mMessages.get(position).getDate());        
                
        return MessageRowV;    
	}
}
