package com.nfreespace.app.android.JoMe;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidhive.pushnotifications.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;
import com.nfreespace.app.android.JoMe.Object.JMChatObject;
import com.nfreespace.app.android.JoMe.Object.JMMessageObject;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(constant.g_SendId);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        constant.displayMessage(context, "Your device registred with GCM");
        ServerUtilities.register(context, "", "", registrationId);
        
        if(constant.g_Callback != null)
        	constant.g_Callback.getRegisterId(registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        constant.displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        
        String pushType = intent.getExtras().getString("push_type");
       
        if(pushType.equals("event"))
        {
        	String message = intent.getExtras().getString("event_name");
        	String sender_name = intent.getExtras().getString("sender_name");
        	String event_id = intent.getExtras().getString("event_id");
        	
        	/// show message
            constant.displayMessage(context, String.format("%s by %s", message,sender_name));
            // notifies user
            generateNotification(context, String.format("%s by %s", message,sender_name),event_id);
            
            
        }else
        {
        	String message = intent.getExtras().getString("message");
            String sendId = intent.getExtras().getString("sender_id");
            String sendName = intent.getExtras().getString("sender_name");
            String Longitude = intent.getExtras().getString("long");
            String Latitude = intent.getExtras().getString("lati");
            String isShort = intent.getExtras().getString("is_short");
            
            /// show message
            constant.displayMessage(context, String.format("%s by %s", message,sendName));
            // notifies user
            generateNotification(context, String.format("%s by %s", message,sendName),null);
            
            if(pushType.equals("chat"))
            {
                String sendTime = intent.getExtras().getString("chat_time");
                
                /// make  push message and show in chat page
                JMChatObject newMessage = new JMChatObject();
                newMessage.message 	= message;
                newMessage.sendId 	= sendId;
                newMessage.sendName = sendName;
                newMessage.sendTime = sendTime;
                newMessage.Longitude= Longitude;
                newMessage.Latitude = Latitude;
                newMessage.isShort 	= isShort;
                
                NotificationService.getInstance().postNotification(constant.gNotificationTag, newMessage);
                
            }else if(pushType.equals("leave") || pushType.equals("reply"))
            {
            	String msgTime = intent.getExtras().getString("msg_time");
            	String leaveMessageId = intent.getExtras().getString("leave_msg_id");
            	String eventId = intent.getExtras().getString("event_id");
            	
            	JMMessageObject newMessage = new JMMessageObject();
            	newMessage.message = message;
            	newMessage.senderId = sendId;
            	newMessage.senderName = sendName;
            	newMessage.latitude = Latitude;
            	newMessage.longitude = Longitude;
            	newMessage.pushType = pushType;
            	newMessage.isShort = isShort;
            	newMessage.messageTime = msgTime;
            	newMessage.leaveMesageId = leaveMessageId;
            	newMessage.eventId = eventId;
            	
            	NotificationService.getInstance().postNotification(constant.gNotificationLeave, newMessage);
            }
        }
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        constant.displayMessage(context, message);
        // notifies user
        generateNotification(context, message,null);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        constant.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        constant.displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message,String eventId) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
       
        Intent notificationIntent = null;
        
        if(eventId != null)
        {
        	 notificationIntent = new Intent(context, JMEventDetailActivity.class);
        	 notificationIntent.putExtra("eventId", eventId);
        }else
        {
        	 notificationIntent = new Intent(context, JMChatHistoryActivity.class);
               
        }
        
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);    
    }
}
