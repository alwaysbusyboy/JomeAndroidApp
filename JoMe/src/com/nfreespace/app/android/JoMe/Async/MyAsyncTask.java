package com.nfreespace.app.android.JoMe.Async;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.os.AsyncTask;

import com.nfreespace.app.android.JoMe.JSONObjectGetParser;

public class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
	
	/// server communicate using asyncTask
	
	ArrayList<HashMap<String, String>> UploadsList = new ArrayList<HashMap<String, String>>();
	JSONObject 		mResponseData 	= null;
	String     		mBaseURL 	 	= "";
	ServerResponse  mCallback     	= null;
	
	public MyAsyncTask(ServerResponse callback, String url)
	{
		mCallback = callback;
		mBaseURL = url;
	}
	
	@Override
	protected void onPreExecute() {
		
	}
	
	@Override
	protected ArrayList<HashMap<String, String>> doInBackground(
			String... params) {

		// Creating JSON Parser instance
		JSONObjectGetParser jParser = new JSONObjectGetParser();
		// getting JSON string from URL
		mResponseData = jParser.getJSONFromUrl(mBaseURL);  
		
 		return UploadsList;
	}

	@Override
	protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
  
		/// when get response, call parser response  function of the class
		if(mCallback != null)
			mCallback.getResponse(mResponseData);
		super.onPostExecute(result);
	}
}
