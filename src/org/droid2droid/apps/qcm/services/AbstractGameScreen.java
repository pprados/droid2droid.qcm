/******************************************************************************
 *
 * droid2droid - Distributed Android Framework
 * ==========================================
 *
 * Copyright (C) 2012 by Atos (http://www.http://atos.net)
 * http://www.droid2droid.org
 *
 ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
******************************************************************************/
package org.droid2droid.apps.qcm.services;

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



