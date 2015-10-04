package com.nfreespace.app.android.JoMe;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;

public class JMMainActivity extends Activity implements ServerResponse{
	
	ImageButton 	vCreateevent,vFetchevent,vFriendFinder,vSetting;
	LocationManager mLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_screen);
		
		vCreateevent 	= (ImageButton) findViewById(R.id.createevent_btn);
		vFetchevent 	= (ImageButton) findViewById(R.id.fetchevent_btn);
		vFriendFinder 	= (ImageButton) findViewById(R.id.friend_finder_btn);
		vSetting		= (ImageButton) findViewById(R.id.setting_btn);

		init();
	  
		vCreateevent.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMJoinActivity.class);
				startActivity(create);
				
			}
		});
		
		vFetchevent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent allevents = new Intent(getApplicationContext(),JMEventListActivity.class);
				startActivity(allevents);
			}
		});
		
		vFriendFinder.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMFriendFinderActivity.class);
				startActivity(create);
			}
		});
		
		vSetting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMSettingActivity.class);
				startActivity(create);
			}
		});
		
	}
	
	public void init()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		constant.gUserId= settings.getString("createUserId", "");
		String  firstName = settings.getString("userFirstName", "");
		String  lastName = settings.getString("userLastName", "");
		constant.gUserName = String.format("%s %s", firstName,lastName);
		
		registerPosFS();
		t.start();
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}

/// ------------- timer ----------------

	/////tick function call section
	CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 1000) {	   
		public void onTick(long millisUntilFinished) {
			
			constant.g_timer ++;
			if(constant.g_timer == 120)
			{
				constant.g_timer = 0;
				registerPosFS();
			}
		}  
		public void onFinish() {
			Log.d("test","Timer last tick");     
		}
		         
	}.start();
	
///	--------------------  save current position to server  -----------------------------------------------------------
	
	public void registerPosFS()
	{
		if(getCurrentPos())
		{
			String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/set_location_user?user_id=%s&long=%s&lati=%s",
					constant.gUserId, constant.g_CurrentLon,constant.g_CurrentLan);
			
			MyAsyncTask task1=new MyAsyncTask(this,baseURL);
			task1.execute();
		}
	}
	
/// ------------- get current position ----------------------
	
	public boolean getCurrentPos()
	{
		GPSTracker mGPS = new GPSTracker(this);
        
		if(mGPS.canGetLocation ){
			mGPS.getLocation();
			Double m_fLat = (double)mGPS.getLatitude();
			Double m_fLon = (double)mGPS.getLongitude();
			
			constant.g_CurrentLan = m_fLat.toString();
			constant.g_CurrentLon = m_fLon.toString();
			
			return true;
		}
	    return false;
	}
	
	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		Log.e("position", "register");
	}
	
}
