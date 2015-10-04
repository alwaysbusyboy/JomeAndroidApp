package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMReplyObject {

	public String messageId;
	public String message;
	public String senderId;
	public String senderName;
	public String messageTime;
	public String Latitude;
	public String Longitude;
	
	public  JMReplyObject()
	{

	}
	
	public JMReplyObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				
				messageId = data.getString("msg_id");
				message = data.getString("message");
				senderId = data.getString("sender_id");
				senderName = data.getString("sender_name");
				messageTime = data.getString("msg_time");
				Latitude = data.getString("lati");
				Longitude = data.getString("long");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
