package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMFollowUserObject {

	public String userId;
	public String firstName;
	public String lastName;
	public String nickName;
	public String gender;
	public String imageUrl;
	
	public JMFollowUserObject()
	{
		
	}
	
	public JMFollowUserObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				userId = data.getString("user_id");
				firstName = data.getString("fname");
				lastName = data.getString("lname");
				nickName = data.getString("nickname");
				gender = data.getString("gender");
				imageUrl = data.getString("profile_pic");
				
				if(gender.equals("1"))
					gender = "Male";
				else
					gender = "Female";
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
