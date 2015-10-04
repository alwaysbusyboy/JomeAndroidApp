package com.nfreespace.app.android.JoMe;

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
import android.widget.TextView;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.JMFollowUserListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMUserDetailObject;

public class JMFollowActivity extends Activity implements ServerResponse{

	Button   			vBtnBack;
	ListView 			vUserList;
	TextView            vTitle;
	
	JMUserDetailObject  mUser = null;
	String              mType = "1";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_joinlist);
		
		vBtnBack = (Button)findViewById(R.id.btnCrose);
		vUserList = (ListView)findViewById(R.id.user_list);
		vTitle = (TextView)findViewById(R.id.textView1);
		
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
	       	
				Intent intent = new Intent(JMFollowActivity.this, JMUserProfileActivity.class);
			
				if(mType.equals("1"))
				{
					intent.putExtra("selectUserId", mUser.followedList.get(position).userId);	
				}else
				{
					intent.putExtra("selectUserId", mUser.myFollowingList.get(position).userId);
				}
				
				startActivity(intent);
			}
		});
	}
	
	public void init()
	{
		Intent intent = getIntent();
		String title = intent.getStringExtra("title");
		mType = intent.getStringExtra("type");
		vTitle.setText(title);
	
		getFollowPeopleFS();
	}
	
	public void getFollowPeopleFS()
	{
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_user_data?user_id=%s",
				constant.gUserId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
	public void parserResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONObject object = data.getJSONObject("data");
					mUser = new JMUserDetailObject(object);
					
				}else
				{
					Toast.makeText(this, "You can not get user data from server.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Toast.makeText(this, "You can not get user data from server.", Toast.LENGTH_LONG).show();
		
		showFollow();
	}
	
	public void showFollow()
	{
		if(mType.equals("1"))
		{
			vUserList.setAdapter(new JMFollowUserListAdapter(this, mUser.followedList));
		}else
		{
			vUserList.setAdapter(new JMFollowUserListAdapter(this, mUser.myFollowingList));
		}
	}
	

	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		constant.hideProgress();
		parserResponse(data);
	}

}
