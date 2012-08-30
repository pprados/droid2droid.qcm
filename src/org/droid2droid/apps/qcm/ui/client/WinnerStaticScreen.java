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

import java.util.ArrayList;
import java.util.Iterator;

import org.droid2droid.apps.qcm.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class WinnerStaticScreen extends SherlockActivity
{
	private LinearLayout result_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_screen_static);
		result_layout = (LinearLayout)findViewById(R.id.winner_layout);
		Intent intent = getIntent();
		ArrayList<String> winners = intent.getStringArrayListExtra("winners");
		if(winners!=null)
			for (Iterator<String> i = winners.iterator(); i.hasNext();)
			{
				TextView winner = new TextView(getApplicationContext());
				winner.setText(i.next());
				result_layout.addView(winner);
			}
	}

}
