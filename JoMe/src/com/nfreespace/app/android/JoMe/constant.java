package com.nfreespace.app.android.JoMe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.nfreespace.app.android.JoMe.Object.GetCategory;
import com.nfreespace.app.android.JoMe.Object.JMCacheObject;
import com.nfreespace.app.android.JoMe.Object.JMPhotoObject;

public class constant {

	public static ArrayList<NameValuePair> 	nameValuePairs;
	public static MultipartEntity          	multipartEntity ;
	public static String 					g_CurrentLan = "0";  
	public static String 					g_CurrentLon = "0";
	public static int       				g_timer 	 =  0;
	public static int 						g_Radius 	 = 30;   // radius in setting page
	public static String 					gAdUnitId = "ca-app-pub-1111550515758910/6599349188";
	
	/// user data
	public static String    gUserId = "";
	public static String    gUserName = "";
	
	/// shared preference
	public static String    PREFS_NAME   = "MyPrefsFile";
   
	
	/// event list
	public static ArrayList<GetCategory> gEventlist = new ArrayList<GetCategory>();
	
	/// modify event
	public static boolean   		gIsModify = false;
	public static GetCategory    	gEventDetail;
	
	/// create event   when user creat event,  in other words, from join page to create page.
	public static boolean   gIsCreate = false;
	
	/// user detail page.
	public static List<JMPhotoObject> gPhotoList = null; // when user click small photo in user profile page
	public static List<JMCacheObject> gCacheList = null; // in full photo page, to show progress
	public static boolean  gIsUploaded = false;          // after upload photo successfully, I have to refresh profile page
	
	public static ProgressDialog 	vProgress; 
	
	/// GCM
	public static String                gNotificationLeave  = "newQuestion";
	public static String 				gNotificationTag = "newMessage";
	public static JMCallbackRegister  	g_Callback = null;
	public static String  				g_SendId = "634520261686";
	public static final String 			EXTRA_MESSAGE = "message";
	public static final String 			DISPLAY_MESSAGE_ACTION = "com.nfreespace.app.android.JoMe.DISPLAY_MESSAGE";
	public constant()
	{
		
	}
	
	public  static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentDate()
	{
		Date cDate = new Date();
		String fDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
		return fDate;
	}
	
	public static int getImageId(int type)
	{
		if(type == 0)
		{
			return R.drawable.default_food;
		}else if(type == 1)
		{
			return R.drawable.default_concert;
		}else if(type == 2)
		{
			return R.drawable.default_mingle;
		}else if(type == 3)
		{
			return R.drawable.default_nightlife;
		}else if(type == 4)
		{
			return R.drawable.default_notification;
		}else if(type == 5)
		{
			return R.drawable.default_profile;
		}else if(type == 6)
		{
			return R.drawable.default_social;
		}else if(type == 7)
		{
			return R.drawable.default_sports;
		}else
		{
			return R.drawable.default_tea;
		}
	}
	
	public static int getMapIconId(int type)
	{
		if(type == 0)
		{
			return R.drawable.map_food;
		}else if(type == 1)
		{
			return R.drawable.map_concert;
		}else if(type == 2)
		{
			return R.drawable.map_mingle;
		}else if(type == 3)
		{
			return R.drawable.map_nightlife;
		}else if(type == 4)
		{
			return R.drawable.map_notification;
		}else if(type == 5)
		{
			return R.drawable.map_profile;
		}else if(type == 6)
		{
			return R.drawable.map_social;
		}else if(type == 7)
		{
			return R.drawable.map_sports;
		}else
		{
			return R.drawable.map_tea;
		}
	}
	
	public static int getUserIconId(int type)
	{
		if(type == 0)
		{
			return R.drawable.map_male_icon;
		}else if(type == 2)
		{
			return R.drawable.map_female_icon;
		}else if(type == 3)
		{
			return R.drawable.map_friend_icon;
		}else
		{
			return R.drawable.map_profile;
		}
	}
	
	public static boolean checkPassedTime(String time)
	{
		 Calendar cal = Calendar.getInstance();     

		 int currentMonth = cal.get(Calendar.MONTH)+1;
		 int currentDay   = cal.get(Calendar.DAY_OF_MONTH);
		 int currentYear  = cal.get(Calendar.YEAR);
		 int currentHour  = cal.get(Calendar.HOUR);
		 int currentMin	  = cal.get(Calendar.MINUTE);
		 int currentSec   = cal.get(Calendar.SECOND);
		 
		 double currentSecond = currentSec + currentMin*60 + currentHour*60*60+ currentDay*24*60*60+currentMonth*30*24*60*60+(currentYear-1970)*365*24*60*60;
		 
		 String[] arr = time.split(" ");
		 String[] arr1= arr[0].split("-");
		 String[] arr2= arr[1].split(":");
		 
		 int month = Integer.parseInt(arr1[1]);
		 int year = Integer.parseInt(arr1[0]);
		 int day = Integer.parseInt(arr1[2]);
		 int hour = Integer.parseInt(arr2[0]);
		 int min = Integer.parseInt(arr2[1]);
		 int sec = Integer.parseInt(arr2[2]);
		 
		 double endSecond = sec + min*60 + hour*60*60+ day*24*60*60+month*30*24*60*60+(year-1970)*365*24*60*60;
		 
		 if(endSecond<currentSecond)
		 {
			 return false;
		 }else
		 {
			 return true;
		 }
	}
	
	public static boolean compareTime(String startTime, String endTime)
	{
		 String[] arr = startTime.split(" ");
		 String[] arr1= arr[0].split("-");
		 String[] arr2= arr[1].split(":");
		 
		 int month = Integer.parseInt(arr1[1]);
		 int year = Integer.parseInt(arr1[0]);
		 int day = Integer.parseInt(arr1[2]);
		 int hour = Integer.parseInt(arr2[0]);
		 int min = Integer.parseInt(arr2[1]);
		 int sec = Integer.parseInt(arr2[2]);
		 
		 double startSecond = sec + min*60 + hour*60*60+ day*24*60*60+month*30*24*60*60+(year-1970)*365*24*60*60;
		 
		 String[] brr = endTime.split(" ");
		 String[] brr1= brr[0].split("-");
		 String[] brr2= brr[1].split(":");
		 
		 int eMonth = Integer.parseInt(brr1[1]);
		 int eYear = Integer.parseInt(brr1[0]);
		 int eDay = Integer.parseInt(brr1[2]);
		 int eHour = Integer.parseInt(brr2[0]);
		 int eMin = Integer.parseInt(brr2[1]);
		 int eSec = Integer.parseInt(brr2[2]);
		 
		 double endSecond = eSec + eMin*60 + eHour*60*60+ eDay*24*60*60+eMonth*30*24*60*60+(eYear-1970)*365*24*60*60;
		 
		 if(startSecond>endSecond)
		 {
			 return false;
		 }else
		 {
			 return true;
		 }
	}
	
	public static void alertbox(String title, String mymessage,Context context)
	{
	   new AlertDialog.Builder(context)
	   .setMessage(mymessage)
	   .setTitle(title)
	   .setCancelable(true)
	   .setNeutralButton("OK",
			new DialogInterface.OnClickListener() {
		   		public void onClick(DialogInterface dialog, int whichButton){}
	       	})
	   .show();
	}
	
	public static void showProgress(Context context)
	 {
		 vProgress = new ProgressDialog(context);
		 vProgress.setMessage("Loading....please wait ");
		 vProgress.setCancelable(true);
		 vProgress.show();
	 }
			
	 public static void hideProgress()
	 {
		 if(vProgress != null)
		 {
			 vProgress.dismiss();
		 }
	 }
	 
	 public static void QuitDialog(String message, String okStr, String cancelStr,final Context context)
	 {
		 // custom dialog
		 final Dialog dialog = new Dialog(context);
		 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialog.getWindow().setBackgroundDrawableResource(R.drawable.empty);
		 dialog.setContentView(R.layout.dialog_input_token);
		 dialog.setCanceledOnTouchOutside(true);
			
		 TextView title = (TextView)dialog.findViewById(R.id.title);
		 title.setText(message);
		 Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
		 okBtn.setText(okStr);
		 okBtn.setOnClickListener(new View.OnClickListener() {
		       	 
			 @Override
			 public void onClick(View view) {		
				 // go to home screen of device
				 Intent intent = new Intent(Intent.ACTION_MAIN);
				 intent.addCategory(Intent.CATEGORY_HOME);
				 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 context.startActivity(intent);
				 dialog.dismiss();
			 }
		 });
			
		 Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		 cancelBtn.setText(cancelStr);
		 // if button is clicked, close the custom dialog
		 cancelBtn.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {	
				 dialog.dismiss();
			 }
		 });
		 dialog.show();  
	 }
}
