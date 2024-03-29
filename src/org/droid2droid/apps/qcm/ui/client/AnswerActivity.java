/******************************************************************************
 *
 * droid2droid - Distributed Android Framework
 * ==========================================
 *
 * Copyright (C) 2012 by Atos (http://www.http://atos.net)
 * http://www.droid2droid.org
 *
 ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
******************************************************************************/
package org.droid2droid.apps.qcm.ui.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.droid2droid.apps.qcm.R;
import org.droid2droid.apps.qcm.model.MultipleChoicesQuestion;
import org.droid2droid.apps.qcm.model.Question;
import org.droid2droid.apps.qcm.model.SimpleChoiceQuestion;
import org.droid2droid.apps.qcm.model.XMLParser;
import org.droid2droid.apps.qcm.remote.RemoteQCMImpl;
import org.droid2droid.apps.qcm.services.AbstractGameScreen;
import org.droid2droid.apps.qcm.services.QCMMasterService;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AnswerActivity extends AbstractGameScreen implements OnClickListener
{
	public static final String FINISH_RESPONSEACTIVITY = "org.remoteandroid.apps.qcm.FINISH_RESPONSEACTIVITY";
	private AsyncTask<?, ?, ?>	mAsyncTask;
	private long startTime;
	private ProgressBar mTimeBar;
	private LinearLayout answerView;
	Question question;
	RadioButton [] rb;
	CheckBox [] cb;
	private List<String> answers;
	private Button selectAnswer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null)
			startTime = System.currentTimeMillis();
		else
			startTime = savedInstanceState.getLong("startTime");
		setContentView(R.layout.answer_layout);
		mTimeBar = (ProgressBar) findViewById(R.id.timeBar);
		answerView = (LinearLayout)findViewById(R.id.answerView);
		selectAnswer = (Button)findViewById(R.id.select_answer);
		selectAnswer.setOnClickListener(this);
		int questionNumber = getIntent().getIntExtra("questionNumber", 0);
		try
		{
			question = new XMLParser(this).getQuestion(questionNumber);
			ArrayList<String> values = question.getValues();
			
			if(XMLParser.SINGLE.equals(question.getType()))
			{
				rb = new RadioButton[values.size()]; 
				RadioGroup radiogroup = new RadioGroup(this);
				radiogroup.setOrientation(RadioGroup.VERTICAL);
				for(int i=0; i< values.size(); i++)
				{
					rb[i] = new RadioButton(this);
					rb[i].setText(values.get(i));
					radiogroup.addView(rb[i]);
				}
				answerView.addView(radiogroup);
			}
			else
			{
				cb = new CheckBox[values.size()];
				for(int i=0; i< values.size(); i++)
				{
					cb[i] = new CheckBox(this);
					cb[i].setText(values.get(i));
					answerView.addView(cb[i]);
				}				
			}
			
				
		} catch (XmlPullParserException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mTimeBar.setMax(QCMMasterService.TIME*1000);
		
//		answerView.
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
				} while (progresStatus < QCMMasterService.TIME * 1000);
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values)
			{
				super.onProgressUpdate(values);
				mTimeBar.setProgress(values[0]);
			}

			@Override
			protected void onPostExecute(Void result)
			{
				RemoteQCMImpl.postResults(new ArrayList<String>(0));
				finish();
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
	public void onClick(View view)
	{
		if(view==selectAnswer)
		{
			if(XMLParser.SINGLE.equals(question.getType()))
			{
				answers = new ArrayList<String>(1);
				SimpleChoiceQuestion sQuestion = (SimpleChoiceQuestion) question;
				for(int i=0; i<sQuestion.getValues().size();i++)
					if(rb[i].isChecked())
						answers.add(rb[i].getText().toString());
			}
			else
			{
				answers = new ArrayList<String>();
				MultipleChoicesQuestion mQuestion = (MultipleChoicesQuestion) question;
				for(int i=0; i<mQuestion.getValues().size();i++)
					if(cb[i].isChecked())
						answers.add(cb[i].getText().toString());
			}
			RemoteQCMImpl.postResults(answers);
			finish();
		}
		
	}
	
	@Override
	public void onBackPressed()
	{
		RemoteQCMImpl.postResults(null);
		finish();
	}

	@Override
	public void onReceiveForService(Bundle resultData)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putLong("startTime", startTime);
	}
	
}
