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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.InviteListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.GetCategory1;

public class JMInviteAllFriendsActivity extends Activity  implements ServerResponse{
	 GridView 					vGridview ;
	 Button						vBtnHome,vBtnProfile,vBtnSelect,vBtnHistory;
	 ProgressDialog 			vProgress;
	 
	 String 					mFb_ids , mUserId ;
	 ArrayList<GetCategory1>	mFriendList = new ArrayList<GetCategory1>(); 
	 InviteListAdapter 			mAdapter;
	 
	 public void onCreate(Bundle savedInsatanceState) {
		 super.onCreate(savedInsatanceState);
		 setContentView(R.layout.activity_sort_friend_connection);
		 
		 vGridview 		= (GridView) findViewById(R.id.grid_view);
		 vBtnHome 		= (Button) findViewById(R.id.btnHome);
		 vBtnProfile 	= (Button)findViewById(R.id.btnInvitationManager);
		 vBtnHistory = (Button) findViewById(R.id.btnMessage);
		 
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
				
		vBtnSelect = (Button) findViewById(R.id.selectBtn);
		vBtnSelect.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
	    		ArrayList<String> selectedItems = mAdapter.getCheckedItems();
				Toast.makeText(JMInviteAllFriendsActivity.this, "Total photos selected: "+selectedItems.size(), Toast.LENGTH_SHORT).show();
			      
				int total = selectedItems.size();
			
				SharedPreferences settingimg = getSharedPreferences(constant.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settingimg.edit();
				editor.putInt("SelectedImages",total);
				editor.commit(); 
				finish();
			}
		});
			
		getAllFriends();
		
	 }
	 
/// ----------------- Get Invite all Facebook Friend -----------------------------	 
	 public void getAllFriends()
	 {
		 SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		 mFb_ids = settings.getString("friendId", "");
		 mUserId = settings.getString("UserId", "");
			
		 String baseUrl = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/invite_all_fb_friends?fbid=%s",mFb_ids);
		 MyAsyncTask task1=new MyAsyncTask(this,baseUrl);
		 task1.execute();
		 showProgress();
	 }
	 
	 public void showProgress()
	 {
		 vProgress = new ProgressDialog(JMInviteAllFriendsActivity.this);
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
/// ------------------------------------------------ parser JSONObject ---------------------------	 
	 public void parserResponse(JSONObject data)
	 {
		 if(data != null)
		 {
			JSONArray arr;
			try {
				arr = data.getJSONArray("data");
			
				for(int i=0;i<arr.length();i++) {
					JSONObject c = arr.getJSONObject(i);
					GetCategory1 values = new GetCategory1();
					values.username = c.getString("username"); 
					values.Profile_pic = c.getString("profile_pic");
					values.userId=c.getString("id");
					mFriendList.add(values);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		 
		 showGridView(); 
	 }
	 
/// --------------------------------------------------------------------------------------------------	 

	 public void showGridView()
	 {
		 if(mFriendList != null)
		 {
			 mAdapter = new InviteListAdapter(JMInviteAllFriendsActivity.this, mFriendList);
			 vGridview.setAdapter(mAdapter);
		 }
	 }
	 
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	} 

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parserResponse(data);
		hideProgress();
	}
	 

}
