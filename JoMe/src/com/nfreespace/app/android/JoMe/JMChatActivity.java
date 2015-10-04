package com.nfreespace.app.android.JoMe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.JMChatListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMChatObject;

public class JMChatActivity extends Activity implements ServerResponse{

	Button     			vBtnSend,vBtnBack;
	EditText   			vEdtMessag;
	ListView   			vChatList;
	
	JMChatListAdapter   mAdapter;
	List<JMChatObject> 	mChatList = new ArrayList<JMChatObject>();
	String     			mOtherUserId,mLat,mLong;
	boolean    			mIsLastChat = false, mIsSendMessage = false;
	
	private final Observer receivedNotification = new Observer()
	{
		@Override
		public void update(Observable observable, Object data) {
			// TODO Auto-generated method stub
			JMChatObject pushInfo = (JMChatObject)data;
			showNewMessage(pushInfo);
	   }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		NotificationService.getInstance().addObserver(constant.gNotificationTag, receivedNotification);
		
		vBtnBack 	= (Button)findViewById(R.id.btnCrose);
		vBtnSend 	= (Button)findViewById(R.id.message_send);
		vEdtMessag 	= (EditText)findViewById(R.id.message_edit);
		vChatList 	= (ListView)findViewById(R.id.message_list);
		
		init();
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vBtnSend.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkMessage();
			}
		});
	}
	
	public void init()
	{
		Intent intent = getIntent();
		mOtherUserId = intent.getStringExtra("receiveUserId");
		
		getLastChatListFS();
	}
	
	public void test()
	{
		JMChatObject obj1= new JMChatObject();
		JMChatObject obj2= new JMChatObject();
		
		obj1.message = "Hi, How are you?";
		obj1.sendId = constant.gUserId;
		obj1.sendTime = "2014-08-22";
		
		obj2.message = "Fine thanks,and you?";
		obj2.sendId =  "31";
		obj2.sendTime = "2014-08-22";
		
				
		mChatList.add(obj1);
		mChatList.add(obj2);
		
		showChatList();
	}
	
/// ------------------------------------------ check  available message  ------------
	public void checkMessage()
	{
		String message = vEdtMessag.getText().toString();
		if(!message.equals(""))
		{
			JMChatObject chatItem = new JMChatObject();
			chatItem.message = message;
			chatItem.sendId = constant.gUserId;
			chatItem.receiveId = mOtherUserId;
			chatItem.sendTime = constant.getCurrentDate();
			if(getCurrentPos())
			{
				chatItem.Latitude = mLat;
				chatItem.Longitude = mLong;
			}
			
			mChatList.add(chatItem);
			mAdapter.notifyDataSetChanged();
			vChatList.setSelection(mChatList.size()-1);
			
			sendMessageToUser(message);
			vEdtMessag.setText("");
		}
	}
	
/// ----------------------------------------------- send message to other user --------------	
	public void sendMessageToUser(String msg)
	{
		String message = "";
		try {
			message = URLEncoder.encode(msg,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mIsSendMessage = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/send_chat?sender_id=%s&recver_id=%s&message=%s&long=%s&lati=%s",
				constant.gUserId,mOtherUserId,message,mLong,mLat);
	
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
/// ------------------------------------------ show Chat list ----------------------------------
	public void showChatList()
	{
		mAdapter = new JMChatListAdapter(this, mChatList);
		vChatList.setAdapter(mAdapter);
		vChatList.setSelection(mChatList.size()-1);
	}
	
/// ------------------------------------------ get last chat history from server -------------------- 	
	public void getLastChatListFS()
	{
		mIsLastChat = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_last_chat?user_id=%s&peer_id=%s",
				constant.gUserId,mOtherUserId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
/// ------------------------------------------ parse Last chat history -----------------------	
	public void parseLastChat(JSONObject data)
	{
		mChatList = new ArrayList<JMChatObject>();
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
							JMChatObject messageItem = new JMChatObject(obj);
							mChatList.add(messageItem);
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
		
		/// reverse chat list
		Collections.reverse(mChatList);
		showChatList();
	}
	
/// ------------------------------------------ parse send message ------------------
	public void parseSendChat(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(!isSuccess.equals("0"))
				{
					Toast.makeText(this, "You can not send message.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "You can not send message.", Toast.LENGTH_LONG).show();
		}
	}
///------------------------------------------- When push get, show message --------------------- 	
	public void showNewMessage(JMChatObject message)
	{
		if(message != null)
		{
			if(message.isShort.equals("0"))
			{
				mChatList.add(message);
				
				this.runOnUiThread(new Runnable() {
				        @Override
				        public void run() {
				         mAdapter.notifyDataSetChanged();
				         vChatList.setSelection(mChatList.size()-1);
				      }
				});
			}else
			{
				getLastChatListFS();
			}
		}
	}
	
/// ------------- get current position ----------------------
	public boolean getCurrentPos()
	{
		GPSTracker mGPS = new GPSTracker(this);
		
		if(mGPS.canGetLocation ){
			mGPS.getLocation();
			Double m_fLat = (double)mGPS.getLatitude();
			Double m_fLon = (double)mGPS.getLongitude();
			
			mLat = m_fLat.toString();
			mLong = m_fLon.toString();
			
			return true;
		}
		return false;
	}
	
	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		constant.hideProgress();
		
		if(mIsLastChat)
		{
			mIsLastChat = false;
			parseLastChat(data);
		}else if(mIsSendMessage)
		{
			mIsSendMessage = false;
			parseSendChat(data);
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		NotificationService.getInstance().removeObserver(constant.gNotificationTag, receivedNotification);
	}

}
