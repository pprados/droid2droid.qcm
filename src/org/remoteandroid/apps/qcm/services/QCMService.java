package org.remoteandroid.apps.qcm.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.remoteandroid.ListRemoteAndroidInfo;
import org.remoteandroid.RemoteAndroid;
import org.remoteandroid.RemoteAndroid.PublishListener;
import org.remoteandroid.RemoteAndroidInfo;
import org.remoteandroid.RemoteAndroidManager;
import org.remoteandroid.apps.qcm.model.XMLParser;
import org.remoteandroid.apps.qcm.remote.RemoteQCM;
import org.remoteandroid.apps.qcm.ui.QCMRemoteActivity;
import org.remoteandroid.apps.qcm.ui.QuestionActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class QCMService extends Service
{
	public static final String REMOTE_START_GAME = "org.remoteandroid.apps.qcm.REMOTE_START_GAME";
	public Map<String, RemoteQCM> mPlayers = Collections.synchronizedMap(new HashMap<String, RemoteQCM>());
	public Map<String, String> mPlayersNickname = Collections.synchronizedMap(new HashMap<String, String>());
	public static final int TIME = 20;
	ListRemoteAndroidInfo mAndroids;
	public static QCMService sMe;

	public static RemoteAndroidManager mManager;

	public static final String TAG = "QCM Service";

//	public static final String ACTION_SUSCRIBE = "org.remoteandroid.apps.qcm.SUSCRIBE_TO_REMOTE";
	private AtomicInteger mPlayersNumbers = new AtomicInteger(0);

	private Mode mState = Mode.STOP;
	private boolean gameStart = false;
	private RemoteQCM master = null;

	private enum Mode 
	{
		CONNECT, PLAY, WAIT, STOP,
	}
	
	@Override
	public void onCreate()
	{

		super.onCreate();
		if (sMe!=null) return;
		Log.d("service","Service onCreate");
		sMe=this;
		RemoteAndroidManager.bindManager(this, new RemoteAndroidManager.ManagerListener()
		{
			
			@Override
			public void unbind(RemoteAndroidManager manager)
			{
				mManager=null;
			}
			
			@Override
			public void bind(RemoteAndroidManager manager)
			{
				// TODO Auto-generated method stub
				mManager=manager;
				mAndroids = mManager.newDiscoveredAndroid(new ListRemoteAndroidInfo.DiscoverListener()
				{
					@Override
					public void onDiscover(final RemoteAndroidInfo remoteAndroidInfo, boolean replace)
					{
						QCMService.this.onDiscover(remoteAndroidInfo,replace);
					}

					@Override
					public void onDiscoverStart()
					{
					}

					@Override
					public void onDiscoverStop()
					{
					}
				});
				
			}
		});
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		Log.i(TAG, "Start Command " + action);
		if(REMOTE_START_GAME.equals(action))
		{
			if(master!=null)
				try
				{
					master.leaveMaster();
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
				
		}
		return 0;
	}

	public void onDiscover(final RemoteAndroidInfo remoteAndroidInfo, boolean replace)
	{
		if (remoteAndroidInfo.getUris().length == 0)
			return;
		// If try a new connexion, and must pairing devices, the discover fire,
		// but i must ignore it now. I will manage in the onResult.
		mAndroids.remove(remoteAndroidInfo);
		mAndroids.add(remoteAndroidInfo);
		if (replace)
			return; // TODO Optimise la connexion
		startConnection(remoteAndroidInfo);

	}
	
	private void startConnection(final RemoteAndroidInfo info)
	{
		new Thread( new Runnable()
		{
			@Override
			public void run()
			{
				for(String uri : info.getUris())
				{
					if(connect(info, uri, true))
					{
//						mPlayersNumbers.incrementAndGet();
						RemoteQCM player = mPlayers.get(uri);
						try
						{
							String nickname = player.subscribe();
							if(nickname!=null)
							{
								mPlayersNickname.put(uri, nickname);
								managePlayer(nickname, QCMRemoteActivity.ADD_PLAYER);
//								int number = mPlayersNumbers.get();
								master=player;
								if(player.starPlayRequest(mPlayersNumbers.incrementAndGet()))
								{
									Log.d("TAG","Master start the game");
									startGame(uri);
								}
								else
									master=null;
							}
						}
						catch (RemoteException e)
						{
							mPlayers.remove(player);
							managePlayer(mPlayersNickname.get(uri), QCMRemoteActivity.REMOVE_PLAYER);
							e.printStackTrace();
							//Gerer l'echec de la souscription
						}
					}
				}
			}
		}).start();
	}
	
	public void managePlayer(String nickname, boolean manageType)
	{
		Intent intent = new Intent(QCMRemoteActivity.REGISTER);
		intent.putExtra("nickname", nickname);
		intent.putExtra("type", manageType);
		sendBroadcast(intent);
	}
	
	public void startGame(final String uri)
	{
		
		//Put the type of question
		ArrayList<Integer> questionList = new ArrayList<Integer>();
		for(int i= 1 ; i<= XMLParser.TOTAL_NUMBER_OF_QUESTION; i++)
			questionList.add(i);
		Collections.shuffle(questionList);
		final int questionNumber = questionList.get(0);
		Intent intent = new Intent(this, QuestionActivity.class);
		intent.putExtra("questionNumber", questionNumber);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
		//Other
		for(final Iterator<RemoteQCM> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final RemoteQCM player = i.next();
			new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					//optimiser en mettant dans une new method
					try
					{
						player.play(questionNumber, QCMService.TIME);
					}
					catch (RemoteException e)
					{
						mPlayers.remove(player);
						managePlayer(mPlayersNickname.get(uri), QCMRemoteActivity.REMOVE_PLAYER);
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		
	}
	
	
	
	private boolean connect (final RemoteAndroidInfo info,final String uri, final boolean block)
	{
		if (info.getUris().length==0)
			return false;
		class Result
		{
			volatile boolean rc; 
		}
		final Result result = new Result();
		mManager.bindRemoteAndroid(new Intent(Intent.ACTION_MAIN, Uri.parse(uri)), new ServiceConnection()
		{
			
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				mPlayers.remove(uri);
				managePlayer(mPlayersNickname.get(uri), QCMRemoteActivity.REMOVE_PLAYER);
				mPlayersNickname.remove(uri);
				//TODO Send broadcast to remove on the liste
				mPlayersNumbers.decrementAndGet();
				mAndroids.remove(info);
				if(block)
				{
					synchronized (QCMService.this)
					{
						QCMService.this.notify();
					}
				}
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				final RemoteAndroid rA = (RemoteAndroid)service;
				rA.setExecuteTimeout(60*60000L);
				try
				{
					rA.pushMe(getApplicationContext(), new PublishListener()
					{
						
						@Override
						public void onProgress(int progress)
						{
							//setStatus("Progress..."+progress/100+"%");
							
						}
						
						@Override
						public void onFinish(int status)
						{
							if(status == 2)
							{
								Toast.makeText(QCMService.this, "Device "+rA.getInfos().getName()+ " accept only applications from market.", Toast.LENGTH_SHORT).show();
							}
							else if(status == -1)
							{
								//Connection refused
							}
							else if (status >= 0)
							{
								rA.bindService(new Intent("org.remoteandroid.apps.QCM.RemoteService"), new ServiceConnection()
								{
									RemoteQCM player;
									@Override
									public void onServiceDisconnected(ComponentName name)
									{
										player=null;
										
									}
									
									@Override
									public void onServiceConnected(ComponentName name, IBinder service)
									{
										player = RemoteQCM.Stub.asInterface(service);
										mPlayers.put(uri, player);
										if(block)
										{
											result.rc = true;
											synchronized (QCMService.this)
											{
												QCMService.this.notify();
											}
										}
									}
								}, Context.BIND_AUTO_CREATE);
							}
							
						}
						
						@Override
						public void onError(Throwable e)
						{
							mPlayers.remove(uri);
							mAndroids.remove(info);
							managePlayer(mPlayersNickname.get(uri), QCMRemoteActivity.REMOVE_PLAYER);
							mPlayersNickname.remove(uri);
							
						}
						
						@Override
						public boolean askIsPushApk()
						{
							return true;
						}
					}, 0, 60000);
				}
				catch (RemoteException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				
			}
		}, 0);
		if(block)
		{
			synchronized (this)
			{
				try
				{
					wait();
					return result.rc;
				}
				catch (InterruptedException e)
				{
					return false;
				}
			}
		}
		return false;
	}
	

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
//	public void getRandom()
//	{
//		int[] values = {1,2,3,4,5,6,7,8,9,10}; 
//		Collections.shuffle(values);
//	}
//	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
//		Log.d("service","Service onDestroy");
//		for (final RemoteQCM i:mPlayers.values())
//		{
//			new AsyncTask<Void, Void, Void>()
//			{
//				@Override
//				protected Void doInBackground(Void... params)
//				{
//					try
//					{
//						i.exit();
//					}
//					catch (RemoteException e)
//					{
//						// Ignore
//					}
//					return null;
//				}
//			}.execute();
//		}
//		if (mAndroids != null)
//		{
//			mAndroids.close();
//		}
//		mManager.close();
//		sMe=null;
	}

}
