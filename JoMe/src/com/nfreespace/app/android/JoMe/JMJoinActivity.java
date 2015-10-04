package com.nfreespace.app.android.JoMe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class JMJoinActivity extends FragmentActivity {

	Button 			vBtnHome,vBtnProfile,vBtnHistory;
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		
		vBtnHome 	= (Button) findViewById(R.id.btnHome);
		vBtnProfile = (Button) findViewById(R.id.btnInvitationManager);
		vBtnHistory = (Button) findViewById(R.id.btnMessage);
		
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
		
	}
	
	public void init()
	{
		goCreatEvent();
	}
	
	public void goCreatEvent()
	{
		/// go to join fragment
		FragmentTransaction fts = getSupportFragmentManager().beginTransaction();
		fts.replace(R.id.join_frame_layout, new JMJoinFragment());
		fts.commit();
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
}
