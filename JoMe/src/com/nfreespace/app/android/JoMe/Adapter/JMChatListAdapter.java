package com.nfreespace.app.android.JoMe.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.constant;
import com.nfreespace.app.android.JoMe.Object.JMChatObject;

public class JMChatListAdapter extends BaseAdapter {
	private Context 		m_Context;
	private LayoutInflater 	layoutInflater;  
	private List<JMChatObject>    mList;
	
	
	public JMChatListAdapter(Context context,List<JMChatObject> list) {
	           	 	
		m_Context=context;
		layoutInflater = LayoutInflater.from(m_Context);  
		mList = list;
	}
	 
	@Override
	public int getCount() {
	    return mList.size();
	}
	 
	@Override
	public Object getItem(int position) {
		return null;
	}
	 
	@Override
	public long getItemId(int position) {
		return position;
	}
	 
	public View getView( final int pos, View convertView, ViewGroup parent) {
		final ViewSend    sendholder;
		final ViewReceive receiveHolder;
		
		int typePos = getItemViewType(pos);
		if(typePos == 0)
		{
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_receive_chat_item, null);
				receiveHolder = new ViewReceive();
				receiveHolder.receiveMessage = (TextView) convertView.findViewById(R.id.receive_message);
				receiveHolder.receiveDate = (TextView) convertView.findViewById(R.id.receive_time);
				receiveHolder.userImage = (ImageView) convertView.findViewById(R.id.user_imageview);
				convertView.setTag(receiveHolder);
	            
			} else {         
				receiveHolder = (ViewReceive) convertView.getTag();         
			}
	          
			/// profile name
			receiveHolder.receiveMessage.setText(mList.get(pos).message);
			receiveHolder.receiveDate.setText(mList.get(pos).sendTime);
			
		}else
		{
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.list_send_chat_item, null);
				sendholder = new ViewSend();
				sendholder.sendMessage = (TextView) convertView.findViewById(R.id.send_message);
				sendholder.sendDate = (TextView) convertView.findViewById(R.id.send_time);
				convertView.setTag(sendholder);
			} else {         
				sendholder = (ViewSend) convertView.getTag();         
			}  
	       
			sendholder.sendMessage.setText(mList.get(pos).message);
			sendholder.sendDate.setText(mList.get(pos).sendTime);
		}
		
		return convertView;         
	}
	     
	@Override
	public int getItemViewType(int position)
	{
		if(!mList.get(position).sendId.equals(constant.gUserId))
			return 0;
		else
			return 1;
	}
	     
	@Override
	public int getViewTypeCount()
	{
		return 2;
	}
	     
	public String getSavename(String str)
	{
		str = str.replace("/", "");
		return str;
	}
	   
	   
	static class ViewSend {
		TextView sendMessage;
		TextView sendDate;
	}   
	     
	static class ViewReceive {
		TextView receiveMessage;
		TextView receiveDate;
		ImageView   userImage;
	}
	   
}