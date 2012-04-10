package org.remoteandroid.apps.qcm.remote;

import org.remoteandroid.apps.qcm.ui.MasterActivity;
import org.remoteandroid.apps.qcm.ui.SuscribeActivity;
import org.remoteandroid.apps.qcm.ui.WaitingActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;

public class RemoteQCMImpl extends RemoteQCM.Stub
{
	private Context mContext;
	private static Object	sLock		= new Object();
	private Handler mHandler = new Handler();
//	public static long time = 60000L;
	private static String playerName;
	private static boolean startValue;

	public RemoteQCMImpl(Context context)
	{
		this.mContext = context;
	}

	@Override
	public String subscribe() throws RemoteException
	{
		postStartActivity(new Intent(this.mContext, SuscribeActivity.class));
		return getNickname();

	}
	private void postStartActivity(final Intent intent)
	{
		mHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		});
	}
	public static void postNickname(String nickname)
	{
		synchronized (sLock)
		{
			playerName = nickname;
			sLock.notify();
		}
	}

	private static String getNickname()
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
	
	public static void postStartGame(boolean value)
	{
		synchronized (sLock)
		{
			startValue = value;
			sLock.notify();
		}
	}

	private static boolean getStartGame()
	{
		synchronized (sLock)
		{
			try
			{
				sLock.wait();
				return startValue;

			}
			catch (InterruptedException e)
			{
				return false;
			}
		}
	}

	@Override
	public boolean starPlay(int number) throws RemoteException
	{
		if(number==1)
		{
			postStartActivity(new Intent(this.mContext, MasterActivity.class));
			return getStartGame();
		}
		else 
		{
			postStartActivity(new Intent(this.mContext, WaitingActivity.class));
			return false;
		}
	}


}
