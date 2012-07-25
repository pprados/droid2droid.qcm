package org.droid2droid.apps.qcm.remote;

import java.util.ArrayList;
import java.util.List;

import org.droid2droid.apps.qcm.services.QCMMasterService;
import org.droid2droid.apps.qcm.ui.client.AnswerActivity;
import org.droid2droid.apps.qcm.ui.client.ClientResult;
import org.droid2droid.apps.qcm.ui.client.ClientStartGame;
import org.droid2droid.apps.qcm.ui.client.SuscribeActivity;
import org.droid2droid.apps.qcm.ui.client.WaitingActivity;
import org.droid2droid.apps.qcm.ui.client.WinnerRestartScreen;
import org.droid2droid.apps.qcm.ui.client.WinnerStaticScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;

public class RemoteQCMImpl extends RemoteQCM.Stub
{
	private final Context mContext;
	private static Object	sLock		= new Object();
	private final Handler mHandler = new Handler();
//	public static long time = 60000L;
	private static String playerName;
	private static boolean startValue;
	private static List<String> results;

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
				mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP/*|Intent.FLAG_ACTIVITY_CLEAR_TASK*/|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_NEW_TASK));
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
	public boolean starPlayRequest(int number) throws RemoteException
	{
		if(number==1)
		{
			postStartActivity(new Intent(this.mContext, ClientStartGame.class));
			return getStartGame();
		}
		else 
		{
			postStartActivity(new Intent(this.mContext, WaitingActivity.class));
			return false;
		}
	}

	@Override
	public List<String> play(int questionNumber, long startTime) throws RemoteException
	{
		Intent intent = new Intent(this.mContext, AnswerActivity.class);
		intent.putExtra("startTime", startTime);
		intent.putExtra("questionNumber", questionNumber);
		postStartActivity(intent);
		return getResults();
	}
	
	private static List<String> getResults()
	{
		synchronized (sLock)
		{
			try
			{
				sLock.wait(QCMMasterService.TIME*1000L+200);
				return results;
			}
			catch (InterruptedException e)
			{
				return null;
			}
		}
	}
	public static void postResults(List<String> results)
	{
		synchronized (sLock)
		{
			RemoteQCMImpl.results = results;
			sLock.notify();
		}
	}

	@Override
	public void leaveMaster() throws RemoteException
	{
		postStartGame(true);
	}
	@Override
	public void exit() throws RemoteException
	{
		new Thread()
		{
			@Override
			public void run()
			{
				try { Thread.sleep(200); } catch (Throwable e) {}
				System.exit(0);
			}
		}.start();
	}

	@Override
	public void startAndStopResultScreen(String winner,String player, int score, boolean manage) throws RemoteException
	{
		if(manage)
		{
			postResults(null);
			mContext.sendBroadcast(new Intent(AnswerActivity.FINISH_RESPONSEACTIVITY));
			Intent intent = new Intent(mContext, ClientResult.class);
			intent.putExtra("winner", winner);
			intent.putExtra("score", score);
			intent.putExtra("player", player);
			postStartActivity(intent);
		}
		else 
		{
			mContext.sendBroadcast(new Intent(ClientResult.FINISH));
		}
	}

	@Override
	public void displayWinner(List<String> winners) throws RemoteException
	{
		Intent intent = new Intent(mContext, WinnerStaticScreen.class);
		intent.putStringArrayListExtra("winners", (ArrayList<String>) winners);
		postStartActivity(intent);
		
	}

	@Override
	public boolean restart(List<String> winners) throws RemoteException
	{
		Intent intent = new Intent(mContext, WinnerRestartScreen.class);
		intent.putStringArrayListExtra("winners", (ArrayList<String>) winners);
		postStartActivity(intent);
		return getStartGame();
	}


	
}
