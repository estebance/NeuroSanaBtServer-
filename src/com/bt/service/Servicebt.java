package com.bt.service;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Servicebt implements Runnable    
{
	
	public Servicebt()
	{
		
	}

	@Override
	public void run() 
	{
	 waitconnection();	
	}
	
	
  public void waitconnection()
  {
	
	LocalDevice local = null;
	StreamConnectionNotifier notifier_connection = null ;
	StreamConnection connection;

	/////
	
	try
	{
	
	local = LocalDevice.getLocalDevice();
	local.setDiscoverable(DiscoveryAgent.GIAC);
    notifier_connection = (StreamConnectionNotifier)Connector.open("btspp://localhost:"+ new UUID("0000110100001000800000805F9B34FD",false).toString() + ";name=btservice");		
	}
	
	////
	
	catch(Exception e)
	{
	e.printStackTrace();	
	}
	
	///// 
	
  while(true)
  {
	try
	{
	System.out.println("esperando por una conexion...");
	connection = notifier_connection.acceptAndOpen();	
	Thread processThread = new Thread(new ManageConnectionThread(connection));
	processThread.start();
	}
	catch(Exception e)
	{
	System.out.println("error"+e);
	}
   }
	
  }

}
