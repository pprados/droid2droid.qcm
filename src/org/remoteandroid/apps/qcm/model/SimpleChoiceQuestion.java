package org.remoteandroid.apps.qcm.model;

import java.util.ArrayList;

public class SimpleChoiceQuestion extends Question
{
	private String answer;
	public SimpleChoiceQuestion()
	{
		super();
		this.answer = null;
	}
	public SimpleChoiceQuestion(String type, String message, ArrayList<String> values, String answer)
	{
		super(type, message, values);
		this.answer = answer;
	}
	
	public String getAnswer()
	{
		return answer;
	}
	public void setAnswer(String answer)
	{
		this.answer = answer;
	}
	

}
