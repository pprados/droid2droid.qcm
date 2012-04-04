package org.remoteandroid.apps.qcm.remote;

import org.remoteandroid.apps.qcm.ui.SuscribeActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;

public class RemoteQCMImpl extends RemoteQCM.Stub
{
	private Context mContext;

	private Handler mHandler = new Handler();

	private static Object sLock = new Object();

	private static String playerName = null;

	public RemoteQCMImpl(Context context)
	{
		this.mContext = context;
	}

	@Override
	public void exit() throws RemoteException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void standby() throws RemoteException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void play() throws RemoteException
	{
		// TODO Auto-generated method stub

	}

	private void postStartActivity(final Intent intent)
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				mContext.startActivity(intent);
			}
		});
	}

	private static String getResult()
	{
		synchronized (sLock)
		{
			try
			{
				sLock.wait();
				return playerName;

			}
			catch (InterruptedException e)
			{
				return null;
			}
		}
	}

	public static void postResult(String name)
	{
		synchronized (sLock)
		{
			playerName = name;
			sLock.notify();
		}
	}

	@Override
	public String suscribe() throws RemoteException
	{
		postStartActivity(new Intent(this.mContext, SuscribeActivity.class));
		return getResult();

	}

}
