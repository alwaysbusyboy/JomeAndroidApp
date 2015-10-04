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
import com.nfreespace.app.android.JoMe.Object.GetCategory;

public class JMEventModifyActivity extends Activity implements OnClickListener,ServerResponse {
	
	ImageView 			vPlaceCheck;
	EditText 			vEdtEventName, vEdtDesc;
	TextView 			vTxtshowtime,vTxtshowtimeEnd;
	TextView 			vTxtlat,vTxtlng ,vTxtPlaceSet;
	CheckBox 			vEventpublic,vInviteUser;
	Button   			vBtnCross, vBtnSetPlace,vBtnModifyEvent,vBtnHome,vBtnProfile,vBtnHistory;
	ProgressDialog 	    vProgress;
	
	/// Google admov
	AdView 				adView;
	RelativeLayout 		adLayout;
		
		
	GetCategory     	mEvent;
	String            	mEventName,mDescription;
	int 				mYear, mMonth, mDay, mHour, mMin, mAm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_modify);

		vBtnProfile 	= (Button) findViewById(R.id.btnInvitationManager);
		vBtnHome 		= (Button) findViewById(R.id.btnHome);
		vBtnCross 		= (Button) findViewById(R.id.backbtn);
		vBtnModifyEvent = (Button) findViewById(R.id.createevent);
		vBtnSetPlace 	= (Button) findViewById(R.id.btnaddvenue);
		vBtnHistory 	= (Button) findViewById(R.id.btnMessage);
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
		     
        vBtnModifyEvent.setOnClickListener(new View.OnClickListener() {
			
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
					ModifyEvent();
				}
			}
		});
	 
		vBtnCross.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent inviteNearby = new Intent(getApplicationContext(),JMEventDetailActivity.class);
				inviteNearby.putExtra("eventId", mEvent.event_id);
	    		startActivity(inviteNearby); 
	    		finish();
			}
		});
		
		vBtnSetPlace.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				 constant.gIsModify = true;
				 Intent itAddVenue = new Intent(getApplicationContext(),JMSetPlaceActivity.class);
				 startActivity(itAddVenue);
			}
		});
		
		
		/// ------------------- date picker about start time ------------- 
		final CustomDateTimePicker custom = new CustomDateTimePicker(JMEventModifyActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() { 
	 		@Override
	 		public void onSet(Dialog dialog, Calendar calendarSelected,
	 				Date dateSelected, int year, String monthFullName,
	 				String monthShortName, int monthNumber, int date,
	 				String weekDayFullName, String weekDayShortName,
	 				int hour24, int hour12, int min, int sec,
	 				String AM_PM) {
			    
	 				
	 			vTxtshowtime.setText(year+ "-" + (monthNumber+1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH) + " " + hour24 + ":" + min + ":" + sec);
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
	 	final CustomDateTimePicker custom1 = new CustomDateTimePicker(JMEventModifyActivity.this, new CustomDateTimePicker.ICustomDateTimeListener() { 
			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) 
			{
				vTxtshowtimeEnd.setText(year+ "-" + (monthNumber+1) + "-" + calendarSelected.get(Calendar.DAY_OF_MONTH)+ " " + hour24 + ":" + min + ":" + sec);
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
    	mEvent = constant.gEventDetail;
    	
    	if(mEvent != null)
    	{
    		vEdtEventName.setText(mEvent.event_name);
    		vEdtDesc.setText(mEvent.description);
    		vTxtlat.setText(mEvent.latitude);
    		vTxtlng.setText(mEvent.longitude);
    		vTxtshowtime.setText(mEvent.event_start_datetime);
    		vTxtshowtimeEnd.setText(mEvent.event_end_datetime);
    		
    		if(constant.gIsModify)
        	{
        		constant.gIsModify = false;
        		SharedPreferences  settings = getSharedPreferences(constant.PREFS_NAME, 0);
        		vTxtlat.setText(settings.getString("tvLatitudes", ""));
        		vTxtlng.setText(settings.getString("tvLongitudes", ""));
        	}
    	}
    }
							
	
/// ---------------------------------- show dialog when user click invite check box ----------------------------------------	
	public void showConfirmDialog()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(JMEventModifyActivity.this) ;
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
		Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
	}

/// ------------------- Create event ----------------------------------	
	public void ModifyEvent()
	{
		String eName = mEventName ;
		String eDes  = mDescription ;
		String startTime = vTxtshowtime.getText().toString();
		String endTime = vTxtshowtimeEnd.getText().toString();
		String latitude = vTxtlat.getText().toString();
		String longitude = vTxtlng.getText().toString();
	 	
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
		}else if(latitude.equals("") && longitude.equals(""))
		{
			constant.alertbox("Warning!", "Please set place.", this);
		}else
		{
			String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/update_event?user_id=%s&event_id=%s&event_start_datetime=%s&event_end_datetime=%s&description=%s&event_name=%s&lati=%s&long=%s",
					constant.gUserId,mEvent.event_id,startTime,endTime,eDes,eName,latitude,longitude);
			MyAsyncTask task1=new MyAsyncTask(this, baseURL);
			task1.execute();

			showProgress();
		}
	}

/// ---------------------------------- show progress while get response from server -----------------------------	
	public void showProgress()
	{
		vProgress = new ProgressDialog(JMEventModifyActivity.this);
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
	
/// ------------------------------------- click listener --------------------------
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.checkbox_public:
				if(!vEventpublic.isChecked())
				{
					vEventpublic.setChecked(false);
					
				}else
				{
					vEventpublic.setChecked(true);
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
	public void onPause()
	{
		
		
		if(adView != null)
			adView.pause();
		
		if(constant.gEventDetail != null)
		{
			constant.gEventDetail.event_name = vEdtEventName.getText().toString();
			constant.gEventDetail.description = vEdtDesc.getText().toString();
			constant.gEventDetail.latitude = vTxtlat.getText().toString();
			constant.gEventDetail.longitude = vTxtlng.getText().toString();
			constant.gEventDetail.event_start_datetime = vTxtshowtime.getText().toString();
			constant.gEventDetail.event_end_datetime = vTxtshowtimeEnd.getText().toString();
		}
		
		super.onPause();
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
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parserResponse(data);
		hideProgress();
	}
	
	
	
}

