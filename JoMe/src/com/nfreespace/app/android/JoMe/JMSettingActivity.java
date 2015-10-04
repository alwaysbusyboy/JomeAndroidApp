package com.nfreespace.app.android.JoMe;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class JMSettingActivity extends Activity {

	Button 	  		vBtnHome,vBtnProfile,vBtnHistory;
	SeekBar   		vRadiusBar ;
	TextView  		vRadiusTxt,vMyfollowers, vFollowbyMe;
	
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
	    setContentView(R.layout.activity_setting);
	    
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
	  	    
	    vBtnHome 	= (Button) findViewById(R.id.btnHome);
		vBtnProfile = (Button) findViewById(R.id.btnInvitationManager);
		vBtnHistory = (Button) findViewById(R.id.btnMessage);
	    vRadiusBar = (SeekBar)findViewById(R.id.seekbar_setting);
	    vRadiusTxt = (TextView)findViewById(R.id.searchradius_value);
	    vMyfollowers = (TextView)findViewById(R.id.myfollowers_layout);
	    vFollowbyMe = (TextView)findViewById(R.id.follow_layout);
	    
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
		
		vMyfollowers.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMFollowActivity.class);
				create.putExtra("title", "MyFollowers");
				create.putExtra("type", "1");
				startActivity(create);
			}
		});
	
		vFollowbyMe.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMFollowActivity.class);
				create.putExtra("title", "Peoples that I follow");
				create.putExtra("type", "2");
				startActivity(create);
		}
		});
		
	    vRadiusBar.setOnTouchListener(new OnTouchListener() 
	    {
	    	@Override
	    	public boolean onTouch(View v, MotionEvent event) 
	    	{
	                
	    		if(event.getAction() == MotionEvent.ACTION_MOVE)
	    		{
	    			vRadiusBar.setProgress(vRadiusBar.getProgress());
	    			constant.g_Radius = vRadiusBar.getProgress();
	    			vRadiusTxt.setText(String.format("%d km", vRadiusBar.getProgress()));
	    			return false;
	    		}
	      
	    		if (event.getAction() == MotionEvent.ACTION_UP)
	    		{
	    			vRadiusBar.setProgress(vRadiusBar.getProgress());
	    			constant.g_Radius = vRadiusBar.getProgress();
	    			vRadiusTxt.setText(String.format("%d km", vRadiusBar.getProgress()));
	    		}
	    		return true;
	    	}
	    });
	}
	
	public void init()
	{
		vRadiusBar.setProgress(constant.g_Radius);
		vRadiusTxt.setText(String.format("%d km", constant.g_Radius));
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
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
}
