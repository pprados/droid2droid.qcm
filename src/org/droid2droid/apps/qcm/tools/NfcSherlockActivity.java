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
package org.droid2droid.apps.qcm.tools;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public abstract class NfcSherlockActivity extends SherlockActivity
implements CreateNdefMessageCallback
{
	private NfcAdapter mNfcAdapter;
	
//	@Override
//	protected void onNewIntent(Intent intent)
//	{
//		super.onNewIntent(intent);
//		mNfcIntegration.onNewIntent(this, getRemoteAndroidManager(), intent);
//	}
	
	@Override
	@TargetApi(14)	
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		if (VERSION.SDK_INT>VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			mNfcAdapter=NfcAdapter.getDefaultAdapter(this);
			mNfcAdapter.setNdefPushMessageCallback(this, this);
		}
	}
}
