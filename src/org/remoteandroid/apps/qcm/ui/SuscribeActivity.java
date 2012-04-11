package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.remote.RemoteQCMImpl;

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
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	  RemoteQCMImpl.postNickname(nickname.getText().toString());
			        	  finish();
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() 
			       {
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