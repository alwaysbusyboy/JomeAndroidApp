package com.nfreespace.app.android.JoMe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMUserMapObject;

public class JMFriendFinderActivity extends Activity implements ServerResponse{

	GoogleMap  			vGoogleMap;
	Button 	   			vBtnMapType,vBtnHome,vBtnProfile,vBtnHistory;
	ProgressDialog  	vProgress;
	
	/// Google admov
	AdView 				adView;
	RelativeLayout 		adLayout;
	
	LatLng 	   	   		mCurrent;
	boolean    	  		mPMapType;
	List<JMUserMapObject> 	mUserList;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_friend_finder);
	    
	    /// google admov
	    adLayout = (RelativeLayout)findViewById(R.id.adlayout);
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.SMART_BANNER);
	    adView.setAdUnitId(constant.gAdUnitId);
	    adLayout.addView(adView);
	  	    
	    AdRequest adRequest = new AdRequest.Builder()
	    	.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    	.build();
	    adView.loadAd(adRequest);
	  	    
		try {
	        // Loading map
			initilizeMap();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		vBtnProfile 	= (Button)findViewById(R.id.btnInvitationManager);
		vBtnHome 		= (Button)findViewById(R.id.btnHome);
		vBtnHistory 	= (Button) findViewById(R.id.btnMessage);
		vBtnMapType 	= (Button)findViewById(R.id.map_type_btn);
		vBtnMapType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(vGoogleMap != null)
				{
					if(mPMapType)
					{
						vGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); 
						mPMapType = false;
					}else
					{
						vGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); 
						mPMapType = true;
					}
				}
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
		
		
		vGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

	        public void onInfoWindowClick(Marker marker) {
	        	//LatLng selectedPos = marker.getPosition();
	    		String[]  arr = marker.getId().split("m");
				int pos = Integer.parseInt(arr[1]);
				if(pos>0)
				{
					String selectUserId = mUserList.get(pos-1).userId;
					Intent intent = new Intent(JMFriendFinderActivity.this, JMUserProfileActivity.class);
					intent.putExtra("selectUserId", selectUserId);
					startActivity(intent);
				}
	        }
	    });
		
		init();
	}
	
	public void init()
	{
		if(IsGetPosition())
		{
			showCurrentPos();
			
			getNearbyListFS();
		}
	}

///------ show current location on Map ---------------------------	
	public void showCurrentPos()
	{
		if(vGoogleMap != null)
    	{
			vGoogleMap.addMarker(new MarkerOptions()
	          .position(mCurrent)
	          .title("Current Position")
	          .snippet("")
	          .icon(BitmapDescriptorFactory
	              .fromResource(R.drawable.current_icon)));
    		
    		 // Move the camera instantly to hamburg with a zoom of 15.
			vGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mCurrent, 8));

    		 // Zoom in, animating the camera.
			vGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(6), 1000, null);
    	}
	}
/// -------------------------------- show nearby user list to map ------------------------	
	public void showNearbyUserList()
	{
		if(mUserList != null)
		{
			for(int i=0;i<mUserList.size();i++)
			{
				LatLng pos = new LatLng(mUserList.get(i).lat,mUserList.get(i).lon);
				showNearbyUser(pos, String.format("%s %s", mUserList.get(i).firstName,mUserList.get(i).lastName),"",mUserList.get(i).type);
			}
		}
	}
	
/// --------------------------------- show position of user with LatLng -----------------------	
	public void showNearbyUser(LatLng pos, String title, String content,int type)
	{
		if(vGoogleMap != null)
		{
			vGoogleMap.addMarker(new MarkerOptions()
	        .position(pos)
	        .title(title)
	        .snippet(content)
	        .icon(BitmapDescriptorFactory
	            .fromResource(constant.getUserIconId(type))));
		}
		
	}
/// ------------------------------------ get current position ------------------------	
    public boolean  IsGetPosition()
    {
    	GPSTracker mGPS = new GPSTracker(this);
        
        if(mGPS.canGetLocation ){
        	mGPS.getLocation();
        	double m_fLat = (double)mGPS.getLatitude();
        	double m_fLon = (double)mGPS.getLongitude();        	
        	mCurrent = new LatLng(m_fLat, m_fLon);
        	return true;
        }
        return false;
    }
/// ------------------------------------------------------	

///	------------------- Get Nearby User List from server ------------------------------------------------------------

    public void getNearbyListFS()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		String user_Id= settings.getString("createUserId", "");
		
		MyAsyncTask task1=new MyAsyncTask(this, String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_users_nearby?user_id=%s&long=%s&lati=%s&distance=%d",
				user_Id, constant.g_CurrentLon,constant.g_CurrentLan,constant.g_Radius));
		task1.execute();
		
		showProgress();
	}
 /// ----------------------------- show progress while get response --------------------   
    public void showProgress()
	{
		vProgress = new ProgressDialog(JMFriendFinderActivity.this);
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
///------------------------------------------------ parser response with JSONObject ----------------------- 	
	public void parserResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String success = data.getString("error");
				if(success.equals("0"))
				{
					JSONArray userArr = data.getJSONArray("data");
					
					if(userArr != null && userArr.length()>0)
					{
						mUserList = new ArrayList<JMUserMapObject>();
						for(int i=0;i<userArr.length();i++)
				        {
							JSONObject c = userArr.getJSONObject(i);
							JMUserMapObject item = new JMUserMapObject(c);
							item.type = i%4;
							mUserList.add(item);
					    }
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		showNearbyUserList();
	}

/// ----   Google map init ---------------------------------

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initilizeMap() {
        if (vGoogleMap == null) {
        	vGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map_friend_finder)).getMap();
            
            // check if map is created successfully or not
            if (vGoogleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
 
	@Override
    protected void onResume() {
        super.onResume();
        if(adView != null)
			adView.resume();
        initilizeMap();
    }

	@Override
	public void onPause()
	{
		if(adView != null)
			adView.pause();
		super.onPause();
	}
/// --------------------  when get response,  AsyncTask call this function ---------------------------------------

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parserResponse(data);
		hideProgress();
	}
	
}
