package org.remoteandroid.apps.qcm.ui.master;

import java.util.ArrayList;
import java.util.Iterator;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.services.QCMMasterService;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class MasterRestartScreen extends SherlockActivity implements OnClickListener
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
		for (Iterator<String> i = winners.iterator(); i.hasNext();)
		{
			TextView winner = new TextView(getApplicationContext());
			winner.setText(i.next());
			result_layout.addView(winner);
		}
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

}
