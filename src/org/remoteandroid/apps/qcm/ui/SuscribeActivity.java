package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

public class SuscribeActivity extends SherlockActivity implements OnClickListener
{
//	private static final int 	REQUEST_CONNECT_CODE=1;
	private Button playButton;
	private EditText nickname;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suscribe_layout);
		playButton = (Button)findViewById(R.id.play_button);
		playButton.setOnClickListener(this);
		nickname = (EditText)findViewById(R.id.edt_pseudo);
	}
	@Override
	public void onClick(View v)
	{
		if(!("").equals(nickname.getText().toString()))
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Do you really want to submit your nickname to the server?")
			       .setCancelable(false)
			       .setTitle("Confirmation")
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.setCanceledOnTouchOutside(false);
			alert.show();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Bad nickname please retry ")
			       .setCancelable(false)
			       .setTitle("Error")
			       .setPositiveButton("Retry", new DialogInterface.OnClickListener() 
			       {
			           public void onClick(DialogInterface dialog, int id) 
			           {
			        	   dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.setCanceledOnTouchOutside(false);
			alert.show();
		}
		
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.main_activity, menu);
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		switch (item.getItemId()) 
//	    {
//		    case R.id.menu_connect:
//				Intent intent=new Intent(RemoteAndroidManager.ACTION_CONNECT_ANDROID);
//				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//				{
//					intent.putExtra(RemoteAndroidManager.EXTRA_THEME_ID,android.R.style.Theme_Holo_Light_DarkActionBar);
//				}
//				else
//				{
//					intent.putExtra(RemoteAndroidManager.EXTRA_THEME_ID,android.R.style.Theme_Holo_Light_NoActionBar);
//				}
//		    	startActivityForResult(intent, REQUEST_CONNECT_CODE);
//		        return true;
//		    default:
//		        return super.onOptionsItemSelected(item);
//	    }
//		
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data)
//	{
//		if (requestCode==REQUEST_CONNECT_CODE && resultCode==Activity.RESULT_OK)
//		{
//			RemoteAndroidInfo info=(RemoteAndroidInfo)data.getParcelableExtra(RemoteAndroidManager.EXTRA_DISCOVER);
//			startService(new Intent(MultiConnectionService.ACTION_ADD_DEVICE)
//				.putExtra(RemoteAndroidManager.EXTRA_DISCOVER,info)
//				.putExtra(RemoteAndroidManager.EXTRA_UPDATE, data.getBooleanExtra(RemoteAndroidManager.EXTRA_UPDATE, false)));
//		}
//	}
}