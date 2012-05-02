package org.remoteandroid.apps.qcm.services;

import org.remoteandroid.RemoteAndroid;
import org.remoteandroid.apps.qcm.remote.RemoteQCM;

public class Player
{
	private RemoteQCM player;
	private String nickname;
	private int score;
	private RemoteAndroid remoteAndroid;
	private String uri ;
	public Player()
	{
		this.player = null;
		this.nickname = null;
		this.score = 0;
		this.remoteAndroid=null;
		this.uri = null;
	}
	public Player(RemoteQCM player, String nickname, int score, RemoteAndroid remoteAndroid, String uri)
	{
		this.player=player;
		this.nickname=nickname;
		this.score = 0;
		this.remoteAndroid=remoteAndroid;
		this.uri = uri;
	}
	public RemoteQCM getPlayer()
	{
		return player;
	}
	public void setPlayer(RemoteQCM player)
	{
		this.player = player;
	}
	public String getNickname()
	{
		return nickname;
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public int getScore()
	{
		return score;
	}
	public void setScore(int score)
	{
		this.score = score;
	}
	public void incrementScore()
	{
		this.score++;
	}
	public RemoteAndroid getRemoteAndroid()
	{
		return remoteAndroid;
	}
	public void setRemoteAndroid(RemoteAndroid remoteAndroid)
	{
		this.remoteAndroid = remoteAndroid;
	}
	public String getUri()
	{
		return uri;
	}
	public void setUri(String uri)
	{
		this.uri = uri;
	}
	
	
}
