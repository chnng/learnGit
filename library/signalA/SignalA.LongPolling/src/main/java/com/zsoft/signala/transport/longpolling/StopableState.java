package com.zsoft.signala.transport.longpolling;

import com.zsoft.signala.ConnectionBase;
import com.zsoft.signala.transport.StateBase;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class StopableState extends StateBase {
	protected AtomicBoolean requestStop = new AtomicBoolean(false);
    protected final String TRANSPORT_NAME = "LongPolling";

	
	public StopableState(ConnectionBase connection) {
		super(connection);
	}

	protected boolean DoStop()
	{
		if(requestStop.get()) 
		{
            mConnection.SetNewState(new DisconnectedState(mConnection));
            return true;
		}
		return false;
	}
	

}
