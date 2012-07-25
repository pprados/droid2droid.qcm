package org.droid2droid.apps.qcm.ui.master;

import static org.droid2droid.Droid2DroidManager.QRCODE_URI;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.droid2droid.apps.qcm.R;
import org.droid2droid.apps.qcm.services.QCMMasterService;
import org.droid2droid.apps.qcm.tools.NfcSherlockActivity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NfcEvent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class QCMMasterActivity extends NfcSherlockActivity implements OnClickListener
{
	public static final String REGISTER = "org.remoteandroid.apps.qcm.REGISTER";
	public static final boolean ADD_PLAYER = true;
	public static final boolean REMOVE_PLAYER = false;
	
	public static final int MANAGE_PLAYER = 0;
	
	private static final String BORNE = "Borne" ;
	
	private static final int MINI = 200;

	private static final int MAXI = 700;

	private ImageButton mQrcodeButton; 
	private Button mStartGame;

	private ListView list;
	
	private ArrayList<String> players;
	ArrayAdapter<String> mAdapter;
	private TextView master_name;
	private Resources resources;
	

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver, new IntentFilter(REGISTER));
		startService(new Intent(QCMMasterService.SEND_PLAYER_LIST));
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
		if(savedInstanceState!=null)
			players = savedInstanceState.getStringArrayList("players");
		else
			players = new ArrayList<String>();
		if(players.size()==0)
			mStartGame.setEnabled(false);
		else
			mStartGame.setEnabled(true);
		mQrcodeButton.setImageBitmap(getOwnQRCodeFromRA(MINI));
		master_name = (TextView)findViewById(R.id.master_game);
		list = (ListView) findViewById(R.id.listView);
		mAdapter = new ArrayAdapter<String>(QCMMasterActivity.this, android.R.layout.simple_list_item_1,android.R.id.text1, players );
		list.setAdapter(mAdapter);
		Intent intent = new Intent(this, QCMMasterService.class);
		startService(intent);
		resources = getResources();
	
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent)
		{
			players.clear();
			players.addAll(intent.getStringArrayListExtra("playersNickname"));
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
			mAdapter.notifyDataSetChanged();
		};
	};

	@TargetApi(11)
	private Bitmap getOwnQRCodeFromRA(final int size)
	{
		Bitmap scaBitmap = null;
		try
		{
			InputStream in;
			if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
			{
				in = getContentResolver().openTypedAssetFileDescriptor(
					QRCODE_URI, "image/png", null).createInputStream();
			}
			else
			{
				in = getContentResolver().openInputStream(
					QRCODE_URI);
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
			final Dialog dialog = new Dialog(QCMMasterActivity.this);
			dialog.setContentView(R.layout.qrcode_dialog);
			dialog.setTitle(QCMMasterActivity.this.getResources().getString(
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
			startService(new Intent(QCMMasterService.REMOTE_START_GAME));
		}
	}
	
	@Override
	public void onBackPressed()
	{
		startService(new Intent(QCMMasterService.QUIT));
		finish();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putStringArrayList("players", players);
	}

	@Override
	@TargetApi(14)
	public NdefMessage createNdefMessage(NfcEvent event)
	{
		return (QCMMasterService.mManager!=null) ? QCMMasterService.mManager.createNdefMessage() : null;
	}
}
