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
