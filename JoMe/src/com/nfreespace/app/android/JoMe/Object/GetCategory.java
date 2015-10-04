package com.nfreespace.app.android.JoMe.Object;

import org.json.JSONException;
import org.json.JSONObject;



public class GetCategory {


	public String event_id;
	public String user_id;
	public String venue;
    public String event_start_datetime;
	public String event_end_datetime;
	public String latitude;
	public String longitude;
	public String description;
	public String event_name;
	public String fbid ;
	public String distance ;
	public String frds_invited;
	public String joined;
	public String merchant ;
	public String joined_male;
	public String joined_female;
	public int type;
	public int leaveNum;
	public int replyNum;
	public String firstname;
	
	public GetCategory()
	{
		
	}

	public GetCategory(JSONObject data)
	{
		if(data != null)
		{
			try {
				event_id = data.getString("event_id");
				user_id = data.getString("user_id");
				frds_invited = data.getString("frds_invited");
				event_start_datetime = data.getString("event_start");
				event_end_datetime = data.getString("event_end");
				latitude = data.getString("lati");
				longitude = data.getString("long");
				description = data.getString("description");
				event_name = data.getString("event_name");
				distance = data.getString("distance");
				merchant = data.getString("merchant");
				joined = data.getString("joined");
				joined_male = data.getString("joined_male");
				joined_female = data.getString("joined_female");
				firstname = data.getString("firstname");
				leaveNum = data.getInt("leave_msgs");
				replyNum = data.getInt("reply_msgs");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
	


