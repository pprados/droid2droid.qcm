package org.remoteandroid.apps.qcm.remote;
interface RemoteQCM
{
	String subscribe();
	boolean starPlay(int number);
	int play(int question, long startTime);	
}