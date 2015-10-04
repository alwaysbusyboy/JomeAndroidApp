package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMPhotoObject {

	public String   mPhotoId;
	public String   mPhotoUrl;
	
	public JMPhotoObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				mPhotoId = data.getString("photo_id");
				mPhotoUrl = data.getString("photo_url");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
