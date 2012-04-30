package org.remoteandroid.apps.qcm.ui.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.remoteandroid.apps.qcm.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class WinnerStaticScreen extends SherlockActivity
{
	private LinearLayout result_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.winner_screen_static);
		result_layout = (LinearLayout)findViewById(R.id.winner_layout);
		Intent intent = getIntent();
		ArrayList<String> winners = intent.getStringArrayListExtra("winners");
		if(winners!=null)
			for (Iterator<String> i = winners.iterator(); i.hasNext();)
			{
				TextView winner = new TextView(getApplicationContext());
				winner.setText(i.next());
				result_layout.addView(winner);
			}
	}

}
