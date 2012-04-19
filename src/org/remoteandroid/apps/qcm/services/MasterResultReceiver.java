package org.remoteandroid.apps.qcm.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class MasterResultReceiver extends ResultReceiver
{

	public MasterResultReceiver()
	{
		super(new Handler());
	}
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData)
	{
		//Implements method to call others activity here from the remote side
	}

}
