package com.nfreespace.app.android.JoMe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidhive.pushnotifications.WakeLocker;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.android.gcm.GCMRegistrar;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;

public class JMLoginActivity extends Activity implements ServerResponse,JMCallbackRegister{

	Button 					vBtnFacebook,vBtnEmail;
	ProgressDialog 			vProgress;
	RelativeLayout			vBtnLayout;

	String  			    mFirstName,mLastName,mUserEmail, mFacebookId,mPicUrl,mFb_Ids = "",mGender,mBirthday;
	String 					mUserFirstName, mUserLastName, mUserId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_login_fb);
	    
	    vBtnLayout = (RelativeLayout)findViewById(R.id.btn_layout);
        vBtnEmail = (Button)findViewById(R.id.email_login);
        vBtnFacebook=(Button)findViewById(R.id.facebook_login);
        
        init();
        vBtnFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				loginfacebook();
			}
		});
		
		vBtnEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loginWithUs();
			}
		});
		
	}
	
	public void init()
	{
		constant.g_Callback = this;
		showBtnLayout();
	}
	
/// --------------------------------------- show button layout  -----------------------
	
	public void showBtnLayout()
	{
		 new Handler().postDelayed(new Runnable(){
				public void run() {
					/* show login layout */
					showAnimation();
				}
		 }, 1000);
	}
	
	public void showAnimation()
	{
		vBtnLayout.setVisibility(View.VISIBLE);
		vBtnLayout.startAnimation(AnimationUtils.loadAnimation(JMLoginActivity.this, R.anim.rbm_in_from_left));
	}
	
/// --------------------------------------------------------------------	

/// --------------------------------------- login with us -------------
	
	public void loginWithUs()
	{
		
		mFacebookId = "1495383177362243";
		mFb_Ids = "1495383177362147";
		mUserId = "27";
		mUserFirstName = "Chui";
		mUserLastName = "Guang";
		mPicUrl = "http://graph.facebook.com/1495383177362243/picture?type=large";
		
		goMainPage();
	}
	
/// --------------------------------------	
/// -------------------------------------------  get friend Ids --------------------------------------
	
	public void getIds( Session session){
        
        String query = "select uid from user where uid in (select uid2 from friend where uid1 = me())";
        Bundle params = new Bundle();
        params.putString("q", query);
        Request request = new Request(session, "/me/friends", params, HttpMethod.GET, new Request.Callback() {

                    @Override
                    public void onCompleted(Response response) {

                    	GraphObject go = response.getGraphObject();
                    	
                    	if(go != null)
                    	{
                    		JSONObject json = go.getInnerJSONObject();
        					JSONArray jsonarray = null;
        					try {
        						jsonarray = json.getJSONArray("data");
        					} catch (JSONException e1) {
        						// TODO Auto-generated catch block
        						e1.printStackTrace();
        					}

        					for (int i = 0; i < jsonarray.length(); i++) {
        						try {
        							JSONObject c = jsonarray.getJSONObject(i);
        							
        							if (mFb_Ids.equals("")) {
        								mFb_Ids =c.getString("id");
        							} else {
        								mFb_Ids = mFb_Ids + "," + c.getString("id");
        							}
        						} catch (JSONException e) {
        							e.printStackTrace();
        						}
        					}
        		    	}
                    }
                });
        Request.executeBatchAsync(request); 
    }
	
/// ------------------------ login  using facebook-----------------  	
	public void loginfacebook()
	{
	   constant.showProgress(this);
	   Session.openActiveSession(this, true, new Session.StatusCallback() {

	        // callback when session changes state
	        @Override
	        public void call(final Session session, SessionState state, Exception exception) {
	          if (session.isOpened()) {

	            // make request to the /me API
	            Request.newMeRequest(session, new Request.GraphUserCallback() {

	              // callback after Graph API response with user object
	              @Override
	              public void onCompleted(GraphUser user, Response response) {
	            	  
	            	  if (user != null) {
	                  	
	                	mFirstName 	= user.getFirstName();
	                	mLastName  	= user.getLastName();
	                	mUserEmail  = (String) user.asMap().get("email");
	                	mFacebookId = user.getId();
	                	mPicUrl  	= String.format("http://graph.facebook.com/%s/picture?type=large",mFacebookId);
	                	mGender 	= user.asMap().get("gender").toString();
	                	
	                	if(mGender.equals("male"))
	                		mGender = "1";
	                	else
	                		mGender = "2";
	                	
	                	mBirthday = user.getBirthday();
	                	if(mBirthday == null)
	                		mBirthday = "";
	                	registerKey();
    			     }
	              }
	            }).executeAsync();
	          }
	        }
	   });
	}
	
///------------------------------------  get key for PUSH NOTIFICATION -----------------------
	public void registerKey()
	{
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				constant.DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, constant.g_SendId);
		} else {
			loginWithFacebook(regId);
		}
	}
	
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(constant.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());
			
			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */
			
			// Showing received message
			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
			
			// Releasing wake lock
			WakeLocker.release();
		}
	};
	
	
/// ----------------------------------  login with FaceBook --------------------------------------	
	public void loginWithFacebook(String regId)
	{
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/create_user?firstname=%s&lastname=%s&email=%s&facebook_id=%s&interest=%s&profile_pic=%s&gender=%s&dev_id=%s",
				mFirstName,mLastName,mUserEmail,mFacebookId,"",mPicUrl,mGender,regId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
	}	
/// -------------------------------------------- response parser -----------------------------------------
	
	public void parserResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONObject detailReturned= data.getJSONObject("data");
					
					mUserFirstName = detailReturned.getString("firstname"); 
					mUserLastName = detailReturned.getString("lastname");
					mUserId = detailReturned.getString("user_id"); 
				}else
				{
					Toast.makeText(this, "Login Fail", Toast.LENGTH_LONG).show();
				}
				
						
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		goMainPage();
	}
	
/// ------------------------------- go to main page -----------------	
	public void goMainPage()
	{
		saveData();
		Intent it = new Intent(JMLoginActivity.this, JMMainActivity.class);
		startActivity(it);
		finish();
	}
/// ------------------------------ save data to preference -----------------------------------	
	public void saveData()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
    			
		editor.putString("UserId", mFacebookId);  
		editor.putString("friendId", mFb_Ids);	
		editor.putString("createUserId", mUserId);	
		editor.putString("userFirstName", mUserFirstName);
		editor.putString("userLastName", mUserLastName);
		editor.putString("picurl", mPicUrl);
		editor.putString("gender", mGender);
		
		editor.commit();
	}
/// ------------------------------- get data from preference -----------------------------------	
	public boolean getData()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		mUserFirstName = settings.getString("userFirstName", "");
		mUserLastName  = settings.getString("userLastName", "");
		mPicUrl  = settings.getString("picurl", "");
		mUserId  = settings.getString("createUserId", "");
		mFb_Ids  = settings.getString("friendId", "");
		mFacebookId  = settings.getString("UserId", "");
		
		if(mUserId.equals(""))
		{
			return false;
		}else
		{
			return true;
		}
	}
	
	 @Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	
	 }

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parserResponse(data);
		constant.hideProgress();
	}

	@Override
	public void getRegisterId(String regId) {
		// TODO Auto-generated method stub
		loginWithFacebook(regId);
	}
	
	
}
