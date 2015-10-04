package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMUserMapObject {

	public String userId;
	public String firstName;
	public String lastName;
	public String email;
	public String interest;
	public String distance;
	public String imageUrl;
	public double lat;
	public double lon;
	public int    type;
	
	
	public JMUserMapObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				userId = data.getString("user_id");
				firstName = data.getString("firstname");
				lastName = data.getString("lastname");
				email = data.getString("email");
				interest = data.getString("interest");
				distance = data.getString("distance");
				imageUrl = data.getString("profile_image");
				lat = data.getDouble("lati");
				lon = data.getDouble("long");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
