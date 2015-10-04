package com.nfreespace.app.android.JoMe;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nfreespace.app.android.JoMe.Adapter.JMPhotoFullAdapter;
import com.nfreespace.app.android.JoMe.Async.MyAsyncTask;
import com.nfreespace.app.android.JoMe.Async.ServerResponse;
import com.nfreespace.app.android.JoMe.Object.JMPhotoObject;

public class JMPhotoShowActivity extends Activity implements ServerResponse {

	Button     			vBtnBack,vBtnDelete;
	ViewPager 			vViewPager;
	ProgressBar			vProgress;
	
	int 				mSelectPos;
	String          	mUserId;
	List<JMPhotoObject> mPhotoList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_show);
		
		vBtnBack 		= (Button) findViewById(R.id.btnCrose);
		vBtnDelete      = (Button) findViewById(R.id.btnDelete);
		vViewPager      = (ViewPager) findViewById(R.id.view_pager);
		vProgress       = (ProgressBar) findViewById(R.id.progress_view);

		init();
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vBtnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int pos = vViewPager.getCurrentItem();
				deletePhoto(mPhotoList.get(pos).mPhotoId);
			}
		});
		
	}
	
	public void init()
	{
		mPhotoList 	= constant.gPhotoList;
		Intent in 	= getIntent();
		mUserId   	= in.getStringExtra("userid");
		mSelectPos 	= Integer.parseInt(in.getStringExtra("selectpos"));
		
		if(mUserId.equals(constant.gUserId))
			vBtnDelete.setVisibility(View.VISIBLE);
		else
			vBtnDelete.setVisibility(View.GONE);
		
		showPhoto();
	}
	
/// ------------------------------ photo to view pager -----------------------------------------	
	public void showPhoto()
	{
		JMPhotoFullAdapter adapter = new JMPhotoFullAdapter(this,vProgress,mPhotoList);
		vViewPager.setAdapter(adapter);
		vViewPager.setCurrentItem(mSelectPos);
	}
	
/// ------------------------------- delete photo -------------------------------------------------	
	public void deletePhoto(String imageId)
	{
		if(imageId != null)
		{
			String baseURL= String.format("http://freelancer.nfreespace.com/event_proj/index.php/api/del_photo?user_id=%s&photo_id=%s",constant.gUserId,imageId);
			MyAsyncTask task1=new MyAsyncTask(this,baseURL);
			task1.execute();
			constant.showProgress(this);
		}
	}
/// -------------------------------- get response ------------------------------------------------
	@Override
	public void getResponse(JSONObject data) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Delete done", Toast.LENGTH_LONG).show();
		constant.hideProgress();
		
		Intent create = new Intent(getApplicationContext(),JMUserProfileActivity.class);
		startActivity(create);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
}
