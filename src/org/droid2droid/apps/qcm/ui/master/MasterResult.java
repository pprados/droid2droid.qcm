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
package org.droid2droid.apps.qcm.ui.master;

import java.util.ArrayList;

import org.droid2droid.apps.qcm.R;
import org.droid2droid.apps.qcm.services.AbstractGameScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MasterResult extends AbstractGameScreen
{
	private ImageView mImage;
	private TextView mWinner;
	LinearLayout resultLayout;
	public static final String FINISH = "org.remoteandroid.apps.qcm.FINISH";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_remote_layout);
		mImage = (ImageView)findViewById(R.id.winner_image);
		mWinner = (TextView) findViewById(R.id.winnerName);
		resultLayout = (LinearLayout) findViewById(R.id.result_linear_layout);
		Intent intent = getIntent();
		String winner = intent.getExtras().getString("winner");
		if(winner!=null)
		{
			mImage.setImageDrawable((getResources().getDrawable(R.drawable.win)));
			mWinner.setText(winner);
		}
		else 
		{
			mImage.setImageDrawable((getResources().getDrawable(R.drawable.loose)));
			mWinner.setText("No winner");
		}
		ArrayList<String> nicknames = intent.getStringArrayListExtra("nicknames");
		ArrayList<Integer> scores = intent.getIntegerArrayListExtra("scores");
		for(int i = 0; i<nicknames.size(); i++)
		{
			View result = getLayoutInflater().inflate(R.layout.result_row, null);
			TextView player = (TextView) result.findViewById(R.id.name);
			TextView score = (TextView) result.findViewById(R.id.score);
			player.setText(nicknames.get(i));
			score.setText(scores.get(i).toString());
			resultLayout.addView(result);
		}
		
		
 	}
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		finish();
	}
	
}
