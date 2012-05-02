package org.remoteandroid.apps.qcm.ui.client;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.services.AbstractGameScreen;

import android.os.Bundle;

public class WaitingActivity extends AbstractGameScreen
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waiting_layout);
	}
	@Override
	public void onReceiveForService(Bundle resultData)
	{
		// TODO Auto-generated method stub
		
	}
}
