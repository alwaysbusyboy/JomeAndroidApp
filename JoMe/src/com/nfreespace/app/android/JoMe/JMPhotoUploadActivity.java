package com.nfreespace.app.android.JoMe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class JMPhotoUploadActivity extends Activity {

	Button     		vBtnBack, vBtnCamera;
	ImageView  		vCaptureImg;
	
	Bitmap    		mBitmap;
	String          mUserId;
	String          fileName;
	String          mCurrentPhotoPath;
	Uri             imageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_upload);
		
		vBtnBack 		= (Button) findViewById(R.id.btnCrose);
		vBtnCamera 		= (Button) findViewById(R.id.btnCamera);
		vCaptureImg 	= (ImageView) findViewById(R.id.photo_view);
		
		Intent in=getIntent();
		mUserId = in.getStringExtra("user_id");
		
		CallCameraAndGallery();
		
		vBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		vBtnCamera.setOnClickListener(new View.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMPhotoUploadActivity.class);
				create.putExtra("user_id", mUserId);
				startActivity(create);
				finish();
			}
		});
	}

/// ------------------------------------------------------- save photo to file -------------------------	
	 private File createImageFile() throws IOException {
	     /// Create an image file name		
		 String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	     String imageFileName = "PNG_" + timeStamp + "_";
	     File storageDir = Environment.getExternalStoragePublicDirectory(
	             Environment.DIRECTORY_PICTURES);
	     File image = File.createTempFile(
	         imageFileName,  /* prefix */
	         ".png",         /* suffix */
	         storageDir      /* directory */
	     );	    
	     
	     fileName=image.getAbsolutePath();
	     /// Save a file: path for use with ACTION_VIEW intents
	     mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	     return image;
	 }
	 
	 static final int REQUEST_TAKE_PHOTO = 0;

/// --------------------------------------------------- call quality camera -----------------------	 
	 private void dispatchTakePictureIntent() {
	     Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);	    
	     if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	     		/// Create the File where the photo should go
	     		File photoFile = null;
	     		try {
	     			photoFile = createImageFile();
	     		} catch (IOException ex) {
	     			// Error occurred while creating the File	            
	     		}	
	     		/// Continue only if the File was successfully created
	     		if (photoFile != null) {
	     			imageUri= Uri.fromFile(photoFile);
	     			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	     					Uri.fromFile(photoFile));
	     			startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	     		}
	     }
	 }
	 
/// ------------------------------------------- call camera -------------------------------	
	public void callCamera()
	{
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO); 
	}
	
/// ------------------------------------------- call gallery ------------------------------	
	final int RESULT_LOAD_IMAGE = 1;
	public void callGallery()
	{
		Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
/// ------------------------------------------- get bitmap from camera -------------------------------------	 
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		 if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {  
			 
			 Bitmap bgBitmap=null;
			 try{
				 bgBitmap=BitmapFactory.decodeFile(fileName); 
			 }catch(Exception e)
			 {
				 // show dialog for error
			 }
			   
			 if(bgBitmap!=null)
			 {
				 int wid=bgBitmap.getWidth();
				 int hei=bgBitmap.getHeight();
				 if(wid>hei)
				 {
					 Bitmap bmp=getRotateBitmap(bgBitmap);					
					 bgBitmap=bmp;
					 bmp=null;
				 }
				
				 mBitmap=Bitmap.createScaledBitmap(bgBitmap,bgBitmap.getWidth()/4,bgBitmap.getHeight()/4, false);;
				 if(mBitmap != null)
					 vCaptureImg.setImageBitmap(mBitmap);           
				 ConfirmDialog();
					           
				 File file=new File(fileName);
				 file.delete();
			 }
		 }else if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK)
		 {
			 Uri selectedImage = data.getData();
			 String[] filePathColumn = { MediaStore.Images.Media.DATA };
			 Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			 cursor.moveToFirst();
			 int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			 String picturePath = cursor.getString(columnIndex);
			 cursor.close();             
			 Bitmap bgBitmap  =BitmapFactory.decodeFile(picturePath);
			 
			 if(bgBitmap != null)
			 {
				 int wid=bgBitmap.getWidth();
				 int hei=bgBitmap.getHeight();
				 if(wid>hei)
				 {
					 Bitmap bmp=getRotateBitmap(bgBitmap);					
					 bgBitmap=bmp;
					 bmp=null;
				 }
				 
				 mBitmap=Bitmap.createScaledBitmap(bgBitmap,bgBitmap.getWidth()/4,bgBitmap.getHeight()/4, false);;
				 if(mBitmap != null)
					 vCaptureImg.setImageBitmap(mBitmap);           
				 ConfirmDialog();
			 }
		 }
			 
	 }
/// --------------------------------------------- rotate bitmap ------------------------------	 
	 public Bitmap getRotateBitmap(Bitmap bitmap)
	 {
		 Bitmap rotateBitmap=null;
		 Matrix mat = new Matrix();
		 mat.postRotate(90);
		 try{
			 rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), mat, false);
		 }catch(Exception e)
		 {
			 Log.e("Photo Error", e.toString());
		 }
		 return rotateBitmap;
	 }
/// --------------------------------- upload photo to server ------------------------------
	 public void uploadPhotoTS()
	 {
		 if(mBitmap != null)
		 {
			 writeFileToInternalStorage(mBitmap,"upload_photo.jpg");
			 File photoFile = getFileStreamPath("upload_photo.jpg");
			 
			 constant.multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	         
			 try {
				constant.multipartEntity.addPart("user_id", new StringBody(mUserId));
				constant.multipartEntity.addPart("name", new StringBody("photo.jpg"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			constant.multipartEntity.addPart("media", new FileBody(photoFile));
			
			MyAsyncTask1 task1=new MyAsyncTask1("http://freelancer.nfreespace.com/event_proj/index.php/api/add_photo");
			task1.execute();
			finish();
		 }
	 }
	 
/// ------------------------------------- save photo and get photo ----------------------------
	 public String writeFileToInternalStorage(Bitmap outputImage,String fname) {
	        String fileName=fname;       
	        FileOutputStream fos = null;
			try {
				fos = this.openFileOutput(fileName, Context.MODE_PRIVATE);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        outputImage.compress(CompressFormat.JPEG, 100, fos);
	        try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return fileName;
	 } 	 
/// ---------------------------------------- call camera or gallery from dialog-----------------------	 
	 public void CallCameraAndGallery()
	 {
		 // custom dialog
		 final Dialog dialog = new Dialog(this);
		 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialog.setContentView(R.layout.dialog_input_token);
		 dialog.setCanceledOnTouchOutside(true);
			
		 TextView  title = (TextView)dialog.findViewById(R.id.title);
		 title.setText("Please select camera or gallery.");
		 Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
		 okBtn.setText("Camera");
		 okBtn.setOnClickListener(new View.OnClickListener() {
		       	 
			 @Override
			 public void onClick(View view) {			
				 dispatchTakePictureIntent();
				 dialog.dismiss();
			 }
		 });
			
		 Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		 cancelBtn.setText("Gallery");
		 cancelBtn.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {	
				 callGallery();
				 dialog.dismiss();
			 }
		 });
		 dialog.show();  
	 }
 
/// --------------------------------------------- show dialog --------------------------------
	 public void ConfirmDialog()
	 {
		 // custom dialog
		 final Dialog dialog = new Dialog(this);
		 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 dialog.setContentView(R.layout.dialog_input_token);
		 dialog.setCanceledOnTouchOutside(true);
			
		 TextView  title = (TextView)dialog.findViewById(R.id.title);
		 title.setText("Do you want to upload image?");
		 Button okBtn = (Button) dialog.findViewById(R.id.okBtn);
		 okBtn.setText("Upload");
		 okBtn.setOnClickListener(new View.OnClickListener() {
		       	 
			 @Override
			 public void onClick(View view) {			
				 uploadPhotoTS();
				 dialog.dismiss();
			 }
		 });
			
		 Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
		 // if button is clicked, close the custom dialog
		 cancelBtn.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {	
				 dialog.dismiss();
			 }
		 });
		 dialog.show();  
	}
	
/// ----------------------------------------------- parser reponse ------------------------------------	 
	public void parseResponse(JSONObject data)
	{
		if(data != null)
		{
			try {
				String isSuccess = data.getString("error");
				String message = data.getString("message");
				if(isSuccess.equals("0"))
				{
					Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
					constant.gIsUploaded = true;
				} 
				else
					Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else
			Toast.makeText(this, "You can not connect to server", Toast.LENGTH_SHORT).show();
	}
	
/// ---------------------------------- server  async task --------------------------------
	public class MyAsyncTask1 extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
			
		/// server communicate using asyncTask
			
		ArrayList<HashMap<String, String>> UploadsList = new ArrayList<HashMap<String, String>>();
		JSONObject 		mResponseData 	= null;
		String     		mBaseURL 	 	= "";
			
		public MyAsyncTask1(String url)
		{
			mBaseURL = url;
		}
			
		@Override
		protected void onPreExecute() {
				
		}
			
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(
				String... params) {
			
			// Creating JSON Parser instance
			JSONImageParser jParser = new JSONImageParser();
			// getting JSON string from URL
			mResponseData = jParser.getJSONFromUrl(mBaseURL);  
				
			return UploadsList;
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			
			parseResponse(mResponseData);
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
}
