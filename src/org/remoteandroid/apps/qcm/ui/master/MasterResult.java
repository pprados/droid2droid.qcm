package org.remoteandroid.apps.qcm.ui.master;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.services.AbstractGameScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MasterResult extends AbstractGameScreen
{
	private ImageView mImage;
	private TextView mWinner;
	public static final String FINISH = "org.remoteandroid.apps.qcm.FINISH";
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
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		finish();
		
	}
	
}
