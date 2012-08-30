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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class SuscribeActivity extends SherlockActivity implements OnClickListener
{
//	private static final int 	REQUEST_CONNECT_CODE=1;
	private Button playButton;
	private EditText nickname;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suscribe_layout);
		Intent intent = getIntent();
		intent.getStringExtra("uri");
		playButton = (Button)findViewById(R.id.subscribe_button);
		playButton.setOnClickListener(this);
		nickname = (EditText)findViewById(R.id.edt_pseudo);
	}
	
	@Override
		public void onBackPressed()
		{
			super.onBackPressed();
			RemoteQCMImpl.postNickname(null);
		}
	@Override
	public void onClick(View v)
	{
		if(!("").equals(nickname.getText().toString()))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you really want to submit your nickname to the server?")
			       .setCancelable(false)
			       .setTitle("Confirmation")
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			       {
			           @Override
					public void onClick(DialogInterface dialog, int id) 
			           {
			        	  RemoteQCMImpl.postNickname(nickname.getText().toString());
			        	  finish();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() 
			       {
			           @Override
					public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.setCanceledOnTouchOutside(false);
			alert.show();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Bad nickname please retry ")
			       .setCancelable(false)
			       .setTitle("Error")
			       .setPositiveButton("Retry", new DialogInterface.OnClickListener() 
			       {
			           @Override
					public void onClick(DialogInterface dialog, int id) 
			           {
			        	   dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.setCanceledOnTouchOutside(false);
			alert.show();
		}
		
	}
	
	
}