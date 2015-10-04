package com.nfreespace.app.android.JoMe.Adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.nfreespace.app.android.JoMe.R;
import com.nfreespace.app.android.JoMe.constant;
import com.nfreespace.app.android.JoMe.Object.JMCacheObject;
import com.nfreespace.app.android.JoMe.Object.JMPhotoObject;

public class JMPhotoFullAdapter extends PagerAdapter {
	
	Context     context;
	ProgressBar vProgress;
	List<JMPhotoObject> photoList = new ArrayList<JMPhotoObject>();
	
	public JMPhotoFullAdapter(Context context,ProgressBar progress,List<JMPhotoObject> list) {
		this.context = context;
		photoList = list;
		vProgress = progress;
		
		/// for photo download
		constant.gCacheList = new ArrayList<JMCacheObject>();
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((ImageView) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imageView = new ImageView(context);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setTag(position);
		
		String url = photoList.get(position).mPhotoUrl;
		if (url != null	&& !url.equals("")) 
		{
			Bitmap  bitmap = getBitmap(url);
			if(bitmap != null)
			{
				imageView.setImageBitmap(bitmap);
			}else
			{
				try{
					vProgress.setVisibility(View.VISIBLE);
	    			new ImageDownloaderTask1(imageView,vProgress,context,url,position).execute(url);
	    		}catch(Exception e)
	    		{
	    					
	    		}
			}

		} else {
			imageView.setImageResource(R.drawable.splash_bg1);
		}

		((ViewPager) container).addView(imageView, 0);
		
		return imageView;
	}
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((ImageView) object);
	}
	
	
	public Bitmap getBitmap(String index)
	{
		Bitmap bitmap = null;
		for(int i=0;i<constant.gCacheList.size();i++)
		{
			if(index.equals(constant.gCacheList.get(i).index))
			{
				bitmap = constant.gCacheList.get(i).bitmap;
				i = constant.gCacheList.size();
			}
		}
		
		return bitmap;
	}
}
