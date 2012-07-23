package org.remoteandroid.apps.qcm.tools;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.os.Build;
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
		if (Build.VERSION.SDK_INT>Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			mNfcAdapter=NfcAdapter.getDefaultAdapter(this);
			mNfcAdapter.setNdefPushMessageCallback(this, this);
		}
	}
}
