package org.remoteandroid.apps.qcm.ui;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.services.QCMService;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;

public class AnswerActivity extends Activity implements OnClickListener
{
	private AsyncTask<?, ?, ?>	mAsyncTask;
	private long startTime;
	private ProgressBar mTimeBar;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		startTime = System.currentTimeMillis();
		setContentView(R.layout.answer_layout);
		mTimeBar = (ProgressBar) findViewById(R.id.timeBar);
		int questionNumber = getIntent().getIntExtra("questionNumber", 0);
		mTimeBar.setMax(QCMService.TIME*1000);
		startUpdateTimeBar();
	}
	
	private void startUpdateTimeBar()
	{
		mAsyncTask = new AsyncTask<Void, Integer, Void>()
		{
			@Override
			protected Void doInBackground(Void... params)
			{
				int progresStatus;
				do
				{
					try
					{
						Thread.sleep(200);
					}
					catch (Exception e)
					{
						// TODO : A voir avec philippe
					}
					if (isCancelled())
						return null;
					progresStatus = (int) (((System.currentTimeMillis() - startTime)));
					publishProgress(progresStatus);
				} while (progresStatus < QCMService.TIME * 1000);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values)
			{
				super.onProgressUpdate(values);
				mTimeBar.setProgress(values[0]);
			}

			protected void onPostExecute(Void result)
			{
			};
		}.execute();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mAsyncTask.cancel(true);
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		
	}
	
}
