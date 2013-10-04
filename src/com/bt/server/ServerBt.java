package com.bt.server;

import com.bt.service.Servicebt;

public class ServerBt {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	
		
	Thread service = new Thread(new Servicebt());
	service.start();
	}

}
