package org.happymtb.unofficial.adapter;

import java.util.ArrayList;
import java.util.List;

import org.happymtb.unofficial.MainActivity;
import org.happymtb.unofficial.PostsActivity;
import org.happymtb.unofficial.R;
import org.happymtb.unofficial.view.ThreadRowView;
import org.happymtb.unofficial.item.Thread;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListThreadsAdapter extends BaseAdapter 
{
	private Context mContext;
	protected ListView mListView;
	private List<Thread> mThreads = new ArrayList<Thread>();
	
	public ListThreadsAdapter(Context context, List<Thread> threads) {
		mContext = context;
		mThreads = threads;
	}

	@Override
	public int getCount() {
		return mThreads.size();
	}

	@Override
	public Object getItem(int position)	{
		return  mThreads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItems(List<Thread> threads) {
		mThreads = threads;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {			
		ThreadRowView threadRowView;
		
        if (convertView == null) {  
        	threadRowView = new ThreadRowView(mContext);
        } else {
        	threadRowView = (ThreadRowView) convertView;
        }            
		
        Thread thread = mThreads.get(position);
               
       	threadRowView.setNew(thread.getNewMsg());
        threadRowView.setTitle(thread.getTitle());
        threadRowView.setFooter("av <b>" + thread.getStartedBy() + "</b>, senaste " + thread.getLastMessageDate() + " av <b>" + thread.getLastMessageBy() + "</b> (" + Integer.toString(thread.getNumberOfMessages()) + ")");
        
        threadRowView.setPage(thread.getNumberOfPages());
        
        LinearLayout ThreadRow = (LinearLayout) threadRowView.findViewById(R.id.thread_row);
        
        ThreadRow.setTag(position);       
        ThreadRow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, false);
        		Message.putExtra(PostsActivity.PAGE, 1);
        		mContext.startActivity(Message);        		
            }            
        });    
        
        ThreadRow.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);
            	Toast.makeText(mContext,thread.getMessageText(),Toast.LENGTH_LONG).show();
            	//mToast.setText(thread.getMessageText());
            	//mToast.show();
				return true;
			}
	    });		        
               
        TextView New = (TextView) threadRowView.findViewById(R.id.thread_new);
        
        New.setTag(position);       
        New.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, true);
        		Message.putExtra(PostsActivity.PAGE, 1);
        		mContext.startActivity(Message);        		
            }
        });                
        
        TextView Page1 = (TextView) threadRowView.findViewById(R.id.thread_page_button_1);
        
        Page1.setTag(position);       
        Page1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, false);
        		Message.putExtra(PostsActivity.PAGE, 1);
        		mContext.startActivity(Message);        		
            }
        });                        
        
        TextView Page2 = (TextView) threadRowView.findViewById(R.id.thread_page_button_2);
        Page2.setTag(position);       
        Page2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, false);
        		if (thread.getNumberOfPages() > 4) {
        			Message.putExtra(PostsActivity.PAGE, thread.getNumberOfPages() - 2);
        		} else {
        			Message.putExtra(PostsActivity.PAGE, 2);
        		}
        		mContext.startActivity(Message);        		
            }
        });   
        
        TextView Page3 = (TextView) threadRowView.findViewById(R.id.thread_page_button_3);
        Page3.setTag(position);       
        Page3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, false);
        		if (thread.getNumberOfPages() > 4) {
        			Message.putExtra(PostsActivity.PAGE, thread.getNumberOfPages() - 1);
        		} else {
        			Message.putExtra(PostsActivity.PAGE, 3);
        		}
        		mContext.startActivity(Message);        		
            }
        });   
        
        TextView Page4 = (TextView) threadRowView.findViewById(R.id.thread_page_button_4);
        Page4.setTag(position);       
        Page4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {           	
            	int position = (Integer) v.getTag();            	
            	Thread thread = mThreads.get(position);          	            	            	
            	Intent Message = new Intent(mContext, PostsActivity.class);
        		Message.putExtra(PostsActivity.THREAD_ID, thread.getThreadId());
        		Message.putExtra(PostsActivity.LOGGED_IN, ((MainActivity) mContext).isLoggedIn());
        		Message.putExtra(PostsActivity.NEW_POST, false);
        		if (thread.getNumberOfPages() > 4) {
        			Message.putExtra(PostsActivity.PAGE, thread.getNumberOfPages());
        		} else {
        			Message.putExtra(PostsActivity.PAGE, 4);
        		}
        		mContext.startActivity(Message);        		
            }
        });           
        
        return threadRowView;
	}	
}
