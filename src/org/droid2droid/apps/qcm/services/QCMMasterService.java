package org.droid2droid.apps.qcm.services;

import static org.droid2droid.Droid2DroidManager.FLAG_ACCEPT_ANONYMOUS;
import static org.droid2droid.Droid2DroidManager.FLAG_PROPOSE_PAIRING;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.droid2droid.Droid2DroidManager;
import org.droid2droid.ListRemoteAndroidInfo;
import org.droid2droid.RemoteAndroid;
import org.droid2droid.RemoteAndroid.PublishListener;
import org.droid2droid.RemoteAndroidInfo;
import org.droid2droid.apps.qcm.model.MultipleChoicesQuestion;
import org.droid2droid.apps.qcm.model.Question;
import org.droid2droid.apps.qcm.model.SimpleChoiceQuestion;
import org.droid2droid.apps.qcm.model.XMLParser;
import org.droid2droid.apps.qcm.remote.RemoteQCM;
import org.droid2droid.apps.qcm.ui.master.MasterRestartScreen;
import org.droid2droid.apps.qcm.ui.master.MasterResult;
import org.droid2droid.apps.qcm.ui.master.QCMMasterActivity;
import org.droid2droid.apps.qcm.ui.master.QuestionActivity;
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

public class QCMMasterService extends Service
{
	public static final String START_SERVICE = "org.remoteandroid.apps.qcm.START_SERVICE";
	public static final String SEND_PLAYER_LIST = "org.remoteandroid.apps.qcm.SEND_PLAYER_LIST";
	public static final String REMOTE_START_GAME = "org.remoteandroid.apps.qcm.REMOTE_START_GAME";
	public static final String QUIT = "org.remoteandroid.apps.qcm.QUIT";
	public static final String ADD_DEVICE_BY_NFC = "org.remoteandroid.apps.qcm.ADD_DEVICE_BY_NFC";
	public Map<String, Player> mPlayers = Collections.synchronizedMap(new HashMap<String, Player>());
	public static final int TIME = 20;
	ListRemoteAndroidInfo mAndroids;
	public static QCMMasterService sMe;

	public static Droid2DroidManager mManager;

	public static final String TAG = "QCM Service";

//	public static final String ACTION_SUSCRIBE = "org.remoteandroid.apps.qcm.SUSCRIBE_TO_REMOTE";
	private final AtomicInteger mPlayersNumbers = new AtomicInteger(0);

	private Mode mState = Mode.STOP;
//	private boolean gameStart = false;
	private RemoteQCM master = null;
	private int badQuestion = 0;
	String winner = null;
	private static Object	sLock		= new Object();
	private enum Mode 
	{
		CONNECT, PLAY, WAIT, STOP,
	}
	Question question = null;
	@Override
	public void onCreate()
	{

		super.onCreate();
		if (sMe!=null) return;
		Log.d("service","Service onCreate");
		sMe=this;
		mAndroids = Droid2DroidManager.newDiscoveredAndroid(this,
				new ListRemoteAndroidInfo.DiscoverListener()
		{
			@Override
			public void onDiscover(final RemoteAndroidInfo remoteAndroidInfo, boolean replace)
			{
				QCMMasterService.this.onDiscover(remoteAndroidInfo,replace);
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

		Droid2DroidManager.bindManager(this, new Droid2DroidManager.ManagerListener()
		{
			
			@Override
			public void unbind(Droid2DroidManager manager)
			{
				mManager=null;
			}
			
			@Override
			public void bind(Droid2DroidManager manager)
			{
				mManager=manager;
								
			}
		});
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String action = intent.getAction();
		Log.i(TAG, "Start Command " + action);
		if(SEND_PLAYER_LIST.equals(action))
		{
			managePlayer();
		}
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
		if(QUIT.equals(action))
		{
			stopService();
		}
		if(ADD_DEVICE_BY_NFC.equals(action))
		{
			RemoteAndroidInfo info = intent.getParcelableExtra("info");
			if(info!=null)
				onDiscover(info, false);
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
		if (mState != Mode.STOP && replace)
			return;
		if(mState == Mode.STOP)
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
								player.setNickname(nickname);
								managePlayer();
								if(master==null)
									setMaster(remotePlayer);
								if(remotePlayer.starPlayRequest(mPlayersNumbers.incrementAndGet()))
								{
									startGame();
								}
//								player.setNickname(nickname);
							}
							else 
							{
								removePlayer(player);
							}
						}
						catch (RemoteException e)
						{
//							mPlayers.remove(remotePlayer);
							mPlayers.remove(player);
							managePlayer();
							e.printStackTrace();
							//Gerer l'echec de la souscription
						}
					}
				}
			}
		}).start();
	}
	
	public void managePlayer()
	{
		ArrayList<String> playersNickname = new ArrayList<String>();
		for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final Player player = i.next();
			if(player.getNickname()!=null)
				playersNickname.add(player.getNickname());
		}
		Intent intent = new Intent(QCMMasterActivity.REGISTER);
		intent.putExtra("playersNickname", playersNickname);
		sendBroadcast(intent);
	}
	
	public void startGame()
	{
		mState = Mode.PLAY;
		sendBroadcast(new Intent(AbstractGameScreen.FINISH_ACTIVITY));
		//Put the type of question
		ArrayList<Integer> questionList = new ArrayList<Integer>();
		for(int i= 1 ; i<= XMLParser.TOTAL_NUMBER_OF_QUESTION; i++)
			questionList.add(i);
		Collections.shuffle(questionList);
		Collections.shuffle(questionList);
		for(int num = 1; num<= XMLParser.MAX_QUESTION; num++ )
		{
			if(mPlayers.size()==0)
			{
				break;
			}
				
			final int questionNumber = questionList.get(num);
			Intent intent = new Intent(this, QuestionActivity.class);
			intent.putExtra("questionNumber", questionNumber); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);

			//Get a parser of question 
			final XMLParser parser = new XMLParser(this);
			setBadQuestion(0);
			try
			{
				question = parser.getQuestion(questionNumber);
			} catch (XmlPullParserException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
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
							ArrayList<String> results = (ArrayList<String>) player.getPlayer().play(questionNumber, QCMMasterService.TIME);
							if(results!=null)
							{
								if(results.size()==0)
									badQuestionIncrement();
//									badQuestion1.incrementAndGet();
								else if(question!=null)
								{
									if(XMLParser.SINGLE.equals(question.getType()))
									{
										SimpleChoiceQuestion sQuestion = (SimpleChoiceQuestion) question;
										if(sQuestion.getAnswer().equals(results.get(0)) && winner == null)
										{
											setWinner(player.getNickname());
											player.incrementScore();
											//TODO Annuler tout le choix chez tous les autres
											Log.d("TAG", "Simple Choice good question");
											restart();
										}
										else
											badQuestionIncrement();
//											badQuestion1.incrementAndGet();
									}
									else if(XMLParser.MULTIPLE.equals(question.getType()))
									{
										MultipleChoicesQuestion mQuestion = (MultipleChoicesQuestion) question;
										if(mQuestion.getAnswers().equals(results) && winner == null)
										{
											setWinner(player.getNickname());
											player.incrementScore();
											//TODO Annuler tout le choix chez tous les autres
											Log.d("TAG", "Multiple Choice good question");
											restart();
										}
										else 
											badQuestionIncrement();
//											badQuestion1.incrementAndGet();
									}							
								}								
							}
							else
							{
								removePlayer(player);
								if(mPlayers.size()==0)
									mState = Mode.STOP;
								if(mPlayers.size()>0)
								{
									if(player.getPlayer()==master)
									for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
									{
										final Player player = i.next();
										if(player.getPlayer()!=null)
											setMaster(player.getPlayer());
										return;
									}
								}
							}

							//Checker si tout le monde a r�pondu faux 
							if(mPlayers.size() == getBadQuestion()/*badQuestion1.get()*/)
							{
								restart();
							}
								
						}
						catch (RemoteException e)
						{
							mPlayers.remove(i);
							managePlayer();
							e.printStackTrace();
						} 
						
					}
				}).start();
			}
			stop();
			Log.d("TAG", "Multiple Choice good question");
			startAndStopResultScreen(winner, true);
			synchronized (sLock)
			{
				try
				{
					sLock.wait(3000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			startAndStopResultScreen(winner, false);
			setWinner(null);
			if(num==XMLParser.MAX_QUESTION)
			{
				mState = Mode.STOP;
				restartGame();
			}
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
				managePlayer();
				if(mPlayersNumbers.get()>0)
					mPlayersNumbers.decrementAndGet();
				mAndroids.remove(info);
				if(block)
				{
					synchronized (QCMMasterService.this)
					{
						QCMMasterService.this.notify();
					}
				}
//				Toast.makeText(getApplicationContext(), "Verify that you've shared your device in Remote Android parameters and you're in the same network ", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				final RemoteAndroid rA = (RemoteAndroid)service;
				rA.setExecuteTimeout(60*60000L);
				final Player player = new Player();
				player.setRemoteAndroid(rA);
				player.setUri(uri);
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
								Toast.makeText(QCMMasterService.this, "Device "+rA.getInfos().getName()+ " accept only applications from market.", Toast.LENGTH_SHORT).show();
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
										removePlayer(player);
										
									}
									
									@Override
									public void onServiceConnected(ComponentName name, IBinder service)
									{
										remotePlayer = RemoteQCM.Stub.asInterface(service);
										player.setPlayer(remotePlayer);
										mPlayers.put(uri, player);
										if(block)
										{
											result.rc = true;
											synchronized (QCMMasterService.this)
											{
												QCMMasterService.this.notify();
											}
										}
									}
								}, Context.BIND_AUTO_CREATE);
							}
							
						}
						
						@Override
						public void onError(Throwable e)
						{
							mAndroids.remove(info);
							mPlayers.remove(uri);
							managePlayer();
							
							
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
		}, FLAG_ACCEPT_ANONYMOUS|FLAG_PROPOSE_PAIRING);
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
//
//	@Override
//	public void onDestroy()
//	{
//		super.onDestroy();
//		Log.d("service","Service onDestroy");
//		for (final Player i:mPlayers.values())
//		{
//			new AsyncTask<Void, Void, Void>()
//			{
//				@Override
//				protected Void doInBackground(Void... params)
//				{
//					try
//					{
//						i.getPlayer().exit();
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
//		mPlayers = null;
//	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d("service","Service onDestroy");
		stopService();
	}
	private synchronized void stopService()
	{
		if (sMe==null)
			return;
		sMe=null;
		mAndroids.close();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				stopSyncService();
			}
		}).start();
		stopSelf();
	}

	private void stopSyncService()
	{
		if(mPlayers!=null)
		{
			for (final Player i:mPlayers.values())
			{
				new AsyncTask<Void, Void, Void>()
				{
					@Override
					protected Void doInBackground(Void... params)
					{
						RemoteAndroid ra = i.getRemoteAndroid();
						if(ra!=null)
							ra.close();
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
			mPlayers.clear();
		}

		if (mAndroids != null)
		{
			mAndroids.close();
		}
		if (mManager!=null)
			mManager.close();
		sMe=null;
	}
	
	public void removePlayer(Player player)
	{
		if(player.getPlayer()!=null)
			try
			{
				player.getPlayer().exit();
			} catch (RemoteException e)
			{
				//ignore
			}
		if(player.getRemoteAndroid()!=null)
			player.getRemoteAndroid().close();
		if(player.getUri()!=null)
			mPlayers.remove(player.getUri());
		if(mPlayers.size()==0)
			sendBroadcast(new Intent(MasterResult.FINISH_ACTIVITY));
		
	}

	
	public synchronized String getWinner()
	{
		return winner;
	}

	public synchronized void setWinner(String winner)
	{
		this.winner = winner;
	}
	
	public synchronized RemoteQCM getMaster()
	{
		return master;
	}

	public synchronized void setMaster(RemoteQCM master)
	{
		this.master = master;
	}
	
	
	public synchronized int getBadQuestion()
	{
		return badQuestion;
	}

	public synchronized void setBadQuestion(int badQuestion)
	{
		this.badQuestion = badQuestion;
	}
	public synchronized void badQuestionIncrement()
	{
		this.badQuestion ++;
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
		ArrayList<String> nicknames = new ArrayList<String>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final Player player = i.next();
			nicknames.add(player.getNickname());
			scores.add(player.getScore());
			new Thread( new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						player.getPlayer().startAndStopResultScreen(winner, player.getNickname(), player.getScore(), manage);
					} catch (RemoteException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
		if(manage)
		{
			sendBroadcast(new Intent(MasterResult.FINISH_ACTIVITY));
			startActivity(new Intent(this, MasterResult.class)
			.putExtra("winner", winner)
			.putStringArrayListExtra("nicknames", nicknames)
			.putIntegerArrayListExtra("scores", scores)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		//Put other player score here
		}
			
		else
			sendBroadcast(new Intent(MasterResult.FINISH_ACTIVITY));
		if(mPlayers.size()==0)
		{
			sendBroadcast(new Intent(MasterResult.FINISH_ACTIVITY));
			mState = Mode.STOP;
		}
			
	}
	
	public ArrayList<String> getWinnersInfos()
	{
		int max_score = 0;
		ArrayList<String> players = new ArrayList<String>();
		for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
		{
			final Player player = i.next();
			int score = player.getScore();
			if(score>max_score)
			{
				max_score=score;
				players.clear();
				players.add(player.getNickname());
			}
			else if(score==max_score && max_score!=0)
				players.add(player.getNickname());
			
		}
		return players;
	}
	public void restartGame()
	{
		ArrayList<String> winners = new ArrayList<String>();
		winners = getWinnersInfos();
		if(mPlayers.size()>0)
		{
			startActivity(new Intent(this, MasterRestartScreen.class)
			.putStringArrayListExtra("winners",winners)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			for(final Iterator<Player> i = mPlayers.values().iterator(); i.hasNext();)
			{
				final Player player = i.next();
				player.setScore(0);
				try
				{
					if(master==player.getPlayer() && master!=null)
					{
						if(master.restart(winners))
						{
							sendBroadcast(new Intent(AbstractGameScreen.FINISH_ACTIVITY));
							startGame();
						}
					}
					else
						player.getPlayer().displayWinner(winners);
				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
		}

	}
		

}
