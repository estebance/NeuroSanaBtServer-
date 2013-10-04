package com.bt.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import com.bt.loadfile.ChargeFile;

public class ManageConnectionThread implements Runnable 
{

	StreamConnection mConnection;	
	
    private  OutputStream mmOutStream;
    private  InputStream mmInputStream;
    private static final int COMANDO_SALIR = 1;
	private static final int COMANDO_ENVIAR = 2;
	
	
	
	
	public ManageConnectionThread( StreamConnection connection )
	{
	  mConnection = connection;	 
      InputStream tmpIn = null;
      OutputStream tmpOut = null;
      try 
      {
          tmpIn = mConnection.openInputStream();
          tmpOut = mConnection.openOutputStream();
      } 
      catch (IOException e) {}

      mmInputStream = tmpIn;
      mmOutStream = tmpOut;
  }
	  
	  
	  
	
	
	
	@Override
	public void run() 
	{
	
	try 
	{
		System.out.println("esperando una solicitud");
		DataInputStream entrada = new DataInputStream(mConnection.openInputStream());		
		
		while(true)
		{			
		System.out.println(entrada.readUTF());			
	
		int comando = Integer.parseInt(entrada.readUTF());

	    if(comando == COMANDO_SALIR)
		{
		System.out.println("salimos");
		closeall();
		break; 
		}

	    else
	    {
	    processCommand(comando);		
	    }
	    
		}
		
	}
	catch(Exception e)
	{
    System.out.println("se esta presentando un error al momento de ejecutar ordenes");
	e.printStackTrace();	
	}
		
		
		
	}

	private void processCommand(int command) {
		// TODO Auto-generated method stub
		System.out.println("el proceso es " + command);
		
		if(command == COMANDO_ENVIAR)
		{	
	    ChargeFile sendata = new ChargeFile();
	    /////// como capturar la ruta del archivo generado por insuasty; 
	    sendata.SetRouteData("una ruta");
	    byte[] informacion = sendata.DataFile();
	    String nombrearchivo = sendata.GetNameFile();
	       
        try 
        {	
         DataOutputStream flujo= new DataOutputStream( mmOutStream );
         flujo.write(informacion);
         // cerramos para evitar problemas
         flujo.close();
        } 
        
        catch (IOException e) { }
				
		}
	}
	
	
	private void closeall()
	{
		
	try
	{
		mmOutStream.close();
		mmInputStream.close();
		// puede que este no funcione 
		mConnection.close();
	} 
	
	catch (IOException e) 
	
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
	}


   

}


