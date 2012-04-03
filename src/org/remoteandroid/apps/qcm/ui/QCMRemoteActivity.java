package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class QCMRemoteActivity extends SherlockActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_start_layout);
	}

}
