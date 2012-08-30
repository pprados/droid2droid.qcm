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
package org.droid2droid.apps.qcm.model;

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
