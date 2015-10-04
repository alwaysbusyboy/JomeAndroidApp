package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMPeerObject {

	public String userId;
	public String firstName;
	public String lastName;
	public String nickName;
	public String imageUrl;
	public String gender;
	
	public JMPeerObject()
	{
		
	}
	
	public JMPeerObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				userId = data.getString("peer_id");
				firstName = data.getString("firstname");
				lastName = data.getString("lastname");
				nickName = data.getString("nickname");
				imageUrl = data.getString("profile_image");
				gender = data.getString("gender");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
