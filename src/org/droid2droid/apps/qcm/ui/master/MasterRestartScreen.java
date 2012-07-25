package org.droid2droid.apps.qcm.ui.master;

import java.util.ArrayList;
import java.util.Iterator;

import org.droid2droid.apps.qcm.R;
import org.droid2droid.apps.qcm.services.AbstractGameScreen;
import org.droid2droid.apps.qcm.services.QCMMasterService;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MasterRestartScreen extends AbstractGameScreen implements OnClickListener
{
	private LinearLayout result_layout;
	Button submit;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_screen_restart);
		result_layout = (LinearLayout)findViewById(R.id.winner_layout);
		submit = (Button) findViewById(R.id.restart_game);
		submit.setOnClickListener(this);
		Intent intent = getIntent();
		ArrayList<String> winners = intent.getStringArrayListExtra("winners");
		if(winners!=null)
			for (Iterator<String> i = winners.iterator(); i.hasNext();)
			{
				TextView winner = new TextView(getApplicationContext());
				winner.setText(i.next());
				result_layout.addView(winner);
			}
		else if(winners.size()==0)
			finish();
	}
	@Override
	public void onClick(View view)
	{
		if(view==submit)
		{
			startService(new Intent(QCMMasterService.REMOTE_START_GAME));
			finish();
		}
		
	}
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		finish();
		
	}

}
