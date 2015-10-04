package com.nfreespace.app.android.JoMe;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class JMJoinFragment extends Fragment {
	
	Button    vBtnLetsJo;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_create_event1, container, false);

		vBtnLetsJo		= (Button)view.findViewById(R.id.letjo_btn);
		vBtnLetsJo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				constant.gIsCreate = true;
     			Intent it=new Intent(getActivity(), JMEventCreateActivity.class);
				startActivity(it);
			}
		});
		
		return view;
	}
}
