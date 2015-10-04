package com.nfreespace.app.android.JoMe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.JMJoinedUserListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMUserSingleObject;

public class JMEventJoinlistActivity extends Activity implements ServerResponse{

	Button   			vBtnBack;
	ListView 			vUserList;
		
	String   			mEventId;
	List<JMUserSingleObject>  mUserList = new ArrayList<JMUserSingleObject>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_joinlist);
		
		vBtnBack = (Button)findViewById(R.id.btnCrose);
		vUserList = (ListView)findViewById(R.id.user_list);
		
		init();
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vUserList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	        	String selectUserId = mUserList.get(position).userId;
				Intent intent = new Intent(JMEventJoinlistActivity.this, JMUserProfileActivity.class);
				intent.putExtra("selectUserId", selectUserId);
				startActivity(intent);
			}
		});
	}
	
	public void init()
	{
		Intent intent = getIntent();
		mEventId = intent.getStringExtra("event_id");
		
		getJoinedUsersFS();
	}

/// ----------------------------------- show user list --------------------------------	
	public void showUserList()
	{
		vUserList.setAdapter(new JMJoinedUserListAdapter(this, mUserList));
	}

/// ----------------------------------- get joined user list with event id --------------------------	
	public void getJoinedUsersFS()
	{
		if(mEventId != null)
		{
			String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_joined_user?event_id=%s",
					mEventId);
			MyAsyncTask task1=new MyAsyncTask(this,baseURL);
			task1.execute();
			
			constant.showProgress(this);
		}
	}
	
/// --------------------------------------- parse response ------------------------------------------
	public void parseResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0")){
					
					JSONArray arr = data.getJSONArray("data");
					for(int i =0;i<arr.length();i++)
					{
						JSONObject obj = arr.getJSONObject(i);
						JMUserSingleObject user = new JMUserSingleObject (obj);
						mUserList.add(user);
					}
				}else
					Toast.makeText(this, "You can not get user list from server ", Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		showUserList();
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
	
	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		parseResponse(data);
		constant.hideProgress();
	}

}
