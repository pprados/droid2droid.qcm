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
import org.droid2droid.apps.qcm.services.AbstractGameScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ClientResult extends AbstractGameScreen
{
	private ImageView mImage;
	private TextView mWinner, mScore;
	public static final String FINISH = "org.remoteandroid.apps.qcm.FINISH";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_client_layout);
		mImage = (ImageView)findViewById(R.id.winner_image);
		mWinner = (TextView) findViewById(R.id.winnerName);
		mScore = (TextView) findViewById(R.id.playerscore);
		Intent intent = getIntent();
		String winner = intent.getExtras().getString("winner");
		String player = intent.getExtras().getString("player");
		int score = intent.getExtras().getInt("score");
		if(winner!=null)
		{
			if(winner.equals(player))
				mImage.setImageResource(R.drawable.win);
			else
				mImage.setImageResource(R.drawable.loose);
			mWinner.setText(winner);
		}
		else 
		{
			mImage.setImageResource(R.drawable.loose);
			mWinner.setText("No winner");
		}
		mScore.setText(String.valueOf(score));
			
		
	}
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		finish();
		
	}
	
}
