package org.remoteandroid.apps.qcm.remote;

import android.content.Context;
import android.os.RemoteException;

public class RemoteQCMImpl extends RemoteQCM.Stub
{
	private Context mContext;
	public RemoteQCMImpl(Context context)
	{
		this.mContext = context;
	}

	@Override
	public void exit() throws RemoteException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void standby() throws RemoteException
	{
		// TODO Auto-generated method stub
		
	}

}
