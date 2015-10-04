package com.nfreespace.app.android.JoMe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class JMSplashActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_splash);
	 
	    new Handler().postDelayed(new Runnable(){
			public void run() {
				/* show login layout */
				Intent intent = new Intent(JMSplashActivity.this, JMLoginActivity.class);
				startActivity(intent);
				finish();	
			}
		}, 2000);
	}
}
