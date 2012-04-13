package org.remoteandroid.apps.qcm.model;

import java.util.ArrayList;

public class Question
{
	protected String type;
	protected String message;
	protected ArrayList<String> values;
	public Question()
	{
		this.type = null;
		this.message = null;
		this.values = new ArrayList<String>();
	}
	public Question(String type, String message, ArrayList<String> values)
	{
		this.type = type;
		this.message = message;
		this.values = values;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
	public ArrayList<String> getValues()
	{
		return values;
	}
	public void setValues(ArrayList<String> values)
	{
		this.values = values;
	}
	

}
