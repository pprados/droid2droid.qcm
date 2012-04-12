package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;

import android.app.Activity;
import android.os.Bundle;

public class WaitingActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_layout);
	}
	@Override
	public void onBackPressed()
	{
	}

}
