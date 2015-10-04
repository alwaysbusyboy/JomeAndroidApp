package com.nfreespace.app.android.JoMe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.JMEventMessageListAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMMessageObject;
import com.nfreespace.app.android.JoMe.Object.JMQuestionObject;
import com.nfreespace.app.android.JoMe.Object.JMReplyObject;

public class JMEventMessagesActivity extends Activity implements ServerResponse,JMCallbackReply{

	Button 					vBtnBack,vBtnUpload;
	EditText    			vEdtMessage;
	ExpandableListView 		vMessageList;
	
	String      			mEventId,mLat,mLong;
	boolean     			mIsGetMessage = false, mIsLeave = false, mIsReply = false;
	
	List<JMQuestionObject> 	mMessageList;
	JMQuestionObject 		mLeaveMessage;
	
	JMEventMessageListAdapter mAdapter;
	
	private final Observer receivedNotification = new Observer()
	{
		@Override
		public void update(Observable observable, Object data) {
			// TODO Auto-generated method stub
			JMMessageObject pushInfo = (JMMessageObject)data;
			showNewLeaveMessage(pushInfo);
	   }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_message);
		
		NotificationService.getInstance().addObserver(constant.gNotificationLeave, receivedNotification);
		
		vBtnBack 		= (Button)findViewById(R.id.btnCrose);
		vBtnUpload 		= (Button)findViewById(R.id.btnPost);
		vEdtMessage 	= (EditText)findViewById(R.id.message_edit);
		vMessageList 	= (ExpandableListView)findViewById(R.id.messages_list);
		
		init();
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vBtnUpload.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkMessage();
			}
		});
		
		// Listview Group click listener
		vMessageList.setOnGroupClickListener(new OnGroupClickListener() {
		  
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				return false;
			}
		});
		
		// Listview Group expanded listener
		vMessageList.setOnGroupExpandListener(new OnGroupExpandListener() {
		  
			@Override
			public void onGroupExpand(int groupPosition) {
				
			}
		});
		  
		// Listview Group collasped listener
		vMessageList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
			
			}
		});
		  
		// Listview on child click listener
		vMessageList.setOnChildClickListener(new OnChildClickListener() {
		  
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				
				return false;
			}
		});
	}	
	
	public void init()
	{
		Intent intent = getIntent();
		mEventId = intent.getStringExtra("eventId");
		
		getMessagesFS();
	}
	
	public void test()
	{
		JMQuestionObject obj1 = new JMQuestionObject();
		obj1.senderName = "Jang HuJong";
		obj1.messageTime = "2014-8-22";
		obj1.message = "What is this event?";
		obj1.replyList = new ArrayList<JMReplyObject>();
		
		JMReplyObject obj2 = new JMReplyObject();
		obj2.senderName = "Jin Guang";
		obj2.message = "This evevt is excellent";
		obj2.messageTime = "2014-8-23";
		
		JMReplyObject obj3 = new JMReplyObject();
		obj3.senderName = "Chui Guang";
		obj3.message = "This evevt is cool";
		obj3.messageTime = "2014-8-23";
		
		obj1.replyList.add(obj2);
		obj1.replyList.add(obj3);
		
		mMessageList = new ArrayList<JMQuestionObject>();
		mMessageList.add(obj1);
		
		showMessagesList();
	}
	
/// ----------------------------------- show messages list -------------------------------
	public void showMessagesList()
	{
		mAdapter = new JMEventMessageListAdapter(this,this, mMessageList);
		vMessageList.setAdapter(mAdapter);
	}
	
/// ------------------------------------ when push message get , show leave message ---------	
	public void showNewLeaveMessage(JMMessageObject message)
	{
		if(message.isShort.equals("0") && message.eventId.equals(mEventId))
		{
			if(message.pushType.equals("leave"))
			{
				JMQuestionObject object = message.getQuestionObject();
				mMessageList.add(0,object);
			}else
			{
				JMReplyObject object = message.getReplyObject();
				addReplyMessageToList(object, message.leaveMesageId);
			}
			
			this.runOnUiThread(new Runnable() {
			        @Override
			        public void run() {
			         mAdapter.notifyDataSetChanged();
			         vMessageList.setSelection(0);
			      }
			});
		}else
		{
			getMessagesFS();
		}
	}
	
	public void addReplyMessageToList(JMReplyObject object, String leaveMessageId)
	{
		for(int i=0;i<mMessageList.size();i++ )
		{
			if(mMessageList.get(i).messageId.equals(leaveMessageId))
			{
				mMessageList.get(i).replyList.add(object);
			}
		}
	}
	
/// ------------------------------------ when user click reply button -------------------
	public void replyMessageToQuestion(int pos)
	{
		replyDialog(pos);
	}
	
	public void sendReplyMessage(String msg,String leaveMessageId)
	{
		String message = "";
		try {
			message = URLEncoder.encode(msg,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mIsReply = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/reply_event_message?user_id=%s&event_id=%s&leave_msg_id=%s&message=%s&long=%s&lati=%s",
				constant.gUserId,mEventId,leaveMessageId,message,mLong,mLat);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
/// ------------------------------------ check available message --------------------------
	public void checkMessage()
	{
		String message = vEdtMessage.getText().toString();
		if(!message.equals(""))
		{
			mLeaveMessage = new JMQuestionObject();
			mLeaveMessage.message = message;
			mLeaveMessage.senderId = constant.gUserId;
			mLeaveMessage.senderName = constant.gUserName;
			mLeaveMessage.messageTime = constant.getCurrentDate();
			
			if(getCurrentPos())
			{
				mLeaveMessage.Latitude = mLat;
				mLeaveMessage.Longitude = mLong;
			}
			
			sendLeaveMessageTS(message);
			
			vEdtMessage.setText("");
		}
	}
	
/// ------------------------------------------------- message ------------------	
	public void sendLeaveMessageTS(String msg)
	{
		String message = "";
		try {
			message = URLEncoder.encode(msg,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mIsLeave = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/leave_event_message?user_id=%s&event_id=%s&message=%s&long=%s&lati=%s",
				constant.gUserId,mEventId,message,mLong,mLat);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
/// ----------------------------------- get message list from server -----------------------	
	public void getMessagesFS()
	{
		mIsGetMessage = true;
		String baseURL = String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/get_event_message?event_id=%s",
				mEventId);
		MyAsyncTask task1=new MyAsyncTask(this,baseURL);
		task1.execute();
		
		constant.showProgress(this);
	}
	
/// ------------------------------------------- parse message list -----------------------	
	public void parseMessage(JSONObject data)
	{
		mMessageList = new ArrayList<JMQuestionObject>();	
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
							JMQuestionObject messageObj = new JMQuestionObject(obj);
							mMessageList.add(messageObj);
						}
					}
				}else
				{
					Toast.makeText(this, "There is no messages", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "You can not get messages.", Toast.LENGTH_LONG).show();
		}
		
		showMessagesList();
	}
	
/// --------------------------------------- when leave message send to server, parse response -------	
	public void parseLeave(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(isSuccess.equals("0"))
				{
					JSONObject jObj = data.getJSONObject("data");
					String messageId = jObj.getString("message_id");
					mLeaveMessage.messageId = messageId;
					
					mMessageList.add(0,mLeaveMessage);
					mAdapter.notifyDataSetChanged();
				}else
				{
					Toast.makeText(this, "You can not leave messages to this event.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "You can not leave messages to this event.", Toast.LENGTH_LONG).show();
		}
	}
	
	public void parseReply(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				if(!isSuccess.equals("0"))
				{
					Toast.makeText(this, "You can not reply to this message.", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else
		{
			Toast.makeText(this, "You can not reply to this message.", Toast.LENGTH_LONG).show();
		}
	}
///------------------------------------------------ reply dialog ----------------------------	
	public void replyDialog(final int pos)
	{
		// custom dialog
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_reply);
		dialog.setCanceledOnTouchOutside(true);
		
		final EditText messageEdt = (EditText)dialog.findViewById(R.id.reply_edit);
		
		Button replyBtn = (Button) dialog.findViewById(R.id.replyBtn);
		
		replyBtn.setOnClickListener(new View.OnClickListener() {
		       	 
			@Override
			public void onClick(View view) {		
				// go to home screen of device
				String replyMessage = messageEdt.getText().toString();
				if(!replyMessage.equals(""))
				{
					JMReplyObject replyItem = new JMReplyObject();
					replyItem.senderId = constant.gUserId;
					replyItem.senderName = constant.gUserName;
					replyItem.message = replyMessage;
					replyItem.messageTime = constant.getCurrentDate();
					if(getCurrentPos())
					{
						replyItem.Latitude = mLat;
						replyItem.Longitude = mLong;
					}
					
					mMessageList.get(pos).replyList.add(replyItem);
					mAdapter.notifyDataSetChanged();
					sendReplyMessage(replyMessage,mMessageList.get(pos).messageId);
				}
				dialog.dismiss();
			 }
		 });
	
		dialog.show();  
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
		if(mIsGetMessage)
		{
			mIsGetMessage = false;
			parseMessage(data);
		}else if(mIsLeave)
		{
			mIsLeave = false;
			parseLeave(data);
		}else if(mIsReply)
		{
			mIsReply = false;
			parseReply(data);
		}
			
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		NotificationService.getInstance().removeObserver(constant.gNotificationLeave, receivedNotification);
	}

	@Override
	public void replyMessage(int position) {
		// TODO Auto-generated method stub
		replyMessageToQuestion(position);
	}


}
