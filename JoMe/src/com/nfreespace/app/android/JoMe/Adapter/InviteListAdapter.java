package com.nfreespace.app.android.JoMe.Adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.Image.ImageLoader;
import com.nfreespace.app.android.JoMe.Object.GetCategory1;

public class InviteListAdapter extends BaseAdapter {
	private Context 		m_Context;
	private LayoutInflater 	layoutInflater;  
	private SparseBooleanArray mSparseBooleanArray;
	private ImageLoader imageLoader;
	
	private ArrayList<GetCategory1> mlist;
	
	public InviteListAdapter(Context context,ArrayList<GetCategory1> list) {
	           	 	
		m_Context=context;
		layoutInflater = LayoutInflater.from(m_Context);  
		imageLoader = new ImageLoader(context);
	    mSparseBooleanArray = new SparseBooleanArray();
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
			
			convertView = layoutInflater.inflate(R.layout.galleryitem, null);
			holder = new ViewHolder();
			holder.checkBox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);       
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView1);
			convertView.setTag(holder);
			
		} else {        	
			holder = (ViewHolder) convertView.getTag();         
		}  
		
		holder.checkBox.setTag(position);
		holder.checkBox.setChecked(mSparseBooleanArray.get(position));
		holder.checkBox.setOnCheckedChangeListener(mCheckedChangeListener);
	    	   	 
		String imgurl = mlist.get(position).Profile_pic;
	    String id= mlist.get(position).userId;

	    holder.imageView.setTag(id);
		imageLoader.DisplayImage(imgurl,holder.imageView);
		
		holder.imageView.requestLayout();
		int density = m_Context.getResources().getDisplayMetrics().densityDpi;
		switch (density) {
			case DisplayMetrics.DENSITY_LOW:
				holder.imageView.getLayoutParams().height = 40;
				holder.imageView.getLayoutParams().width = 40;
			 
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				holder.imageView.getLayoutParams().height = 85;
				holder.imageView.getLayoutParams().width = 85;
			 
				break;
			case DisplayMetrics.DENSITY_HIGH:
				holder.imageView.getLayoutParams().height = 85;
				holder.imageView.getLayoutParams().width = 85;
			
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				holder.imageView.getLayoutParams().height = 120;
				holder.imageView.getLayoutParams().width = 120;
			 
				break;
		}
		
		holder.id = position;
		 
		return convertView;         
	}
	
	OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
    	 
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        	 mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
         }
	};
     
	public ArrayList<String> getCheckedItems() {
		ArrayList<String> mTempArry = new ArrayList<String>();

		for(int i=0;i<mlist.size();i++) {
			if(mSparseBooleanArray.get(i)) {
				mTempArry.add(mlist.get(i).userId);
			}
		}

		return mTempArry;
	}
	 
	static class ViewHolder {
		CheckBox 	checkBox;      
		ImageView 	imageView;
		int id;
	}    
	    
	  
}
