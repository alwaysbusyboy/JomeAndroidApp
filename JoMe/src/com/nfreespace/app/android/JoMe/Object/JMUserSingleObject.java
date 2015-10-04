package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMUserSingleObject {

	public String userId;
	public String firstname;
	public String lastname;
	public String imageUrl;
	public String gender;
	
	public JMUserSingleObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				userId = data.getString("user_id");
				firstname = data.getString("firstname");
				lastname = data.getString("lastname");
				imageUrl = data.getString("profile_image");
				gender = data.getString("gender");
				
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
