package com.nfreespace.app.android.JoMe.Object;

import android.graphics.Bitmap;

public class JMCacheObject {

	public String index;
	public Bitmap bitmap;
	
	public JMCacheObject(String pId, Bitmap bmp)
	{
		index = pId;
		bitmap = bmp;
	}
}
