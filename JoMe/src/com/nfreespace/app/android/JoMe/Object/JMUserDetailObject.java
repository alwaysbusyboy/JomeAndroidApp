package com.nfreespace.app.android.JoMe.Object;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JMUserDetailObject {

	public String userId;
	public String firstname;
	public String lastname;
	public String nickname;
	public String email;
	public String interest;
	public String picUrl;
	public String gender;
	public String birthday;
	public String about;
	public String location;
	public String movieLike;
	public String starLike;
	public String relationShip;
	public String company;
	
	public List<JMPhotoObject> photolist = new ArrayList<JMPhotoObject>();
	public List<JMFollowUserObject> followedList = new ArrayList<JMFollowUserObject>();
	public List<JMFollowUserObject> myFollowingList = new ArrayList<JMFollowUserObject>();
	
	public JMUserDetailObject(JSONObject data)
	{
		try {
			JSONObject obj = data.getJSONObject("profile");
			if(obj != null)
			{
				userId = obj.getString("user_id");
				firstname = obj.getString("firstname");
				lastname = obj.getString("lastname");
				nickname = obj.getString("nickname");
				email = obj.getString("email");
				interest = obj.getString("interest");
				picUrl = obj.getString("profile_image");
				birthday = obj.getString("birthday");
				about = obj.getString("about");
				location = obj.getString("hometown");
				gender = obj.getString("gender");
				
				if(gender.equals("1"))
					gender = "Male";
				else
					gender = "Female";
			}	
			
			JSONArray  photoArr = data.getJSONArray("photo_list");
			if(photoArr != null)
			{
				for(int i=0;i<photoArr.length();i++)
				{
					JSONObject c = photoArr.getJSONObject(i);
					JMPhotoObject photo = new JMPhotoObject(c);
					photolist.add(photo);
				}
			}
			
			JSONArray otherArr = data.getJSONArray("attr_list");
			
			if(otherArr != null)
			{
				for(int i=0;i<otherArr.length();i++)
				{
					JSONObject c = otherArr.getJSONObject(i);
					String key  = c.getString("key");
					String val  = c.getString("val");
					if(key.equals("movie_like"))
						movieLike = val;
					else if(key.equals("star_like"))
						starLike = val;
					else if(key.equals("relationship"))
						relationShip = val;
					else if(key.equals("company"))
						company = val;
				}
			}
			
			JSONArray followerArr = data.getJSONArray("follower_list");
			if(followerArr != null)
			{
				for(int i=0;i<followerArr.length();i++)
				{
					JSONObject c = followerArr.getJSONObject(i);
					JMFollowUserObject followUser = new JMFollowUserObject(c);
					followedList.add(followUser);
				}
			}
			
			JSONArray followingArr = data.getJSONArray("following_list");
			if(followingArr != null)
			{
				for(int i=0;i<followingArr.length();i++)
				{
					JSONObject c = followingArr.getJSONObject(i);
					JMFollowUserObject followUser = new JMFollowUserObject(c);
					myFollowingList.add(followUser);
				}
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
