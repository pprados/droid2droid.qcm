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
import org.remoteandroid.apps.qcm.model.MultipleChoicesQuestion;
import org.remoteandroid.apps.qcm.model.Question;
import org.remoteandroid.apps.qcm.model.SimpleChoiceQuestion;
import org.remoteandroid.apps.qcm.model.XMLParser;
import org.remoteandroid.apps.qcm.remote.RemoteQCM;
import org.remoteandroid.apps.qcm.ui.QCMRemoteActivity;
import org.remoteandroid.apps.qcm.ui.QuestionActivity;
import org.remoteandroid.apps.qcm.ui.RemoteResult;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class QCMService extends Service
{
	public static final String REMOTE_START_GAME = "org.remoteandroid.apps.qcm.REMOTE_START_GAME";
	public Map<String, Player> mPlayers = Collections.synchronizedMap(new HashMap<String, Player>());
//	public Map<String, String> mPlayersNickname = Collections.synchronizedMap(new HashMap<String, String>());
	public static final int TIME = 200;
	ListRemoteAndroidInfo mAndroids;
	public static QCMService sMe;

	public static RemoteAndroidManager mManager;

	public static final String TAG = "QCM Service";

//	public static final String ACTION_SUSCRIBE = "org.remoteandroid.apps.qcm.SUSCRIBE_TO_REMOTE";
	private AtomicInteger mPlayersNumbers = new AtomicInteger(0);

	private Mode mState = Mode.STOP;
	private boolean gameStart = false;
	private RemoteQCM master = null;
	String winner = null;
	private static Object	sLock		= new Object();

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
						Player player = mPlayers.get(uri);
						RemoteQCM remotePlayer = player.getPlayer();
						try
						{
							String nickname = remotePlayer.subscribe();
							if(nickname!=null)
							{
//								mPlayersNickname.put(uri, nickname);
								player.setNickname(nickname);
								managePlayer(nickname, QCMRemoteActivity.ADD_PLAYER);
//								int number = mPlayersNumbers.get();
								master=remotePlayer;
								if(remotePlayer.starPlayRequest(mPlayersNumbers.incrementAndGet()))
								{
									Log.d("TAG","Master start the game");
									startGame();
								}
								else
									master=null;
								player.setNickname(nickname);
							}
						}
						catch (RemoteException e)
						{
//							mPlayers.remove(remotePlayer);
							managePlayer(mPlayers.get(uri).getNickname(), QCMRemoteActivity.REMOVE_PLAYER);
							mPlayers.remove(player);
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
	
	public void startGame()
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
		
		//Get a parser of question 
		final XMLParser parser = new XMLParser(this);
		//Atomic integer of number of player who have fail to th question 
		final AtomicInteger badQuestion = new AtomicInteger(0);
		for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final Player player = i.next();
			new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					//optimiser en mettant dans une nouvelle method
					try
					{
						ArrayList<String> results = (ArrayList<String>) player.getPlayer().play(questionNumber, QCMService.TIME);
						if(results==null)
							badQuestion.incrementAndGet();
						else
						{
							Question question = parser.getQuestion(questionNumber);
							if(XMLParser.SINGLE.equals(question.getType()))
							{
								SimpleChoiceQuestion sQuestion = (SimpleChoiceQuestion) question;
								if(sQuestion.getAnswer().equals(results.get(0)) && winner != null)
								{
									setWinner(player.getNickname());
									player.incrementScore();
									//TODO Annuler tout le choix chez tous les autres
									Log.d("TAG", "Simple Choice good question");
									restart();
								}
								else
									badQuestion.incrementAndGet();
							}
							else if(XMLParser.MULTIPLE.equals(question.getType()))
							{
								MultipleChoicesQuestion mQuestion = (MultipleChoicesQuestion) question;
								if(mQuestion.getAnswers().equals(results) && winner != null)
								{
									setWinner(player.getNickname());
									player.incrementScore();
									//TODO Annuler tout le choix chez tous les autres
									Log.d("TAG", "Multiple Choice good question");
									restart();
								}
								else 
									badQuestion.incrementAndGet();
							}							
						}
						
						//Checker si tout le monde a rŽpondu faux 
						if(mPlayers.size() == badQuestion.get())
						{
							//Lancer l'ecan qui dit que personne n'a gagnŽ
							restart();
						}
							
					}
					catch (RemoteException e)
					{
//						mPlayers.remove(player);
						managePlayer(((Player) i).getNickname(), QCMRemoteActivity.REMOVE_PLAYER);
						mPlayers.remove(i);
						e.printStackTrace();
					} catch (XmlPullParserException e)
					{
						e.printStackTrace();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					
				}
			}).start();
		}
		stop();
		Log.d("TAG", "Multiple Choice good question");
		startAndStopResultScreen(winner, true);
		
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
//				mPlayers.remove(uri);
				managePlayer(mPlayers.get(uri).getNickname(), QCMRemoteActivity.REMOVE_PLAYER); //FIXME Null pointer exception on disconnect
//				mPlayersNickname.remove(uri);
				mPlayers.remove(uri);
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
									RemoteQCM remotePlayer;
									@Override
									public void onServiceDisconnected(ComponentName name)
									{
										remotePlayer=null;
										
									}
									
									@Override
									public void onServiceConnected(ComponentName name, IBinder service)
									{
										remotePlayer = RemoteQCM.Stub.asInterface(service);
										Player player = new Player();
										player.setPlayer(remotePlayer);
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
//							mPlayers.remove(uri);
							mAndroids.remove(info);
							managePlayer(mPlayers.get(uri).getNickname(), QCMRemoteActivity.REMOVE_PLAYER);
//							mPlayersNickname.remove(uri);
							mPlayers.remove(uri);
							
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d("service","Service onDestroy");
		for (final Player i:mPlayers.values())
		{
			new AsyncTask<Void, Void, Void>()
			{
				@Override
				protected Void doInBackground(Void... params)
				{
					try
					{
						i.getPlayer().exit();
					}
					catch (RemoteException e)
					{
						// Ignore
					}
					return null;
				}
			}.execute();
		}
		if (mAndroids != null)
		{
			mAndroids.close();
		}
		mManager.close();
		sMe=null;
		mPlayers = null;
	}
	
	public synchronized String getWinner()
	{
		return winner;
	}

	public synchronized void setWinner(String winner)
	{
		this.winner = winner;
	}
	
	private void stop()
	{
		synchronized (sLock)
		{
			try
			{
				sLock.wait();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	private void restart()
	{
		synchronized (sLock)
		{
			sLock.notify();
		}
	}
	private void startAndStopResultScreen(final String winner, final boolean manage)
	{
		if(manage)
		{
			startActivity(new Intent(QCMService.this, RemoteResult.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
		else 
		{
			sendBroadcast(new Intent(RemoteResult.FINISH));
			
		}
		for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final Player player = i.next();
			new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						player.getPlayer().startAndStopResultScreen(winner, player.getScore(), manage);
					} catch (RemoteException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
		

}
