package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class ClientResult extends SherlockActivity
{
	private ImageView image;
	private TextView winner, score;
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
		setContentView(R.layout.result_client_layout);
		image = (ImageView)findViewById(R.id.winner_image);
		winner = (TextView) findViewById(R.id.winnerName);
		score = (TextView) findViewById(R.id.playerscore);
//		Intent intent = getIntent();
		
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
