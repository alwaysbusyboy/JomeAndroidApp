package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;

public class JMChatObject {

	public String message;
	public String sendId;
	public String sendName;
	public String receiveId;
	public String receiveName;
	public String sendTime;
	public String Latitude;
	public String Longitude;
	public String isShort;
	
	public JMChatObject()
	{
		
	}
	
	public JMChatObject(JSONObject data)
	{
		if(data != null)
		{
			try {
				message = data.getString("message");
				sendId = data.getString("sender_id");
				receiveId = data.getString("recver_id");
				sendName = data.getString("sender_name");
				receiveName = data.getString("recver_name");
				sendTime = data.getString("chat_time");
				Longitude = data.getString("long");
				Latitude = data.getString("lati");
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
