package org.remoteandroid.apps.qcm.remote;
interface RemoteQCM
{
	String subscribe();
	boolean starPlayRequest(int number);
	void leaveMaster();
	String play(int question, long startTime);	
}