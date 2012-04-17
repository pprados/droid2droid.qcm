package org.remoteandroid.apps.qcm.services;

import org.remoteandroid.apps.qcm.remote.RemoteQCM;

public class Player
{
	private RemoteQCM player;
	private String nickname;
	public Player()
	{
		this.player = null;
		this.nickname = null;
	}
	public Player(RemoteQCM player, String nickname)
	{
		this.player=player;
		this.nickname=nickname;
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

}
