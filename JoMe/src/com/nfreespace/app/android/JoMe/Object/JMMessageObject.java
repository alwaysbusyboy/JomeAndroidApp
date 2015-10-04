package com.nfreespace.app.android.JoMe.Object;

public class JMMessageObject {

	public String message;
	public String senderId;
	public String senderName;
	public String messageTime;
	public String latitude;
	public String longitude;
	public String pushType;
	public String isShort;
	public String eventId;
	public String leaveMesageId;
	
	public JMMessageObject()
	{
		
	}
	
	public JMQuestionObject getQuestionObject()
	{
		JMQuestionObject object = new JMQuestionObject();
		object.message = message;
		object.senderId = senderId;
		object.senderName = senderName;
		object.messageTime = messageTime;
		object.Latitude = latitude;
		object.Longitude = longitude;
		object.messageId = leaveMesageId;		
		return object;
	}
	
	public JMReplyObject getReplyObject()
	{
		JMReplyObject object = new JMReplyObject();
		object.message = message;
		object.senderId = senderId;
		object.senderName = senderName;
		object.messageTime = messageTime;
		object.Latitude = latitude;
		object.Longitude = longitude;
		object.messageId = leaveMesageId;		
		return object;
	}
}
