package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.remote.RemoteQCMImpl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MasterActivity extends Activity implements OnClickListener
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

}
