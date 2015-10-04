package com.nfreespace.app.android.JoMe;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.GetCategory;

public class JMEventDetailActivity extends Activity implements ServerResponse{
	
	TextView 		vEventName,vEventUsername,vEventDesc,vEventDistance,vEventTime ,vJoinNum,vJoinState,vMessageNum;
	Button    		vMapTypeBtn,vBtnHome,vBtnProfile,vBtnModify,vBtnDelete,vBtnJoin,vBtnHistory;
	ImageView 		vImage;
	RelativeLayout  vJoinStateLayout;
	ProgressDialog 	vProgress;	
	GoogleMap 		googleMap;
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
		
		
	GetCategory     mEvent;
	String          mEventId;
	boolean 		mFMapType = false;
	boolean         mIsEdit   = false;
	boolean         mIsJoin   = false;
	boolean         mIsDismiss = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		
		vJoinStateLayout= (RelativeLayout)findViewById(R.id.join_state_layout);
		vImage 			= (ImageView) findViewById(R.id.img);
		vJoinState 		= (TextView) findViewById(R.id.join_state);
		vEventName 		= (TextView) findViewById(R.id.evnt_name);
		vEventDesc 		= (TextView) findViewById(R.id.evnt_desc);
		vEventUsername 	= (TextView) findViewById(R.id.user_name);
		vEventDistance 	= (TextView) findViewById(R.id.evnt_distance);
		vEventTime 		= (TextView) findViewById(R.id.evnt_time);
		vJoinNum 		= (TextView) findViewById(R.id.join_view);
		vMessageNum     = (TextView) findViewById(R.id.message_num);
		vMapTypeBtn 	= (Button) findViewById(R.id.map_type_btn);
		vBtnHome 		= (Button) findViewById(R.id.btnHome);
		vBtnProfile 	= (Button) findViewById(R.id.btnInvitationManager);
		vBtnModify 		= (Button) findViewById(R.id.modify_btn);
		vBtnDelete 		= (Button) findViewById(R.id.delete_btn);
		vBtnJoin  		= (Button) findViewById(R.id.join_btn);
		vBtnHistory 	= (Button) findViewById(R.id.btnMessage);
		
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
			    
		try {
	        // Loading map
			initilizeMap();
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		vMapTypeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if(googleMap != null)
				{
					if(mFMapType)
					{
						googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); 
						mFMapType = false;
					}else
					{
						googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); 
						mFMapType = true;
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
		
		vEventUsername.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMUserProfileActivity.class);
				create.putExtra("selectUserId", mEvent.user_id);
				startActivity(create);
			}
		});
		
		
		vBtnModify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// modify event
				constant.gEventDetail = mEvent;
				Intent create = new Intent(getApplicationContext(),JMEventModifyActivity.class);
				startActivity(create);
			}
		});
		
		vBtnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// delete event
				ConfirmDialog();
			}
		});
		
		vMessageNum.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// message to this event
				Intent intent = new Intent(getApplicationContext(),JMEventMessagesActivity.class);
				intent.putExtra("eventId", mEvent.event_id);
				startActivity(intent);
			}
		});
		
		vBtnJoin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mEvent.joined.equals("0"))
				{
					// join event
					JoinToEvent();
				}else
				{
					// dismiss this event
					DismissEvent();
				}
			}
		});
		
		vJoinNum.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMEventJoinlistActivity.class);
				create.putExtra("event_id", mEvent.event_id);
				startActivity(create);
			}
		});
		
		
		final ScrollView mainScrollView = (ScrollView) findViewById(R.id.scrollView1);
		ImageView transparentImageView = (ImageView) findViewById(R.id.transparent_image);

		transparentImageView.setOnTouchListener(new View.OnTouchListener() {

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
	
		init();
	}
	
	public void init()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		constant.gUserId= settings.getString("createUserId", "");
		String  firstName = settings.getString("userFirstName", "");
		String  lastName = settings.getString("userLastName", "");
		constant.gUserName = String.format("%s %s", firstName,lastName);
		
		Intent intent = getIntent();
		mEventId = intent.getStringExtra("eventId");
		getEventDetail(mEventId);
	}
	
	public void initEvent()
	{
		if(mEvent != null)
		{
			vEventName.setText(mEvent.event_name);
			vEventDistance.setText(String.format("Distance: %s", mEvent.distance));
			vEventTime.setText(String.format("time: %s ~ %s", mEvent.event_start_datetime,mEvent.event_end_datetime));
			vEventDesc.setText(mEvent.description);
			vImage.setBackgroundResource(constant.getImageId(mEvent.type));  
			
			if(mEvent.user_id.equals(constant.gUserId))
			{
				showModifyButton();
			}else
			{
				if(mEvent.joined.equals("0"))
					hideJoinLayout();
				else
					showJoinLayout();
				
				hideModifyButton();
			}
	
			// show position of event to map
			LatLng pos = new LatLng(Double.parseDouble(mEvent.latitude),Double.parseDouble(mEvent.longitude));
			showPinToMap(pos,mEvent.event_name,mEvent.distance,mEvent.type);
		}
	}
	
/// ------------------------ show join state layout --------------------------------
	public void showJoinLayout()
	{
		vBtnJoin.setText("Leave");
		constant.gEventDetail.joined = "1";
		vJoinStateLayout.setVisibility(View.VISIBLE);
		vJoinState.setText("You already joined this event.");
	}
	
	public void hideJoinLayout()
	{
		vBtnJoin.setText("Join");
		constant.gEventDetail.joined = "0";
		vJoinStateLayout.setVisibility(View.GONE);
	}
	
/// -------------------------- show modify button and delete button by event creator ----------------
	
	public void showModifyButton()
	{
		vBtnModify.setVisibility(View.VISIBLE);
		vBtnDelete.setVisibility(View.VISIBLE);
		vBtnJoin.setVisibility(View.GONE);
		vJoinStateLayout.setVisibility(View.GONE);
	}
	
	public void hideModifyButton()
	{
		vBtnModify.setVisibility(View.GONE);
		vBtnDelete.setVisibility(View.GONE);
		vBtnJoin.setVisibility(View.VISIBLE);
	}
///	-------------------  parser response when get from server  about event detail ------------------------------------------------------------
			
	public void parserResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONObject jObject 	= data.getJSONObject("data");
					mEvent = new GetCategory(jObject);
					constant.gEventDetail = mEvent;
					
					initEvent();
					
					vMessageNum.setText(String.format("Messages ( %d ):",mEvent.leaveNum+mEvent.replyNum));
					if(!mEvent.firstname.equals("null"))
						vEventUsername.setText(mEvent.firstname);
					
					vJoinNum.setText(String.format("Attendee - Male: %s, Female: %s ", mEvent.joined_male,mEvent.joined_female));
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
/// -------------------------------parser response about event edit -----------------------
	public void parserEdit(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				String message   = data.getString("message");
				if(isSuccess.equals("0"))
				{
					if(mIsDismiss)
					{
						hideJoinLayout();
						Toast.makeText(this, "You left this event.", Toast.LENGTH_SHORT).show();
					}else if(mIsJoin)
					{
						showJoinLayout();
						Toast.makeText(this, "You joined this event.", Toast.LENGTH_SHORT).show();
					}else
					{
						Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
					}
						
				}else
				{
					Toast.makeText(this, message, Toast.LENGTH_LONG).show();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
		}
		
		if(!mIsJoin && !mIsDismiss)
		{
			Intent eventcreate = new Intent(getApplicationContext(), JMEventListActivity.class);
			startActivity(eventcreate);
		}
		
		mIsJoin = false;
		mIsDismiss = false;
		
	}
/// ------------------------------- get event detail data from server ------------------------ 	
	public void getEventDetail(String eventId)
	{
		if(getCurrentPos())
		{
			mIsEdit = false;
			String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_event_detail?event_id=%s&user_id=%s&lati=%s&long=%s",
					eventId,constant.gUserId,constant.g_CurrentLan,constant.g_CurrentLon);
			MyAsyncTask task1=new MyAsyncTask(this, baseURL);
			task1.execute();
			
			showProgress();
		}
	}
	
/// -------------------------------- join to this event ------------------------------------------	
	public void JoinToEvent()
	{
		mIsJoin = true; 
		String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/join_event?user_id=%s&event_id=%s&join=1",
				constant.gUserId,mEvent.event_id);
		MyAsyncTask task1=new MyAsyncTask(this, baseURL);
		task1.execute();
		
		showProgress();
	}

/// -------------------------------- dismiss this event --------------------------------------------
	public void DismissEvent()
	{
		mIsDismiss = true; 
		String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/join_event?user_id=%s&event_id=%s&join=0",
				constant.gUserId,mEvent.event_id);
		MyAsyncTask task1=new MyAsyncTask(this, baseURL);
		task1.execute();
		
		showProgress();
	}
	
/// -------------------------------- remove event -------------------------------------------------	
	public void RemoveEvent()
	{
		mIsEdit = true; 
		String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/remove_event?user_id=%s&event_id=%s",
				constant.gUserId,mEvent.event_id);
		MyAsyncTask task1=new MyAsyncTask(this, baseURL);
		task1.execute();
		
		showProgress();
	}

/// ------------------------------- show progress and hide progress -----------------------------	
	public void showProgress()
	{
		vProgress = new ProgressDialog(JMEventDetailActivity.this);
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
/// --------------------------------------------- show dialog --------------------------------
	
	public void ConfirmDialog()
	{
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_input_token);
		dialog.setCanceledOnTouchOutside(true);
		
		Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
		okBtn.setOnClickListener(new View.OnClickListener() {
	       	 
			@Override
			public void onClick(View view) {			
				RemoveEvent();
				dialog.dismiss();
			}
		});
		
		Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		// if button is clicked, close the custom dialog
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				dialog.dismiss();
			}
		});
		dialog.show();  
	}
/// -------------------------------------------------------------	
	
	///// show pin with position to map
    public void showPinToMap(LatLng pos,String name,String city, int type)
    {
    	if (googleMap!=null){
		   
    		googleMap.addMarker(new MarkerOptions()
	          .position(pos)
	          .title(name)
	          .snippet(city)
	          .icon(BitmapDescriptorFactory
	              .fromResource(constant.getMapIconId(type))));
    		
    		// Move the camera instantly to hamburg with a zoom of 15.
   		 	googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 8));

   		 	// Zoom in, animating the camera.
   		 	googleMap.animateCamera(CameraUpdateFactory.zoomTo(6), 1000, null);
   	   }
    }
    

	/**
    * function to load map. If map is not created it will create it for you
    * */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initilizeMap() {
       if (googleMap == null) {
           googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                   R.id.map_event_detail)).getMap();
           
           // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); 
           // check if map is created successfully or not
           if (googleMap == null) {
               Toast.makeText(getApplicationContext(),
                       "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                       .show();
           }
       }
   }
    
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
	
   @Override
   public void getResponse(JSONObject data) {
	   // TODO Auto-generated method stub
	   if(mIsEdit || mIsJoin || mIsDismiss)
	   {
		   parserEdit(data);
		   mIsEdit = false;
	   }else
	   {
		   parserResponse(data);  
	   }
	   hideProgress();	
   }
	
}
