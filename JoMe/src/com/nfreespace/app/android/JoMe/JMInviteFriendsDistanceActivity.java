package com.nfreespace.app.android.JoMe;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;

public class JMInviteFriendsDistanceActivity extends Activity implements OnItemClickListener,LocationListener,ServerResponse {

	  GridView 			vGridView;
	  Button   			vBtnHome,vBtnProfile,vBtnHistory;
	  ProgressDialog 	vProgress;
	  String 			mfb_ids;
	  String 			mUserId ;
	  String            interest;
	  Double 			lat,lng;
	  
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  setContentView(R.layout.activity_sort_distance_interest);
		
		  SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		  mfb_ids = settings.getString("friendId", "");
		  mUserId = settings.getString("UserId", "");
		
		  vGridView 	= (GridView) findViewById(R.id.grid_view);
		  vBtnHome 		= (Button) findViewById(R.id.btnHome); 
		  vBtnProfile 	= (Button)findViewById(R.id.btnInvitationManager);
		  vBtnHistory   = (Button) findViewById(R.id.btnMessage);
		  
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
			
		 
		  vGridView.setOnItemClickListener(new OnItemClickListener() {
			  public void onItemClick(AdapterView<?> parent, View v,
					  int position, long id) {
			   Toast.makeText(getApplicationContext(),((TextView) v).getText(), Toast.LENGTH_SHORT).show();
			  }
		  });  
		 
		  getFriendWithDistance();
	}
	  
/// ------------------- Get Invite Friend with distance frome server ------------------------
	
	public void getFriendWithDistance()
	{
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/invite_by_distance_interest?latitude=%s&longitude=%s&distance=%s&interest=%s",
				lat,lng,lat,interest);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		showProgress();
	}

/// ----------------------------- show progress while get reponse from server ---------------------	
	public void showProgress()
	{
		vProgress = new ProgressDialog(JMInviteFriendsDistanceActivity.this);
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
	
	public void parserResponse(JSONObject data)
	{
		
	}
	
/// ----------------------------------------------------------------------	
	
		
		
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
				
	}
			
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		lat = location.getLatitude();
		lng = location.getLongitude();
	}
			
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
			
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
				
	}
			
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
				
	}

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parserResponse(data);
		hideProgress();
	}
	 

}


