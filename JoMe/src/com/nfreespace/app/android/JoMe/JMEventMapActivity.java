package com.nfreespace.app.android.JoMe;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nfreespace.app.android.JoMe.Object.GetCategory;

public class JMEventMapActivity extends FragmentActivity implements LocationListener {

	EditText 		vEdtSearch ;
	Button 			vBtnSearch,vBtnOnOff,vListBtn,vMapTypeBtn,vBtnHome,vBtnProfile,vBtnHistory;

	GoogleMap 		googleMap;
	Marker      	m_Marker;
	
	/// Google admov
	AdView 			adView;
	RelativeLayout 	adLayout;
	
	boolean 		mFMapType 	= false;
	int     		mStatus 	= 0;
	
	ArrayList<GetCategory> 	mEventList 	= new ArrayList<GetCategory>();
	
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_myevent_map);
	   
	    vBtnProfile	= (Button)findViewById(R.id.btnInvitationManager);
	    vBtnHome 	= (Button) findViewById(R.id.btnHome);
	    vListBtn 	= (Button)findViewById(R.id.btnmap);
	    vBtnOnOff 	= (Button) findViewById(R.id.button_on); 
	    vBtnSearch	= (Button) findViewById(R.id.search_btn);	
	    vMapTypeBtn = (Button) findViewById(R.id.map_type_btn);
	    vBtnHistory = (Button) findViewById(R.id.btnMessage);
	    vEdtSearch 	= (EditText) findViewById(R.id.eventsearch);
	    vEdtSearch.setVisibility(View.GONE);
	    
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
	  		
     	
     	try {
	        // Loading map
			initilizeMap();
			
			googleMap.setOnMarkerClickListener(new OnMarkerClickListener(){

				@Override
				public boolean onMarkerClick(Marker marker) {
					// TODO Auto-generated method stub
					
					String[]  arr = marker.getId().split("m");
		    		
					goEventDetail(Integer.parseInt(arr[1]));
				
					return false;
				}
			});
	 
		} catch (Exception e) {
			e.printStackTrace();
		}
     	
    	vMapTypeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if(googleMap != null)
				{
					if(mFMapType)
					{
						googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); 
						mFMapType = false;
					}else
					{
						googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); 
						mFMapType = true;
					}
				}
			}
		});
		
        
    	vBtnSearch.setOnClickListener(new View.OnClickListener() {
     		public void onClick(View v) {

     			if(!vEdtSearch.isShown()) {
     				vEdtSearch.setVisibility(View.VISIBLE);;
       			} else {
       				vEdtSearch.setVisibility(View.GONE);
       			}
       	  	}
     	});
	  
    	
    	vBtnOnOff.setOnClickListener(new View.OnClickListener() {
	    
	    	public void onClick(View v) {
	           //toggle picture
	           if (mStatus == 0) {
	        	   vBtnOnOff.setBackgroundResource(R.drawable.off_button);
	        	   mStatus=1 ; // change the status to 1 so the at the second click , the else will be executed
	           }   

	           else {  
	        	   vBtnOnOff.setBackgroundResource(R.drawable.on_button);
	        	   mStatus =0;//change the status to 0 so the at the second click , the if will be executed
	           }
	       }
    	});
	    
	    vListBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent allevents = new Intent(getApplicationContext(),JMEventListActivity.class);
				startActivity(allevents);
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
	    
	    initList();
	   
	}
	
/// -----------------  show map with  event list ------------------------	
	public void initList()
	{
		mEventList = constant.gEventlist;
		
		showMarker();
	}

/// -------------------------------------- get position ----------------------	
	public boolean  GetPosition()
	{
		GPSTracker mGPS = new GPSTracker(this);
	        
		if(mGPS.canGetLocation ){
			mGPS.getLocation();
			double m_fLat = (double)mGPS.getLatitude();
			double m_fLon = (double)mGPS.getLongitude();
			
			SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putString("currentlat",String.format("%f", m_fLat));
		    editor.putString("currentlng",String.format("%f", m_fLon)); 
		    editor.commit();
		    
			return true;
		}
	    return false;
	}
	
/// ---------------------------------------- show marker with LatLng -------------------------
	public void showMarker()
	{
		if(mEventList != null)
		{
			 for (int i = 0; i < mEventList.size(); i++) {
				 LatLng location = new LatLng(Double.parseDouble(mEventList.get(i).latitude),Double.parseDouble(mEventList.get(i).longitude));
				 showPinToMap(location,mEventList.get(i).event_name,mEventList.get(i).distance,mEventList.get(i).type);
	     	 }
		}
	}
	
///------------------------ show pin with position to map -----------------------------
    public void showPinToMap(LatLng pos,String name,String city, int type)
    {
    	if (googleMap!=null){
		   
    		googleMap.addMarker(new MarkerOptions()
	          .position(pos)
	          .title(name)
	          .snippet(city)
	          .icon(BitmapDescriptorFactory
	              .fromResource(constant.getMapIconId(type))));
       }
    }
    
 /// ------------------------- when go to event detail page, save event data to preference ------------------
    
    public void goEventDetail(int position)
    {
    	Intent i = new Intent(getApplicationContext(), JMEventDetailActivity.class);
		i.putExtra("eventId", mEventList.get(position).event_id);
		startActivity(i);
    }


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	      Double	lat = location.getLatitude();
		  Double	lng = location.getLongitude();
	      String  latitude = lat.toString();
	      String  longitude = lng.toString();
	      
	      SharedPreferences settings = getSharedPreferences(constant.PREFS_NAME, 0);
	      SharedPreferences.Editor editor = settings.edit();
	      editor.putString("currentlat",latitude);
	      editor.putString("currentlng",longitude); 
	      editor.commit();
	}


	 /**
     * function to load map. If map is not created it will create it for you
     * */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map_detail)).getMap();
            
            // googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        
    }
    
	@Override
	public void onBackPressed() {
		constant.QuitDialog("Are you going to quit JoMe?", "Yes", "No", this);
	}
  
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
        
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
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
		
	 
}

