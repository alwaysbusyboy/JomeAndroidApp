package com.nfreespace.app.android.JoMe.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.constant;
import com.nfreespace.app.android.JoMe.Object.GetCategory;

public class EventListAdapter extends BaseAdapter {
	private Context 		m_Context;
	private LayoutInflater 	layoutInflater;  
	ArrayList<GetCategory> 	mlist = new ArrayList<GetCategory>();
	
	public EventListAdapter(Context context,ArrayList<GetCategory> list) {
	           	 	
		m_Context=context;
		layoutInflater = LayoutInflater.from(m_Context);  
		mlist = list;
	}
	 
	@Override
	public int getCount() {
		return mlist.size();
	}
	 
	@Override
	public Object getItem(int position) {
		return null;
	}
	 
	@Override
	public long getItemId(int position) {
		return position;
	}
	 
	public View getView( final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
	
		if (convertView == null) {
			
			convertView = layoutInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon_image);
			holder.eventname = (TextView) convertView.findViewById(R.id.eventname);
			holder.distance = (TextView) convertView.findViewById(R.id.distance);
			holder.frnd_invite = (TextView) convertView.findViewById(R.id.friendsinvite);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
			
		} else {        	
			holder = (ViewHolder) convertView.getTag();         
		}  
		
	
		holder.imageView.setImageResource(constant.getImageId(mlist.get(position).type));
	    holder.eventname.setText(mlist.get(position).event_name);
	    holder.distance.setText(mlist.get(position).distance);
	    holder.frnd_invite.setText(String.format("Male: %s, Female: %s", mlist.get(position).joined_male,mlist.get(position).joined_female));
	    holder.time.setText(mlist.get(position).event_start_datetime);
	        
		return convertView;         
	}
	 
	static class ViewHolder {
		ImageView imageView;
		TextView  eventname , distance ,frnd_invite,time;
	}    
	    
	  
}

