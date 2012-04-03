package org.remoteandroid.apps.qcm.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class QCMService extends Service
{
	public static final String TAG = "QCM Service";
	public static final String ACTION_ADD_DEVICE = "org.remoteandroid.apps.qcm.ADD_DEVICE";
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		Log.i(TAG, "Start Command "+action);
		if (ACTION_ADD_DEVICE.equals(action))
		{
			
		}
		return 0;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

}
