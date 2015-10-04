package com.nfreespace.app.android.JoMe.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nfreespace.app.android.JoMe.JMCallbackReply;
import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.Object.JMQuestionObject;

public class JMEventMessageListAdapter extends BaseExpandableListAdapter {
	 private Context _context;   
	 private JMCallbackReply mCallback;
	 List<JMQuestionObject> mList = null;
	 
	 
	 public JMEventMessageListAdapter(Context context, 	JMCallbackReply callback,List<JMQuestionObject> list ) {
		 this._context = context;     
		 mCallback = callback;
		 mList = list;
	 }
	 
	 @Override
	 public Object getChild(int groupPosition, int childPosititon) {
		 //return constant.groupList.get(groupPosition).tokencodeArr.get(childPosititon);
		 return null;
	 }
	 
	 @Override
	 public long getChildId(int groupPosition, int childPosition) {
		 return childPosition;
	 }	
	 
	 @Override
	 public View getChildView(int groupPosition, final int childPosition,
			 boolean isLastChild, View convertView, ViewGroup parent) {
		 
		 final ViewChildHolder childholder ;   
	     
		 //final String childText = (String) getChild(groupPosition, childPosition);
	 
		 if (convertView == null) {
			 LayoutInflater infalInflater = (LayoutInflater) this._context
					 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = infalInflater.inflate(R.layout.list_reply_item, null);           
			 childholder = new ViewChildHolder();
			 childholder.replymessage = (TextView) convertView.findViewById(R.id.reply_message); 
			 childholder.replyName=(TextView)convertView.findViewById(R.id.reply_name);
			 childholder.replytime=(TextView)convertView.findViewById(R.id.reply_time);
			
			 convertView.setTag(childholder);
		 }else {
	         
			 childholder = (ViewChildHolder) convertView.getTag();         
	     }           
	      
		 childholder.replymessage.setText(mList.get(groupPosition).replyList.get(childPosition).message);
		 childholder.replyName.setText(String.format("by %s",mList.get(groupPosition).replyList.get(childPosition).senderName));
		 childholder.replytime.setText(mList.get(groupPosition).replyList.get(childPosition).messageTime);
		 return convertView;
	 }
	 
	 @Override
	 public int getChildrenCount(int groupPosition) {
		 
		 if(mList.get(groupPosition).replyList != null)
			 return mList.get(groupPosition).replyList.size();
		 else
			 return 0;
	 }
	 
	 @Override
	 public Object getGroup(int groupPosition) {
	     
		 //return constant.groupList.get(groupPosition);
	     return null;
	 }
	 
	 @Override
	 public int getGroupCount() {
		 return mList.size();
	 }	
	 
	 @Override
	 public long getGroupId(int groupPosition) {
		 return groupPosition;
	 }	
	 
	 @Override
	 public View getGroupView(final int groupPosition, boolean isExpanded,
			 View convertView, ViewGroup parent) {
	     	
		 final ViewHolder holder ;      
	     if (convertView == null) {        
	                    
			 LayoutInflater infalInflater = (LayoutInflater) this._context
					 .getSystemService(Context.LAYOUT_INFLATER_SERVICE);          
	            	
			 convertView = infalInflater.inflate(R.layout.list_question_item, null);
			 holder = new ViewHolder();
			 holder.username = (TextView) convertView.findViewById(R.id.item_username); 
			 holder.message=(TextView)convertView.findViewById(R.id.item_message);
			 holder.time=(TextView)convertView.findViewById(R.id.item_time);
			 holder.replyBtn=(Button)convertView.findViewById(R.id.item_reply_btn);
	
			  ExpandableListView eLV = (ExpandableListView)parent;
			  eLV.expandGroup(groupPosition);
			    
			 convertView.setTag(holder);
		 } else {
			 holder = (ViewHolder) convertView.getTag();         
	     }  
	     
	     holder.username.setText(mList.get(groupPosition).senderName);
	     holder.message.setText(mList.get(groupPosition).message);
	     holder.time.setText(mList.get(groupPosition).messageTime);
	     holder.replyBtn.setFocusable(false);
	     
	     holder.replyBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(mCallback != null)
						mCallback.replyMessage(groupPosition);
				}
		});
	        
		return convertView;
	 }	
	 
	 @Override
	 public boolean hasStableIds() {
		 return false;
	 }
	 
	 @Override
	 public boolean isChildSelectable(int groupPosition, int childPosition) {
		 return true;
	 }	
	    
	 static class ViewHolder {
		 TextView 		username;
		 TextView 		message;
		 TextView 		time;
		 Button    	    replyBtn;       
		 
	 }    
	    
	 static class ViewChildHolder {
		 TextView 	replymessage;
		 TextView  	replyName;
		 TextView 	replytime;
	 }    
}