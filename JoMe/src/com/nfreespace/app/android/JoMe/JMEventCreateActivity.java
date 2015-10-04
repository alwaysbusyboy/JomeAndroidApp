package com.nfreespace.app.android.JoMe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;

public class JMEventCreateActivity extends Activity implements OnClickListener,ServerResponse {
	
	ImageView 			vPlaceCheck;
	EditText 			vEdtEventName, vEdtDesc;
	TextView 			vTxtshowtime,vTxtshowtimeEnd;
	TextView 			vTxtlat,vTxtlng ,vTxtPlaceSet;
	CheckBox 			vEventpublic,vInviteUser;
	Button   			vBtnCross, vBtnSetPlace,vBtnCreateEvent,vBtnHome,vBtnProfile,vBtnHistory;
	ProgressDialog 	    vProgress;
	
	/// Google admov
	AdView 				adView;
	RelativeLayout 		adLayout;
		
	String            	mEventName,mDescription, mLatitude = "", mLongitude ="",mShowtime,mShowtimeEnd;
	SharedPreferences 	settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_create_event2);

		vBtnProfile 	= (Button) findViewById(R.id.btnInvitationManager);
		vBtnHome 		= (Button) findViewById(R.id.btnHome);
		vBtnCross 		= (Button) findViewById(R.id.backbtn);
		vBtnCreateEvent = (Button) findViewById(R.id.createevent);
		vBtnSetPlace 	= (Button) findViewById(R.id.btnaddvenue);
		vBtnHistory 	= (Button)findViewById(R.id.btnMessage);
		vTxtshowtime	= (TextView) findViewById(R.id.starttxtTime);
		vTxtshowtimeEnd	= (TextView) findViewById(R.id.endtxtTime);
		vTxtPlaceSet  	= (TextView) findViewById(R.id.txtplaceset);
		vTxtlat 		= (TextView)findViewById(R.id.txtlat);
	    vTxtlng 		= (TextView)findViewById(R.id.txtlng);
		vPlaceCheck 	= (ImageView) findViewById(R.id.imageview_check);
		vEdtEventName	= (EditText) findViewById(R.id.edtEventName); 
		vEdtDesc		= (EditText) findViewById(R.id.edtDesc);
		vEventpublic 	= (CheckBox) findViewById(R.id.checkbox_public);
		vInviteUser 	= (CheckBox) findViewById(R.id.checkbox_invite_nearby);
		
		vEventpublic.setOnClickListener(this);
		vInviteUser.setOnClickListener(this);
		
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

     
	    vBtnCreateEvent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//validation on event name and decription 
				mEventName = vEdtEventName.getText().toString();
				mDescription = vEdtDesc.getText().toString();
				
				if(mEventName.length()==0){
					vEdtEventName.setError(Html.fromHtml("<font color='red'>Event name is required</font>"));
					vEdtEventName.requestFocus();
				}
				
				if(mDescription.length()==0){
					vEdtDesc.setError(Html.fromHtml("<font color='red'>Event description is required</font>"));
					vEdtDesc.requestFocus();
				} else {
					creatEvent();
					
				}
			}
		});
	 
		vBtnCross.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent inviteNearby = new Intent(getApplicationContext(),JMJoinActivity.class);
	    		startActivity(inviteNearby); 
			}
		});
		
		vBtnSetPlace.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				  SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
			      SharedPreferences.Editor editor = settings.edit();
			      String eventName			= vEdtEventName.getText().toString();
			      String descriptionName 	= vEdtDesc.getText().toString();
			      String showTime 			= vTxtshowtime.getText().toString();
			      String endTime  			= vTxtshowtimeEnd.getText().toString();

			      if(eventName!=null && eventName.length()>0)
			      {
			    	  editor.putString("EventsName",eventName);
			      }
			      if(descriptionName!=null && descriptionName.length()>0)
			      {
			    	  editor.putString("DescriptionName",descriptionName); 
			      } 
			      if(showTime!=null && showTime.length()>0)
			      {
			    	  editor.putString("ShowTime",showTime); 
			      } 
			      if(endTime!=null && endTime.length()>0)
			      {
			    	  editor.putString("EndTime",endTime); 
			      } 
			      editor.commit();
				
			      Intent itAddVenue = new Intent(getApplicationContext(),JMSetPlaceActivity.class);
			      startActivity(itAddVenue);
			}
		});
		
		
		/// ------------------- date picker about start time ------------- 
		final CustomDateTimePicker custom = new CustomDateTimePicker(JMEventCreateActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() { 
	 		@Override
	 		public void onSet(Dialog dialog, Calendar calendarSelected,
	 				Date dateSelected, int year, String monthFullName,
	 				String monthShortName, int monthNumber, int date,
	 				String weekDayFullName, String weekDayShortName,
	 				int hour24, int hour12, int min, int sec,
	 				String AM_PM) {
			    
	 			vTxtshowtime.setText(year+ "-" + (monthNumber+1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec);
	 			
	 			saveTime();
	 		}
									
	 		@Override
	 		public void onCancel() {
									
	 		}
	 	});
			 
	 	custom.set24HourFormat(false);
	 	custom.setDate(Calendar.getInstance());
									
	 	vTxtshowtime.setOnClickListener(new OnClickListener() {
									
	 		@Override
	 		public void onClick(View v) {
	 			custom.showDialog();
	 		}
	 	});
	 	
	 	/// ------------------------ date picker about end time -------------------------
	 	final CustomDateTimePicker custom1 = new CustomDateTimePicker(JMEventCreateActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() { 
			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) {
					
					vTxtshowtimeEnd.setText(year+ "-" + (monthNumber+1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)+ " " + hour24 + ":" + min + ":" + sec);
			        	
					saveTime();
			}
									
			@Override
			public void onCancel() {
									
			}
		});
		custom1.set24HourFormat(false);
		custom1.setDate(Calendar.getInstance());
						
		vTxtshowtimeEnd.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				custom1.showDialog();
			}
		});
	}
	
	public void init()
	{
		if(constant.gIsCreate)
		{
			constant.gIsCreate = false;
		}else
		{
			settings = getSharedPreferences(constant.PREFS_NAME, 0);
		    mEventName 		= settings.getString("EventsName", "");
		    mDescription 	= settings.getString("DescriptionName", "");
	        mLatitude  		= settings.getString("tvLatitudes", "");
	        mLongitude 		= settings.getString("tvLongitudes", "");
	        mShowtime 		= settings.getString("txtshowtime", "");
	        mShowtimeEnd 	= settings.getString("txtshowtimeEnd", "");
	    }
		
		if(mEventName!=null && mEventName.length()>0)
		{
			vEdtEventName.setText(mEventName);  
		}
		if(mDescription!=null && mDescription.length()>0)
		{
			vEdtDesc.setText(mDescription);
		}
		      
		if(mShowtime!=null && mShowtime.length()>0)
		{
			vTxtshowtime.setText(mShowtime);
		}
		if(mShowtimeEnd!=null && mShowtimeEnd.length()>0)
		{
			vTxtshowtimeEnd.setText(mShowtimeEnd); 
		} 
		       
		vTxtlng.setText(mLongitude);
		vTxtlat.setText(mLatitude);
		
		if(mLatitude.equals("") && mLongitude.equals(""))
		{
			vTxtPlaceSet.setVisibility(View.GONE);
			vPlaceCheck.setVisibility(View.GONE);
		}else
		{
			vTxtPlaceSet.setVisibility(View.VISIBLE);
			vPlaceCheck.setVisibility(View.VISIBLE);
		}
	
    }
							
///	-------------------------------------- save time  to preference -----------------------------			
	public void saveTime()
	{
		String showTimeStart= vTxtshowtime.getText().toString();
		String showTimeEnd= vTxtshowtimeEnd.getText().toString();
		
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
	    
		if(showTimeStart!=null && showTimeStart.length()>0)
		{
			editor.putString("txtshowtime",showTimeStart);
		}
		if(showTimeEnd!=null && showTimeEnd.length()>0)
		{
			editor.putString("txtshowtimeEnd",showTimeEnd); 
		}
		editor.commit();    
	}

/// ---------------------------------- show dialog when user click invite check box ----------------------------------------	
	public void showConfirmDialog()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(JMEventCreateActivity.this) ;
	    alertDialog.setTitle("Confirm Box..."); //set title
	    alertDialog.setPositiveButton("Sort By Distance Interest",
	       new DialogInterface.OnClickListener() {
	  
	    	public void onClick(DialogInterface dialog, int Button) {
	    		Intent invitebyinterest = new Intent(getApplicationContext(),JMInviteFriendsDistanceActivity.class);
	    		startActivity(invitebyinterest);   	

	    	}
	    });

	    alertDialog.setNegativeButton("Sort By Friend Connection",
	    		new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog,int Button) {

	    		Intent inviteNearby = new Intent(getApplicationContext(),JMInviteAllFriendsActivity.class);
	    		startActivity(inviteNearby);   	
	    	}
	    });
	    
	    alertDialog.show();
	}
	
///	----------------  parser response from server and go to event list page ---------------------------------------------------------------

	public void parserResponse(JSONObject data)
	{
		Intent eventcreate = new Intent(getApplicationContext(), JMEventListActivity.class);
		startActivity(eventcreate);
		Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();
	}

/// ------------------- Create event ----------------------------------	
	public void creatEvent()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		String fb_ids = settings.getString("friendId", "");
		String create_user_ids = settings.getString("createUserId", "");
		
		int frds_invited = settings.getInt("SelectedImages",0);
		String invitedFriends=String.valueOf(frds_invited);
		String publicOrPrivate = settings.getString("IsChecked", "");
		
		String startTime = vTxtshowtime.getText().toString();
		String endTime = vTxtshowtimeEnd.getText().toString();
		mLatitude = vTxtlat.getText().toString();
		mLongitude = vTxtlng.getText().toString();
	 		
		String eName = mEventName ;
		String eDes  = mDescription ;
	 		
	 		
		try {
			eName = URLEncoder.encode(mEventName,"utf-8");
			eDes  = URLEncoder.encode(mDescription, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(startTime.equals("") || endTime.equals(""))
		{
			constant.alertbox("Warning!", "Please set  time.", this);
		}else if(!constant.compareTime(startTime, endTime))
		{
			constant.alertbox("Warning!", "Please set start time again.", this);
		}else if(!constant.checkPassedTime(endTime))
		{
			constant.alertbox("Warning!", "Please set current time or future time.", this);
		}else if(mLatitude.equals("") && mLongitude.equals(""))
		{
			constant.alertbox("Warning!", "Please set place.", this);
		}else
		{
			String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/create_event?event_name=%s&user_id=%s&fbids=%s&description=%s&lati=%s&long=%s&event_start_datetime=%s&event_end_datetime=%s&frds_invited=%s&public=%s",
					eName, create_user_ids,fb_ids,eDes,mLatitude,mLongitude,startTime,endTime,invitedFriends,publicOrPrivate);
			MyAsyncTask task1=new MyAsyncTask(this, baseURL);
			task1.execute();
			
			showProgress();
		}
	}

/// ---------------------------------- show progress while get response from server -----------------------------	
	public void showProgress()
	{
		vProgress = new ProgressDialog(JMEventCreateActivity.this);
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
			
/// ----------------------------------------------------------------------------	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
		getSharedPreferences(constant.PREFS_NAME, 0).edit().clear().commit();
	}
	
/// ------------------------------------- click listener --------------------------
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.checkbox_public:
				if(!vEventpublic.isChecked())
				{
					vEventpublic.setChecked(false);
					SharedPreferences settingss = getSharedPreferences(constant.PREFS_NAME, 0);
					SharedPreferences.Editor editorr = settingss.edit();
					editorr.putString("IsChecked", "0");
					editorr.commit();
					
				}else
				{
					vEventpublic.setChecked(true);
					
					SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("IsChecked", "1");
					editor.commit();
				}
		
				break;
			case R.id.checkbox_invite_nearby:
				if(!vInviteUser.isChecked())
				{
					vInviteUser.setChecked(false);
				}else
				{
					vInviteUser.setChecked(true);
					showConfirmDialog();
				}
			    break;
			
		}
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
		parserResponse(data);
		hideProgress();
	}
	
}
