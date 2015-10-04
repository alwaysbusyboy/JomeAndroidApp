package com.nfreespace.app.android.JoMe.Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.Object.JMPeerObject;

public class JMHistoryAdapter extends BaseAdapter {
	private Context 		m_Context;
	private LayoutInflater 	layoutInflater;  
	List<JMPeerObject> mlist = new ArrayList<JMPeerObject>();
	
	public JMHistoryAdapter(Context context,List<JMPeerObject> list) {
	           	 	
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
			
			convertView = layoutInflater.inflate(R.layout.list_peer_item, null);
			holder = new ViewHolder();
			
			holder.peername= (TextView) convertView.findViewById(R.id.peer_name);
			holder.imageView = (ImageView) convertView.findViewById(R.id.user_image);
			
			convertView.setTag(holder);
			
		} else {        	
			holder = (ViewHolder) convertView.getTag();         
		}  
	
		holder.peername.setText(String.format("History with %s %s", mlist.get(position).firstName,mlist.get(position).lastName));
	       
		String url = mlist.get(position).imageUrl;
		holder.imageView.setTag(position);
        File filepath = m_Context.getFileStreamPath(getSaveName(url));
        if(filepath.exists())
        {
              final Uri uri = Uri.parse(filepath.toString());
              holder.imageView.setImageURI(uri);
        }else{                    
       
        	/// show default image while download image from server
        	holder.imageView.setImageResource(R.drawable.default_social);
        
        	if(!url.equals(""))
        	{
        		try{
        			new ImageDownloaderTask(holder.imageView,m_Context,getSaveName(url),position).execute(url);
        		}catch(Exception e)
        		{
        					
        		}
        	}
        }
		return convertView;         
	}
	
	public String getSaveName(String str)
	{
		str = str.replace("/", "");
		return str;
	}
	 
	static class ViewHolder {
		
		TextView  peername;
		ImageView imageView;
	}    
	    
	  
}

