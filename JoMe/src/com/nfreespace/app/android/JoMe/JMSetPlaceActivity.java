package com.nfreespace.app.android.JoMe;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class JMSetPlaceActivity extends FragmentActivity implements LocationListener {
	
	
	ListView 		vNearbylist;
	TextView 		vLongitude,vLatitude;
	Button 	 		vBtnCross,vBtnSearch,vSetPlaceBtn,vMapTypeBtn,vBtnHome,vBtnProfile,vBtnHistory;    
	
	LatLng   		mPoint;
	MarkerOptions 	markerOptions;
	Marker        	mKiel,mSearchMarker;
	
	String			mCoordl1, mCoordl11 ,mCoordl2, mCoordl22;
	String 			mProvider;
	
    GoogleMap 		map;
	LocationManager mLocationManager;

	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
   
    double 			mLat = 0, mLon = 0;
    boolean 		mFMapType = false;

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_venue);
		
		vNearbylist		= (ListView) findViewById(R.id.nearbylist);
		vBtnHome 		= (Button) findViewById(R.id.btnHome);
		vBtnCross 		= (Button) findViewById(R.id.btnCrose);
		vSetPlaceBtn 	= (Button) findViewById(R.id.set_place);
		vMapTypeBtn  	= (Button) findViewById(R.id.map_type_btn);
		vBtnProfile 	= (Button) findViewById(R.id.btnInvitationManager);
		vBtnSearch      = (Button) findViewById(R.id.btn_find);
		vBtnHistory 	= (Button) findViewById(R.id.btnMessage);
		vLongitude 	    = (TextView)findViewById(R.id.tv_longitude);
		vLatitude 		= (TextView)findViewById(R.id.tv_latitude);
		
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
			    
		vBtnCross.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 
				 boolean flag = constant.gIsModify;
				 constant.gIsModify = false;
				 if(flag)
				 {
					 Intent itCrose = new Intent(getApplicationContext(),JMEventModifyActivity.class);
					 startActivity(itCrose);
				 }else
				 {
					 Intent itCrose = new Intent(getApplicationContext(),JMEventCreateActivity.class);
					 startActivity(itCrose);
				 }
				 finish();
			}
		});
		
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
		
	    vBtnHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent create = new Intent(getApplicationContext(),JMChatHistoryActivity.class);
				startActivity(create);
			}
		});
		
		vMapTypeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if(map != null)
				{
					if(mFMapType)
					{
						map.setMapType(GoogleMap.MAP_TYPE_NORMAL); 
						mFMapType = false;
					}else
					{
						map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); 
						mFMapType = true;
					}
				}
			}
		});
		
		vSetPlaceBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(mLat == 0 && mLon == 0)
				{
					Toast.makeText(JMSetPlaceActivity.this, "Please select a location on map", Toast.LENGTH_LONG);
				}else
				{
					String latitude = String.valueOf(mLat);
		  		    String longitude = String.valueOf(mLon);
		  		    
		  		    SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		  		    SharedPreferences.Editor editor = settings.edit();
		  		    editor.putString("tvLatitudes", latitude);
		  		    editor.putString("tvLongitudes",longitude); 
		  		    editor.commit();
		  		    
		  		    if(constant.gIsModify)
		  		    {
		  		    	Intent itCrose = new Intent(getApplicationContext(),JMEventModifyActivity.class);
						startActivity(itCrose);
		  		    }else
		  		    {
		  		    	Intent itCrose = new Intent(getApplicationContext(),JMEventCreateActivity.class);
						startActivity(itCrose);
		  		    }
		  		    
		  		    finish();
		  		}
			}
		});
		
		/// ------------ Defining button click event listener for the find button ------------
  		OnClickListener findClickListener = new OnClickListener() {           
  			@Override
  			public void onClick(View v) {
  				// Getting reference to EditText to get the user input location
  				EditText etLocation = (EditText) findViewById(R.id.et_location);
  				
  				// Getting user input location
  				String location = etLocation.getText().toString();
  				
  				if(location!=null && !location.equals("")){
  					new GeocoderTask().execute(location);
  				}
  			}
  		};
  		        
  		vBtnSearch.setOnClickListener(findClickListener);   
  		
  	    /// ----------------- end ------------------------------------
  	

		/// ---------------- map initialize --------------------------------------------------
		
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
  		
        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

  		// Creating a criteria object to retrieve provider
  		Criteria criteria = new Criteria();

  		// Getting the name of the best provider
  		mProvider = mLocationManager.getBestProvider(criteria, true);
  				
  		if(mProvider!=null && !mProvider.equals("")){
  		           
  			// Get the location from the given provider
  			Location location = mLocationManager.getLastKnownLocation(mProvider);
  			mLocationManager.requestLocationUpdates(mProvider, 20000, 1, this);	 
  			
  			if (location != null) 
  				onLocationChanged(location);
  			else
  				Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
  		}else{
  			Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
  		} 
  		
  	
  		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				if (mKiel != null) {
					mKiel.remove();
  				}
				
				if(mSearchMarker != null)
				{
					mSearchMarker.remove();
				}
				
				mKiel = map.addMarker(new MarkerOptions()
  					.position( new LatLng(arg0.latitude, arg0.longitude))
  					.draggable(true).visible(true));
  		      
  		        // Setting Current Longitude
  				vLongitude.setText("Longitude:" + arg0.longitude);
  		        
  		        // Setting Current Latitude
  				vLatitude.setText("Latitude:" + arg0.latitude );
  		          
  		        mLon=arg0.longitude;
  		        mLat=arg0.latitude;
			}
		});
  		
  		map.setOnMarkerClickListener(new OnMarkerClickListener(){

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				
				 mLon=marker.getPosition().longitude;
	  		     mLat=marker.getPosition().latitude;
	  		        
	  		     // Setting Current Longitude
	  		     vLongitude.setText("Longitude:" + mLon);
	  		        
	  		     // Setting Current Latitude
	  		     vLatitude.setText("Latitude:" +  mLat );
				 return false;
			}
		});
  		
  		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

  			@Override
  			public void onMapLongClick(LatLng arg0) {
  				if (mKiel != null) {
  					mKiel.remove();
  				}
  				
  				if(mSearchMarker != null)
				{
  					mSearchMarker.remove();
				}  				
  			   
  				mKiel = map.addMarker(new MarkerOptions()
  					.position( new LatLng(arg0.latitude, arg0.longitude))
  					.draggable(true).visible(true));
  		            
  				// Setting Current Longitude
  				vLongitude.setText("Longitude:" + arg0.longitude);
  		        
  		        // Setting Current Latitude
  				vLatitude.setText("Latitude:" + arg0.latitude );
  		          
  		        mLon=arg0.longitude;
  		        mLat=arg0.latitude;
  			}
  		});
    }
	
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;
            
			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
			} catch (IOException e) {
				e.printStackTrace();
			}           
			return addresses;
             
		}
         
		@Override
		protected void onPostExecute(List<Address> addresses) {           
            
			if(addresses==null || addresses.size()==0){
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
			}	
            
			// Clears all the existing markers on the map
			map.clear();
            
			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size();i++){               
                
				Address address = (Address) addresses.get(i);
                	
				// Creating an instance of GeoPoint, to display in Google Map
				mPoint = new LatLng(address.getLatitude(), address.getLongitude());
                
				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",address.getCountryName());
				// Setting position for the marker
              
				markerOptions = new MarkerOptions();
				markerOptions.position(mPoint);
				markerOptions.title(addressText);
             
                // Setting title for the infowindow
				markerOptions.title(mPoint.latitude + ","+mPoint.longitude);
              
				//map.addMarker(markerOptions).setIcon(icon);
				mSearchMarker =  map.addMarker(markerOptions);
                
				Double mL1,mL2;
				mL1=mPoint.latitude;
				mL2=mPoint.longitude;
				mCoordl1 = mL1.toString();
				mCoordl2 = mL2.toString();
			    
				// Locate the first location
				if(i==0)                       
					map.animateCamera(CameraUpdateFactory.newLatLng(mPoint));    
                
				CameraPosition camPos = new CameraPosition.Builder().target(mPoint).zoom(15).build();
				CameraUpdate cam = CameraUpdateFactory.newCameraPosition(camPos);
				map.animateCamera(cam);
          
			}           
         }   
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		map.clear();
      
		Double mL1, mL2;
		mL1=location.getLatitude();
		mL2=location.getLongitude();
        
        mPoint = new LatLng(mL1, mL2);
        mKiel = ((GoogleMap) map)
        		.addMarker(new MarkerOptions()
        		.position(mPoint)
        		.title("My current location")
        		.snippet(" ")
        		.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_red)));

       
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mPoint, 12));
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
       
		mCoordl11 = mL1.toString();
		mCoordl22 = mL2.toString();
	   
        SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("tvLatitude", mCoordl11);
        editor.putString("tvLongitude",mCoordl22); 
        editor.commit();
	}
	
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(adView != null)
			adView.resume();
	}
	
	@Override
	public void onPause()
	{
		if(adView != null)
			adView.pause();
		super.onPause();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		   Log.d("Latitude","status");
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		  Log.d("Latitude","enable");
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		 Log.d("Latitude","disable");

	}
	
}
