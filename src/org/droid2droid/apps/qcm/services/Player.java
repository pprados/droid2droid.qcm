/******************************************************************************
 *
 * droid2droid - Distributed Android Framework
 * ==========================================
 *
 * Copyright (C) 2012 by Atos (http://www.http://atos.net)
 * http://www.droid2droid.org
 *
 ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
******************************************************************************/
package org.droid2droid.apps.qcm.services;

import org.droid2droid.RemoteAndroid;
import org.droid2droid.apps.qcm.remote.RemoteQCM;

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
