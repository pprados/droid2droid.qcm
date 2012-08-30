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
package org.droid2droid.apps.qcm.ui.client;

import org.droid2droid.apps.qcm.R;
import org.droid2droid.apps.qcm.remote.RemoteQCMImpl;
import org.droid2droid.apps.qcm.services.AbstractGameScreen;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ClientStartGame extends AbstractGameScreen implements OnClickListener
{
	Button playButton;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.master_start_playing_layout);
		playButton = (Button)findViewById(R.id.play_button);
		playButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View view)
	{
		if(view==playButton)
		{
			RemoteQCMImpl.postStartGame(true);
		}
		
	}
	@Override
	public void onBackPressed()
	{
	}
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		// TODO Auto-generated method stub
	}
}
