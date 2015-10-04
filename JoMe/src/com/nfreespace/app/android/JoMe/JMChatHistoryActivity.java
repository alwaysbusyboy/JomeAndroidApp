package com.nfreespace.app.android.JoMe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nfreespace.app.android.JoMe.Adapter.JMHistoryAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMPeerObject;

public class JMChatHistoryActivity extends Activity implements ServerResponse{

	ListView 		vHistoryList;
	Button 	 		vBtnHome,vBtnProfile;
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
	
	List<JMPeerObject> mHistoryList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_history);
		
		vBtnHome 	= (Button) findViewById(R.id.btnHome);
		vBtnProfile = (Button) findViewById(R.id.btnInvitationManager);
		vHistoryList = (ListView) findViewById(R.id.history_list);
		
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
		
		vHistoryList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	        
				Intent intent = new Intent(getApplicationContext(), JMChatActivity.class);
				intent.putExtra("receiveUserId", mHistoryList.get(position).userId);
				startActivity(intent);
			
			}
		});
	}
	
	public void init()
	{
		SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		constant.gUserId= settings.getString("createUserId", "");
		String  firstName = settings.getString("userFirstName", "");
		String  lastName = settings.getString("userLastName", "");
		constant.gUserName = String.format("%s %s", firstName,lastName);
		
		getHistoryFS();
	}
	
	public void showChatHistory()
	{
		if(mHistoryList != null)
		{
			vHistoryList.setAdapter(new JMHistoryAdapter(this, mHistoryList));
		}
	}
	
	public void getHistoryFS()
	{
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_peer_list?user_id=%s",
				constant.gUserId);
	
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
	public void parseResponse(JSONObject data)
	{
		mHistoryList = new ArrayList<JMPeerObject>();
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONArray arr = data.getJSONArray("data");
					
					if(arr != null)
					{
						for(int i=0;i<arr.length();i++)
						{
							JSONObject obj = arr.getJSONObject(i);
							JMPeerObject messageItem = new JMPeerObject(obj);
							mHistoryList.add(messageItem);
						}
					}
				}else
				{
					Toast.makeText(this, "You can not get chat history.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "You can not get chat history.", Toast.LENGTH_LONG).show();
		}
		
		showChatHistory();
	}
	
	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		constant.hideProgress();
		parseResponse(data);
	}

}
