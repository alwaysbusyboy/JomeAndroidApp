package com.nfreespace.app.android.JoMe.Object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JMQuestionObject {

	
	public String messageId;
	public String message;
	public String senderId;
	public String senderName;
	public String messageTime;
	public String Latitude;
	public String Longitude;

	public List<JMReplyObject> replyList = new ArrayList<JMReplyObject>();
	
	public JMQuestionObject()
	{
		
	}
	
	public JMQuestionObject(JSONObject data)
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
				
				JSONArray arr = data.getJSONArray("reply_list");
				replyList = new ArrayList<JMReplyObject>();
				if(arr != null)
				{
					for(int i=0;i<arr.length();i++)
					{
						JSONObject obj = arr.getJSONObject(i);
						JMReplyObject replyObj = new JMReplyObject(obj);
						replyList.add(replyObj);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
