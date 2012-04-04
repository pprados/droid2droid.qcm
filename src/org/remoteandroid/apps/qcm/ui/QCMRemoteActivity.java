package org.remoteandroid.apps.qcm.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.remoteandroid.RemoteAndroidManager;
import org.remoteandroid.apps.qcm.R;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;

public class QCMRemoteActivity extends SherlockListActivity implements OnClickListener
{
	public static final String SUSCRIBE = "org.remoteandroid.apps.qcm.REGISTER";

	private static final int MINI = 250;

	private static final int MAXI = 700;

	private ImageButton mQrcodeButton;

	private ListView list;

	private List<String> players = new ArrayList<String>();

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(
			mReceiver, new IntentFilter(SUSCRIBE));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_start_layout);
		mQrcodeButton = (ImageButton) findViewById(R.id.qrcodeButton);
		mQrcodeButton.setOnClickListener(this);
		mQrcodeButton.setImageBitmap(getOwnQRCodeFromRA(MINI));
		list = (ListView) findViewById(R.id.listView);

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
			String nickname = intent.getExtras().getString(
				"nickname");
			boolean master = intent.getExtras().getBoolean(
				"master");
			players.add(nickname);
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
		Log.d(
			"TAG", "HERE");
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
	}

}
