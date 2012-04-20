package org.remoteandroid.apps.qcm.ui.client;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.services.AbstractGameScreen;

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
		int score = intent.getExtras().getInt("score");
		if(winner!=null)
		{
			mImage.setImageResource(R.drawable.win);
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
		// TODO Auto-generated method stub
		
	}
	
}