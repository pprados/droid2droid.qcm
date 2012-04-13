package org.remoteandroid.apps.qcm.ui;

import java.io.IOException;
import java.util.ArrayList;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.model.Question;
import org.remoteandroid.apps.qcm.model.XMLParser;
import org.remoteandroid.apps.qcm.services.QCMService;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class QuestionActivity extends Activity
{
	private AsyncTask<?, ?, ?>	mAsyncTask;
	private long  startTime;
	private ProgressBar mTimeBar;
	private TextView questionMessage, listChoice;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		startTime = System.currentTimeMillis();
		setContentView(R.layout.question_layout);
		mTimeBar = (ProgressBar) findViewById(R.id.timeBar);
		questionMessage = (TextView) findViewById(R.id.question);
		listChoice = (TextView) findViewById(R.id.answer_list);
		int questionNumber = getIntent().getIntExtra("questionNumber", 0);
		mTimeBar.setMax(QCMService.TIME*1000);
		startUpdateTimeBar();
		try
		{
			Question question = new XMLParser(this).getQuestion(questionNumber);
			questionMessage.setText(question.getMessage());
			ArrayList<String> values = question.getValues();
			StringBuilder choices = new StringBuilder();
			for(String choice : values)
			{
				choices.append(" - "+choice+"\n");
			}
			listChoice.setText(choices);
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
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
}
