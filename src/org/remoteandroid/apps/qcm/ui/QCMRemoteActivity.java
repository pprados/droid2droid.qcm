package org.remoteandroid.apps.qcm.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.remoteandroid.RemoteAndroidManager;
import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.model.Question;
import org.remoteandroid.apps.qcm.model.XMLParser;
import org.remoteandroid.apps.qcm.services.QCMService;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

public class QCMRemoteActivity extends SherlockActivity implements OnClickListener
{
	public static final String REGISTER = "org.remoteandroid.apps.qcm.REGISTER";
	public static final boolean ADD_PLAYER = true;
	public static final boolean REMOVE_PLAYER = false;
	
	private static final String BORNE = "Borne" ;
	
	private static final int MINI = 200;

	private static final int MAXI = 700;

	private ImageButton mQrcodeButton; 
	private Button mStartGame;

	private ListView list;
	
	private List<String> players = new ArrayList<String>();
	ArrayAdapter<String> mAdapter;
	private TextView master_name;
	private Resources resources;

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(REGISTER));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_start_layout);
		mQrcodeButton = (ImageButton) findViewById(R.id.qrcodeButton);
		mStartGame = (Button) findViewById(R.id.remote_start);
		mQrcodeButton.setOnClickListener(this);
		mStartGame.setOnClickListener(this);
		if(players.size()==0)
			mStartGame.setEnabled(false);
		else
			mStartGame.setEnabled(true);
		mQrcodeButton.setImageBitmap(getOwnQRCodeFromRA(MINI));
		master_name = (TextView)findViewById(R.id.master_game);
		list = (ListView) findViewById(R.id.listView);
		mAdapter = new ArrayAdapter<String>(QCMRemoteActivity.this, android.R.layout.simple_list_item_1,android.R.id.text1, players );
		list.setAdapter(mAdapter);
		startService(new Intent(this, QCMService.class));
		resources = getResources();
//		Question  question= new XMLParser(this).getQuestion(7);
	
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		public void onReceive(android.content.Context context, android.content.Intent intent)
		{
			String nickname = intent.getExtras().getString("nickname");
			boolean type = intent.getExtras().getBoolean("type");
			if(ADD_PLAYER==type)
				players.add(nickname);
			else
				players.remove(nickname);
			
			mAdapter.notifyDataSetChanged();
			if(players.size() >= 1)
			{
				master_name.setText(players.get(0));
				mStartGame.setEnabled(true);
			}
				
			else
			{
				master_name.setText(resources.getText(R.string.no_master_game));
				mStartGame.setEnabled(false);
			}
				
			
		};
	};

	private Bitmap getOwnQRCodeFromRA(final int size)
	{
		Bitmap scaBitmap = null;
		try
		{
			InputStream in;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			{
				in = getContentResolver().openTypedAssetFileDescriptor(
					RemoteAndroidManager.QRCODE_URI, "image/png", null).createInputStream();
			}
			else
			{
				in = getContentResolver().openInputStream(
					RemoteAndroidManager.QRCODE_URI);
			}
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			in.close();
			scaBitmap = Bitmap.createScaledBitmap(
				bitmap, size, size, false);
			bitmap.recycle();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return scaBitmap;
	}

	@Override
	public void onClick(View view)
	{
		if (view == mQrcodeButton)
		{
			final Dialog dialog = new Dialog(QCMRemoteActivity.this);
			dialog.setContentView(R.layout.qrcode_dialog);
			dialog.setTitle(QCMRemoteActivity.this.getResources().getString(
				R.string.title_qrcode_dialog));
			ImageView image = (ImageView) dialog.findViewById(R.id.qrcode_dialog_image);
			image.setImageBitmap(getOwnQRCodeFromRA(MAXI));
			Button dialog_quit_button = (Button) dialog.findViewById(R.id.qrcode_dialog_quit_button);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			dialog_quit_button.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.cancel();

				}
			});
		}
		else if(view == mStartGame)
		{
			master_name.setText(BORNE);
			startService(new Intent(QCMService.REMOTE_START_GAME));
		}
	}
}
