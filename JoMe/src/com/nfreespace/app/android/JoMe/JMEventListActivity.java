package com.nfreespace.app.android.JoMe;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nfreespace.app.android.JoMe.Adapter.EventListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.GetCategory;

public class JMEventListActivity extends Activity implements ServerResponse {
	
	ListView 		vEventList;
	EditText 		vEdtsearch ;
	Button   		vBtnSearch,vBtnOnOff,vMapBtn,vBtnHome,vBtnProfile,vBtnHistory;
	ProgressDialog 	vProgress;
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
		
	int 					mOnStatus = 0;
	ArrayList<GetCategory> 	mEventlist = new ArrayList<GetCategory>();

	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_myevent);
	    
	    vBtnProfile = (Button)findViewById(R.id.btnInvitationManager);
	    vBtnOnOff 	= (Button) findViewById(R.id.button_on);
		vBtnHome 	= (Button) findViewById(R.id.btnHome);
		vBtnSearch 	= (Button) findViewById(R.id.search_btn);
		vMapBtn 	= (Button) findViewById(R.id.btnmap);
		vBtnHistory = (Button) findViewById(R.id.btnMessage);
		vEventList 	= (ListView) findViewById(R.id.nearbylist);
	    vEdtsearch 	= (EditText) findViewById(R.id.eventsearch);
		vEdtsearch.setVisibility(View.GONE);
		
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
	    
	    vBtnSearch.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
       			
	    		if(!vEdtsearch.isShown()) {
	    			vEdtsearch.setVisibility(View.VISIBLE);;
       			} else {
       				vEdtsearch.setVisibility(View.GONE);
       			}
       	  	}
        });
	    
	    vBtnOnOff.setOnClickListener(new View.OnClickListener() {
	       public void onClick(View v) {

	           if (mOnStatus == 0) {
	        	   vBtnOnOff.setBackgroundResource(R.drawable.off_button);
	        	   mOnStatus=1 ; // change the status to 1 so the at the second click , the else will be executed
	           }   

	           else {  
	        	   vBtnOnOff.setBackgroundResource(R.drawable.on_button);
	        	   mOnStatus =0;//change the status to 0 so the at the second click , the if will be executed
	           }
	       }
	    });
	    
	    vMapBtn.setOnClickListener(new View.OnClickListener() {
			
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					constant.gEventlist = mEventlist;
					
					Intent allevents = new Intent(getApplicationContext(),JMEventMapActivity.class);
					startActivity(allevents);
				}
	    });
	    
	    vBtnHome.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMMainActivity.class);
				startActivity(create);
			}
		});
	    
	    vBtnProfile.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMUserProfileActivity.class);
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
	    
	    vEventList.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		
        		
        		Intent i = new Intent(getApplicationContext(), JMEventDetailActivity.class);
        		i.putExtra("eventId", mEventlist.get(position).event_id);
        		startActivity(i);
		
        	}
        });
	    
	    getEventList();
	   
	}
	
//	------------------------  when app get JSONObject,  parser response  -------------------------------------------------------
	
	public void parseResponse(JSONObject data)
	{
		
		ArrayList<GetCategory> 	eventlist = new ArrayList<GetCategory>();
		if(data != null)
		{
			try {
				
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONArray arr = data.getJSONArray("data");
					
					for(int i=0;i<arr.length();i++) {
						
						JSONObject c = arr.getJSONObject(i);
						GetCategory values = new GetCategory();
						values.event_id = c.getString("event_id"); 
						values.user_id = c.getString("user_id");
						values.event_start_datetime = c.getString("event_start");
						values.event_end_datetime = c.getString("event_end");
						values.latitude = c.getString("lati");
						values.longitude = c.getString("long");
						values.description = c.getString("description");
						values.event_name = c.getString("event_name");
						values.frds_invited = c.getString("frds_invited");
						values.joined = c.getString("joined");
						values.joined_male = c.getString("joined_male");
						values.joined_female = c.getString("joined_female");
						
						try{
							values.distance= c.getString("distance");
						}catch(JSONException e)
						{
							
						}
						
						try{
							values.merchant= c.getString("merchant");
						}catch(JSONException e)
						{
							
						}
						
						values.type = i%9;
						eventlist.add(values);
	                              
					}
				}else
				{
					Toast.makeText(this, "App can not get data from server", Toast.LENGTH_LONG).show();
				}
							
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			sortEventList(eventlist);
		}
	}
/// ----------------------------------- sort event list -----------------------------------------
	public void sortEventList(ArrayList<GetCategory> list)
	{
		mEventlist = new ArrayList<GetCategory>();
		
		// add event that is created by this user
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).user_id.equals(constant.gUserId))
			{
				mEventlist.add(list.get(i));
			}
		}
		
		// add event that is joined by this user
		for(int i=0;i<list.size();i++)
		{
			if(list.get(i).joined.equals("1"))
			{
				mEventlist.add(list.get(i));
			}
		}
		
		// add remain event
		for(int i=0;i<list.size();i++)
		{
			if(!list.get(i).user_id.equals(constant.gUserId) && list.get(i).joined.equals("0"))
			{
				mEventlist.add(list.get(i));
			}
		}
		
		vEventList.setAdapter(new EventListAdapter(this, mEventlist));
	}

/// ----------------------------------- get event list from server -----------------------------------	
	public void getEventList()
	{
		GetPosition();
		
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		String latStr= settings.getString("currentlat", "");
		String lngStr= settings.getString("currentlng", "");
					
		String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_all_event?user_id=%s&lati=%s&long=%s",constant.gUserId,latStr,lngStr);
	
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		showProgress();
	}

/// --------------------------------- show progress while get response from server -----------------------	
	public void showProgress()
	{
		vProgress = new ProgressDialog(JMEventListActivity.this);
		vProgress.setMessage("Loading....please wait ");
		vProgress.setCancelable(true);
		vProgress.show();
	}
	
	public void hideProgress()
	{
		if(vProgress != null)
		{
			vProgress.dismiss();
		}
	}

/// ---------------------------------------------------------------
	
	public boolean  GetPosition()
	{
		GPSTracker mGPS = new GPSTracker(this);
	        
		if(mGPS.canGetLocation ){
			mGPS.getLocation();
			double m_fLat = (double)mGPS.getLatitude();
			double m_fLon = (double)mGPS.getLongitude();
			
			SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString("currentlat",String.format("%f", m_fLat));
		    editor.putString("currentlng",String.format("%f", m_fLon)); 
		    editor.commit();
		    
			return true;
		}
	    return false;
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(adView != null)
			adView.resume();
	}
	
	@Override
	public void onPause()
	{
		if(adView != null)
			adView.pause();
		super.onPause();
	}

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parseResponse(data);
		hideProgress();
	}
}
