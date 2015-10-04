package com.nfreespace.app.android.JoMe.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.Image.ImageLoader;
import com.nfreespace.app.android.JoMe.Object.JMPhotoObject;

public class JMPhotoListAdapter extends BaseAdapter {
	private Context 		m_Context;
	private LayoutInflater 	layoutInflater;  
	private List<JMPhotoObject>    mList;
	private ImageLoader     mImageLoader;
	
	public JMPhotoListAdapter(Context context,List<JMPhotoObject> list) {
	           	 	
		m_Context=context;
		layoutInflater = LayoutInflater.from(m_Context);  
		mList = list;
		mImageLoader = new ImageLoader(context);
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
	 
	public View getView( final int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
	
		if (convertView == null) {
			
			convertView = layoutInflater.inflate(R.layout.list_photoitem, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.photo_item);
			convertView.setTag(holder);
			
		} else {        	
			holder = (ViewHolder) convertView.getTag();         
		}  
		
		mImageLoader.DisplayImage(mList.get(position).mPhotoUrl, holder.imageView);
		return convertView;         
	}
	
	public String getSaveName(String str)
	{
		str = str.replace("/", "");
		return str;
	}
	 
	static class ViewHolder {
		ImageView 	imageView;
	}    
	    
	  
}
