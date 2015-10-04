package com.nfreespace.app.android.JoMe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.ui.HorizontalListView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nfreespace.app.android.JoMe.Adapter.JMPhotoListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMPhotoObject;
import com.nfreespace.app.android.JoMe.Object.JMUserDetailObject;

public class JMUserProfileActivity extends Activity implements ServerResponse{

	Button   			vBtnBack,vBtnHome,vBtnUpdate,vBtnCamera,vBtnChat,vBtnHistory;
	EditText 			vNickname,vNewLocation, vInterest, vAboutme, vMovieLike, vStarLike,vRelationship, vCompany;
	TextView  			vUsername,vUserage, vOldLocation,vBirthday,vPhotoEmpty;
	ImageView  			vTouchImg,vUserImg;
	HorizontalListView 	vGridPhotos;
	
	/// Google admov
	AdView 				adView;
	RelativeLayout 		adLayout;
		
	JMUserDetailObject  mUser = null;
	String              mUserId = null;
	boolean             mIsUpdate = false;
	boolean             mIsFollow = false;
	boolean             mIsUnfollow = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);
		
		vBtnBack 		= (Button) findViewById(R.id.btnCrose);
		vBtnHome 		= (Button) findViewById(R.id.btnHome);
		vBtnUpdate 		= (Button) findViewById(R.id.update_btn);
		vBtnCamera      = (Button) findViewById(R.id.btnCamera);
		vBtnChat		= (Button) findViewById(R.id.btnChat);
		vBtnHistory 	= (Button) findViewById(R.id.btnMessage);
		vNickname 		= (EditText) findViewById(R.id.nickname);
		vNewLocation 	= (EditText) findViewById(R.id.location);
		vInterest 		= (EditText) findViewById(R.id.interest);
		vAboutme 		= (EditText) findViewById(R.id.about);
		vMovieLike 		= (EditText) findViewById(R.id.movie);
		vStarLike 		= (EditText) findViewById(R.id.star);
		vRelationship 	= (EditText) findViewById(R.id.relationship);
		vCompany 		= (EditText) findViewById(R.id.company);
		
		vUsername 		= (TextView) findViewById(R.id.user_name);
		vUserage 		= (TextView) findViewById(R.id.user_age);
		vOldLocation 	= (TextView) findViewById(R.id.user_location);
		vBirthday 		= (TextView) findViewById(R.id.birthday);
		vPhotoEmpty     = (TextView) findViewById(R.id.photo_empty);
		
		vUserImg 		= (ImageView) findViewById(R.id.user_image);
		vTouchImg 		= (ImageView) findViewById(R.id.touch_image);
		vGridPhotos 	= (HorizontalListView) findViewById(R.id.photo_list);
		
		/// google admov
		adLayout    = (RelativeLayout)findViewById(R.id.adlayout);
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(constant.gAdUnitId);
		adLayout.addView(adView);
		
		AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.build();
		adView.loadAd(adRequest);
			    
		init();
		vBtnHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMMainActivity.class);
				startActivity(create);
			}
		});
		
		vBtnHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMChatHistoryActivity.class);
				startActivity(create);
			}
		});
		
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vBtnChat.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mUserId.equals(constant.gUserId))
				{
					goChatActivity();
				}
			}
		});
		
		vBtnUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mUserId.equals(constant.gUserId))
				{
					updateUserData();
				}else
				{
					if(vBtnUpdate.getText().equals("Follow"))
					{
						followUser();
					}else
					{
						unFollowUser();
					}
				}
			}
		});
		
		vBtnCamera.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mUserId.equals(constant.gUserId))
				{
					Intent create = new Intent(getApplicationContext(),JMPhotoUploadActivity.class);
					create.putExtra("user_id", mUserId);
					startActivity(create);
				}
			}
		});
		
		/// ------------------- date picker about start time ------------- 
		final CustomDateTimePicker custom = new CustomDateTimePicker(JMUserProfileActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() { 
			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) {
					    
				vBirthday.setText(year+ "-" + (monthNumber+1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH));
			}
			
			@Override
			public void onCancel() {
				
			}
		});
					 
		custom.set24HourFormat(false);
		custom.setDate(Calendar.getInstance());
		
		vBirthday.setOnClickListener(new OnClickListener() {
											
			@Override
			public void onClick(View v) {
				custom.showDialog();
			}
		});
		
		/// for horizontal photo list
		final ScrollView mainScrollView = (ScrollView) findViewById(R.id.scrollView2);
		vTouchImg.setOnTouchListener(new View.OnTouchListener() {

		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        int action = event.getAction();
		        switch (action) {
		           case MotionEvent.ACTION_DOWN:
		                // Disallow ScrollView to intercept touch events.
		        	   mainScrollView.requestDisallowInterceptTouchEvent(true);
		                // Disable touch on transparent view
		                return false;

		           case MotionEvent.ACTION_UP:
		                // Allow ScrollView to intercept touch events.
		        	   mainScrollView.requestDisallowInterceptTouchEvent(false);
		                return true;

		           case MotionEvent.ACTION_MOVE:
		        	   mainScrollView.requestDisallowInterceptTouchEvent(true);
		                return false;

		           default: 
		                return true;
		        }   
		    }
		});
		
		vGridPhotos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				constant.gPhotoList = mUser.photolist;
				Intent create = new Intent(getApplicationContext(),JMPhotoShowActivity.class);
				create.putExtra("selectpos", String.format("%d", position));
				create.putExtra("userid", mUser.userId);
				startActivity(create);
				finish();
			}
		});
	}
	
	public void init()
	{
		Intent intent = getIntent();
		mUserId = intent.getStringExtra("selectUserId");
	
		if(mUserId == null)
		{
			mUserId = constant.gUserId;
		}

		getUserdataFS(mUserId);
	}
	
	public void goChatActivity()
	{
		Intent intent = new Intent(JMUserProfileActivity.this, JMChatActivity.class);
		intent.putExtra("receiveUserId", mUserId);
		startActivity(intent);
	}
	
/// ----------------------------- show photos to list ------------------------------	
	public void showPhotos(List<JMPhotoObject> list)
	{
		if(list != null && list.size()>0)
		{
			vPhotoEmpty.setVisibility(View.GONE);
			vGridPhotos.setAdapter(new JMPhotoListAdapter(this,list));
		}else
		{
			vPhotoEmpty.setVisibility(View.VISIBLE);
		}
	}
/// ------------------------------- get user data from server -----------------------
	
	public void getUserdataFS(String userId)
	{
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_user_data?user_id=%s",
				userId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
/// -------------------------------------- follow user ------------------------------------
	public void followUser()
	{
		mIsFollow = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/do_follow?user_id=%s&guest_id=%s",
				constant.gUserId,mUser.userId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
	public void unFollowUser()
	{
		mIsUnfollow = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/undo_follow?user_id=%s&guest_id=%s",
				constant.gUserId,mUser.userId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
/// -------------------------------- upload updated user data to server ---------------------
	
	public void updateUserData()
	{
		String birthday = "",
			   nickname = "",
			   interest = "",
			   about = "",
			   hometown = "",
			   movielike = "",
			   starLike = "",
			   relationship = "",
			   company = "";
		try {
			 birthday 		= URLEncoder.encode(vBirthday.getText().toString(),"utf-8");
			 nickname 		= URLEncoder.encode(vNickname.getText().toString(),"utf-8");
			 interest 		= URLEncoder.encode(vInterest.getText().toString(),"utf-8");
			 about    		= URLEncoder.encode(vAboutme.getText().toString(),"utf-8");
			 hometown 		= URLEncoder.encode(vNewLocation.getText().toString(),"utf-8");
			 movielike 		= URLEncoder.encode(vMovieLike.getText().toString(),"utf-8");
			 starLike  		= URLEncoder.encode(vStarLike.getText().toString(),"utf-8");
			 relationship 	= URLEncoder.encode(vRelationship.getText().toString(),"utf-8");
			 company 		= URLEncoder.encode(vCompany.getText().toString(),"utf-8");
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(birthday.equals("") || nickname.equals("") || hometown.equals("") || about.equals("") || interest.equals(""))
		{
			constant.alertbox("Warning!", "Please fill birthday, nickname, location, Aboutme, interest",  this);
		}else
		{
			mIsUpdate = true;
			String att = "[{\"key\":\"movie_like\", \"val\":\""+movielike+"\"},{\"key\":\"star_like\", \"val\":\""+starLike+"\"},{\"key\":\"relationship\", \"val\":\""+relationship+"\"},{\"key\":\"company\", \"val\":\""+company+"\"}]";
			
			String baseURL = "http://freelancer.nfreespace.com/event_proj/index.php/api/set_profile";
			
			constant.nameValuePairs = new ArrayList<NameValuePair>();
			constant.nameValuePairs.add(new BasicNameValuePair("user_id",mUserId));
			constant.nameValuePairs.add(new BasicNameValuePair("birthday",birthday));
			constant.nameValuePairs.add(new BasicNameValuePair("nickname",nickname));
			constant.nameValuePairs.add(new BasicNameValuePair("interest",interest));
			constant.nameValuePairs.add(new BasicNameValuePair("about",about));
			constant.nameValuePairs.add(new BasicNameValuePair("hometown",hometown));
			constant.nameValuePairs.add(new BasicNameValuePair("attr",att));
			
			MyAsyncTask1 task1=new MyAsyncTask1(baseURL);
			task1.execute();
			constant.showProgress(this);
		}
	}

/// ----------------------------------- parse json object for user ------------------
	public void parserResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONObject object = data.getJSONObject("data");
					mUser = new JMUserDetailObject(object);
					
				}else
				{
					Toast.makeText(this, "You can not get user data from server.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Toast.makeText(this, "You can not get user data from server.", Toast.LENGTH_LONG).show();
		
		showUser();
	}
	
	public void ParseFollow(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					Toast.makeText(this, "Followed", Toast.LENGTH_LONG).show();
					vBtnUpdate.setText("Unfollow");
				}else
				{
					Toast.makeText(this, "You can not follow this user.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Toast.makeText(this, "You can not follow this user.", Toast.LENGTH_LONG).show();
		
	}
	
	public void ParseUnFollow(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					Toast.makeText(this, "Unfollowed", Toast.LENGTH_LONG).show();
					vBtnUpdate.setText("Follow");
				}else
				{
					Toast.makeText(this, "You can not Unfollow this user.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Toast.makeText(this, "You can not Unfollow this user.", Toast.LENGTH_LONG).show();
		
	}
	
/// ----------------------------------------------- response when update user profile page -------------------------	
	public void parseUpdateResponse(JSONObject data)
	{
		constant.hideProgress();
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					Toast.makeText(this, "Updated successfully.", Toast.LENGTH_LONG).show();
				}else
				{
					Toast.makeText(this, "Update fail.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
/// ---------------------------- show user data --------------------------------------
	public void showUser()
	{
		if(mUser != null)
		{
			showPhotos(mUser.photolist);
			showUserImage();
			showUserDetail();
			showUpdateButton();
		}
	}
	
/// ---------------------------------- change update button name according other user data	
	public void showUpdateButton()
	{
		boolean flag = false;
		if(mUser.followedList != null)
		{
			for(int i=0;i<mUser.followedList.size();i++)
			{
				if(mUser.followedList.get(i).userId.equals(constant.gUserId))
				{
					flag = true;
				}
			}
		}
		
		if(!mUser.userId.equals(constant.gUserId))
		{
			if(flag)
			{
				vBtnUpdate.setText("Unfollow");
			}else
			{
				vBtnUpdate.setText("Follow");
			}
		}
	}
	
/// ---------------------------- show user image -----------------------------
	public void showUserImage()
	{
		new ImageDownloaderTask(vUserImg,this,"").execute(mUser.picUrl);
	}
	
/// ----------------------------- show user name, gender, age,location ----------------
	public void showUserDetail()
	{
		vUsername.setText(String.format("%s %s", mUser.firstname,mUser.lastname));
		vUserage.setText(String.format("%s, %d years old", mUser.gender,getAge(mUser.birthday)));
		vOldLocation.setText(mUser.location);
		vNewLocation.setText(mUser.location);
		vNickname.setText(mUser.nickname);
		if(!mUser.birthday.equals("0000-00-00"))
			vBirthday.setText(mUser.birthday);
		vInterest.setText(mUser.interest);
		vAboutme.setText(mUser.about);
		vMovieLike.setText(mUser.movieLike);
		vStarLike.setText(mUser.starLike);
		vRelationship.setText(mUser.relationShip);
		vCompany.setText(mUser.company);
	}
	
	public int getAge(String birthday)
	{
		int age = 0;
		Calendar cal = Calendar.getInstance();     
		int currentYear  = cal.get(Calendar.YEAR);
		
		String[] arr = birthday.split("-");
		int birthyear = Integer.parseInt(arr[0]);
		
		if(birthyear == 0)
			age = 0;
		else
			age = currentYear - birthyear;
		return age;
	}
/// ---------------------------------- server  async task --------------------------------
	public class MyAsyncTask1 extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
		
		/// server communicate using asyncTask
		
		ArrayList<HashMap<String, String>> UploadsList = new ArrayList<HashMap<String, String>>();
		JSONObject 		mResponseData 	= null;
		String     		mBaseURL 	 	= "";
		
		public MyAsyncTask1(String url)
		{
			mBaseURL = url;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {

			// Creating JSON Parser instance
			JSONPostParser jParser = new JSONPostParser();
			// getting JSON string from URL
			mResponseData = jParser.getJSONFromUrl(mBaseURL);  
			
	 		return UploadsList;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
	  
			parseUpdateResponse(mResponseData);
			
			super.onPostExecute(result);
		}
	}
	
	/////tick function call section
	CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 1000) {	   
		 public void onTick(long millisUntilFinished) {
		           
			 	if(constant.gIsUploaded)
			 	{
			 		constant.gIsUploaded = false;
			 		getUserdataFS(mUserId);
			 	}
	        }  
	        public void onFinish() {
	            Log.d("test","Timer last tick");     
	        }
		         
	}.start();
	

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		if(mIsUnfollow)
		{
			mIsUnfollow = false;
			ParseUnFollow(data);
		}else if(mIsFollow)
		{
			mIsFollow = false;
			ParseFollow(data);			
		}else
		{
			parserResponse(data);	
		}
		constant.hideProgress();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(adView != null)
			adView.resume();
		t.start();
	}
		
	@Override
	public void onPause()
	{
		if(adView != null)
			adView.pause();
		
		t.cancel();
		super.onPause();
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
}
