package org.remoteandroid.apps.qcm.services;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public abstract class AbstractGameScreen extends SherlockActivity
{
	public static final String FINISH_ACTIVITY = "org.remoteandroid.apps.qcm.FINISH_ACTIVITY";
	BroadcastReceiver mBroadcast=new BroadcastReceiver()
	{
	
		public void onReceive(android.content.Context context, android.content.Intent intent) 
		{
			onReceiveForService(intent.getExtras());
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mBroadcast, new IntentFilter(FINISH_ACTIVITY));
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mBroadcast);
	}
	
	public abstract void onReceiveForService(Bundle resultData);
}



