package com.bt.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import com.bt.loadfile.ChargeFile;


//hilo para el manejo de la conexion bluetooth 

public class ManageConnectionThread implements Runnable 
{
	StreamConnection My_Connection;		
    private  OutputStream MyOutStream;
    private  InputStream MyInputStream;
    private static final int COMANDO_SALIR = 1;
	private static final int COMANDO_ENVIAR = 2;
	private static final int COMANDO_CANCELAR=3;
	private static final int COMANDO_INICIAR=4; ;
	private static final int COMANDO_TERMINADO=5;
	private static final int COMANDO_CANCELADO=5;
	
	
	
	
   public ManageConnectionThread( StreamConnection connection )
	{
	  My_Connection = connection;	 
      InputStream tmpInput = null;
      OutputStream tmpOutput = null;
      try 
      {
          tmpInput = My_Connection.openInputStream();
          tmpOutput = My_Connection.openOutputStream();
      } 
      catch (IOException e) {}

      MyInputStream = tmpInput;
      MyOutStream = tmpOutput;
  }
	  
	  
	  
	
	
	
	@Override
	public void run() 
	{
	
	try 
	{
		System.out.println("esperando una solicitud por parte del cliente");
		DataInputStream entrada = new DataInputStream(My_Connection.openInputStream());			
		while(true)
		{			
		System.out.println(entrada.readUTF());			
		int comando = Integer.parseInt(entrada.readUTF());
	    if(comando == COMANDO_SALIR)
		{
		System.out.println("salimos del servidor");
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
         DataOutputStream flujo= new DataOutputStream( MyOutStream );
         flujo.write(informacion);
         // cerramos para evitar problemas
         flujo.close();
        } 
        
        catch (IOException e) { }			
		}
		
		if(command == COMANDO_INICIAR)
		{
		
		/* ordenes para iniciar la captura de archivos cuando finalmente termine 
		   respondera con un comando terminado*/	
	     try 
		      {
		      DataOutputStream flujo= new DataOutputStream(MyOutStream);
		      flujo.writeUTF(Integer.toString(COMANDO_TERMINADO));
		      flujo.close();
		      } 
		      catch (IOException e) 
		      {
		 	  System.out.println("error al responder la solicitud:"+e);    
		      }	
		}
		
		if(command == COMANDO_CANCELAR)
		{
			/* ordenes para cancelar la toma de archivos se supone  este deberia buscar 
			 * el archivo hasta el momento generado y borrarlo	
			 */
		 try 
	     {
			      DataOutputStream flujo= new DataOutputStream(MyOutStream);
			      flujo.writeUTF(Integer.toString(COMANDO_CANCELADO));
			      flujo.close();
	     } 
	     catch (IOException e) 
	     {
		  System.out.println("error al responder la solicitud:"+e);    
	     }			
			
		}
		
		
	}
	
	
	private void closeall()
	{
		
	try
	{
		MyOutStream.close();
		MyInputStream.close();
		// puede que este no funcione 
		My_Connection.close();
	} 
	
	catch (IOException e) 
	
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
	}


   

}


