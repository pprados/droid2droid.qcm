package org.droid2droid.apps.qcm.remote;
interface RemoteQCM
{
	String subscribe();
	boolean starPlayRequest(int number);
	void leaveMaster();
	List<String> play(int question, long startTime);	
	void exit();
	void startAndStopResultScreen(String winner,String player, int score, boolean manage);
	void displayWinner(in List<String> winners);
	boolean restart(in List<String> winners);
}