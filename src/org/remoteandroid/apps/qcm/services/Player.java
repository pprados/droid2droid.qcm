package org.remoteandroid.apps.qcm.services;

import org.remoteandroid.apps.qcm.remote.RemoteQCM;

public class Player
{
	private RemoteQCM player;
	private String nickname;
	private int score;
	public Player()
	{
		this.player = null;
		this.nickname = null;
		this.score = 0;
	}
	public Player(RemoteQCM player, String nickname)
	{
		this.player=player;
		this.nickname=nickname;
		this.score = 0;
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
	
}
