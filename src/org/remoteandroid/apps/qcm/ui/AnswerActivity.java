package org.remoteandroid.apps.qcm.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.remoteandroid.apps.qcm.R;
import org.remoteandroid.apps.qcm.model.MultipleChoicesQuestion;
import org.remoteandroid.apps.qcm.model.Question;
import org.remoteandroid.apps.qcm.model.SimpleChoiceQuestion;
import org.remoteandroid.apps.qcm.model.XMLParser;
import org.remoteandroid.apps.qcm.remote.RemoteQCMImpl;
import org.remoteandroid.apps.qcm.services.QCMService;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
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

public class AnswerActivity extends Activity implements OnClickListener
{
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
		startTime = System.currentTimeMillis();
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
		mTimeBar.setMax(QCMService.TIME*1000);
		
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
						answers.add(rb[i].getText().toString());
			}
			RemoteQCMImpl.postResults(answers);
			finish();
		}
		
	}
	
}
