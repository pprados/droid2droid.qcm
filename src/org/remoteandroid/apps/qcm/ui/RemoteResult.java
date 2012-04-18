package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class RemoteResult extends SherlockActivity
{
	private ImageView mImage;
	private TextView mWinner;
	public static final String FINISH = "org.remoteandroid.apps.qcm.FINISH";
	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(FINISH));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_remote_layout);
		mImage = (ImageView)findViewById(R.id.winner_image);
		mWinner = (TextView) findViewById(R.id.winnerName);
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
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		public void onReceive(android.content.Context context, android.content.Intent intent)
		{
			finish();
		};
	};
	
	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
	}


}
