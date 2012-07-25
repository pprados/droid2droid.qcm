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