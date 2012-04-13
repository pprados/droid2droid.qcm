package org.remoteandroid.apps.qcm.model;

import java.util.ArrayList;

public class MultipleChoicesQuestion extends Question
{
	private ArrayList<String> answers;
	public MultipleChoicesQuestion()
	{
		super();
		this.answers = new ArrayList<String>();
	}
	public MultipleChoicesQuestion(String type, String message, ArrayList<String> values, ArrayList<String> answers)
	{
		super(type, message, values);
		this.answers = answers;
	}
	public ArrayList<String> getAnswers()
	{
		return answers;
	}
	public void setAnswers(ArrayList<String> answers)
	{
		this.answers = answers;
	}
}
