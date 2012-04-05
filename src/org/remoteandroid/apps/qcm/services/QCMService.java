package org.remoteandroid.apps.qcm.services;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.remoteandroid.ListRemoteAndroidInfo;
import org.remoteandroid.RemoteAndroid;
import org.remoteandroid.RemoteAndroid.PublishListener;
import org.remoteandroid.RemoteAndroidInfo;
import org.remoteandroid.RemoteAndroidManager;
import org.remoteandroid.apps.qcm.remote.RemoteQCM;
import org.remoteandroid.apps.qcm.ui.QCMRemoteActivity;
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
	public Map<String, RemoteQCM> mPlayers = Collections.synchronizedMap(new HashMap<String, RemoteQCM>());

	ListRemoteAndroidInfo mAndroids;
	public static QCMService sMe;

	public static RemoteAndroidManager mManager;

	public static final String TAG = "QCM Service";

	public static final String ACTION_ADD_DEVICE = "org.remoteandroid.apps.qcm.ADD_DEVICE";

	private Mode mState = Mode.STOP;

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
		Log.i(
			TAG, "Start Command " + action);
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
					connect(info, uri);
				}
			}
		}).start();
	}
	
	private void connect (final RemoteAndroidInfo info,final String uri)
	{
		if (info.getUris().length==0)
			return ;
		mManager.bindRemoteAndroid(new Intent(Intent.ACTION_MAIN, Uri.parse(uri)), new ServiceConnection()
		{
			
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				mPlayers.remove(uri);
				mAndroids.remove(info);
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
										try
										{
											player.subscribe();
										}
										catch (RemoteException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
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
		}, 60000);
	}
	

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

}
