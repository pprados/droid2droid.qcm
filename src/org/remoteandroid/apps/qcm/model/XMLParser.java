package org.remoteandroid.apps.qcm.model;

import java.io.IOException;
import java.util.ArrayList;

import org.remoteandroid.apps.qcm.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

public class XMLParser
{
	public static final int MAX_QUESTION = 5;
	public static final int  TOTAL_NUMBER_OF_QUESTION= 10;
	public static final String SINGLE = "radio";
	public static final String MULTIPLE = "check";
	public static final String QUESTION="question";
	public static final String VALUE="value";
	private XmlResourceParser model;
	
	public XMLParser(Context context)
	{
		model = context.getResources().getXml(R.xml.model);
	}
	
	public Question getQuestion(int questionNumber) throws XmlPullParserException, IOException
	{
		Question question;
		int eventType = model.getEventType();
		String type = null;
		boolean rightQuestion = false;
		boolean rightAnswer = false;
		String message = null;
		ArrayList<String> answers = new ArrayList<String>();
		String answer = null;
		ArrayList<String> values = new ArrayList<String>();
		while (eventType != XmlPullParser.END_DOCUMENT)
		{
			if (eventType == XmlPullParser.START_DOCUMENT)
			{
				Log.d("XML","Start document");
			}
			else if (eventType == XmlPullParser.START_TAG)
			{
				System.out.println("Start tag " + model.getName());
				if(QUESTION.equals(model.getName()) && String.valueOf(questionNumber).equals(model.getAttributeValue(0)) && !rightQuestion)
				{
					rightQuestion = true;
					if(SINGLE.equals(model.getAttributeValue(0)))
						type = SINGLE;
							
					else
						type = MULTIPLE;
				}
				
				if(VALUE.equals(model.getName()) && model.getAttributeCount()>0 && rightQuestion)
					rightAnswer = true;
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				System.out.println("End tag " + model.getName());
				if(rightQuestion && QUESTION.equals(model.getName()))
					break;
			}
			else if (eventType == XmlPullParser.TEXT)
			{
				System.out.println("Text " + model.getText());
				if(rightQuestion)
				{
					if(message==null)
						message = model.getText();
					else
						values.add(model.getText());
					if(rightAnswer)
					{
						if(SINGLE.equals(type))
							answer = model.getText();
						else 
							answers.add(model.getText());
						rightAnswer = false;
					}
				}
					
			}
			eventType = model.next();
        }
		
		if(SINGLE.equals(type))
		{
			question = new SimpleChoiceQuestion(type, message,values, answer);
			return question;
		}
			
		else
		{
			question = new MultipleChoicesQuestion(type, message, values, answers);
			return question;
		}
			
	}
}
