package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.RemoteAndroidInfo;
import org.remoteandroid.RemoteAndroidManager;
import org.remoteandroid.apps.qcm.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class SuscribeActivity extends SherlockActivity
{
	private static final int 	REQUEST_CONNECT_CODE=1;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suscribe_layout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_activity, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) 
	    {
		    case R.id.menu_connect:
				Intent intent=new Intent(RemoteAndroidManager.ACTION_CONNECT_ANDROID);
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				{
					intent.putExtra(RemoteAndroidManager.EXTRA_THEME_ID,android.R.style.Theme_Holo_Light_DarkActionBar);
				}
				else
				{
					intent.putExtra(RemoteAndroidManager.EXTRA_THEME_ID,android.R.style.Theme_Holo_Light_NoActionBar);
				}
		    	startActivityForResult(intent, REQUEST_CONNECT_CODE);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode==REQUEST_CONNECT_CODE && resultCode==Activity.RESULT_OK)
		{
			RemoteAndroidInfo info=(RemoteAndroidInfo)data.getParcelableExtra(RemoteAndroidManager.EXTRA_DISCOVER);
//			startService(new Intent(MultiConnectionService.ACTION_ADD_DEVICE)
//				.putExtra(RemoteAndroidManager.EXTRA_DISCOVER,info)
//				.putExtra(RemoteAndroidManager.EXTRA_UPDATE, data.getBooleanExtra(RemoteAndroidManager.EXTRA_UPDATE, false)));
		}
	}
}