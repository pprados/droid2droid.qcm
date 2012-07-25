package org.droid2droid.apps.qcm.remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RemoteQCMService extends Service
{

	@Override
	public IBinder onBind(Intent intent)
	{
		return new RemoteQCMImpl(getApplicationContext());
	}

}
