package com.bt.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import com.bt.loadfile.ChargeFile;
import com.bt.server.ServerBt;


//hilo para el manejo de la conexion bluetooth 

public class ManageConnectionThread extends Thread 
{
	private StreamConnection My_Connection;		
   // private  OutputStream MyOutStream;
    private InputStream MyInputStream;
    private OutputStream MyOutputStream;
    private static final int COMANDO_SALIR = 1;
	private static final int COMANDO_ENVIAR = 2;
	private static final int COMANDO_CANCELAR=3;
	private static final int COMANDO_INICIAR=4; ;
	private static final int COMANDO_TERMINADO=5;
	private static final int COMANDO_CANCELADO=6;
	private static final int COMANDO_ERROR = -1; 
	
	
	
	
	
   public ManageConnectionThread( StreamConnection connection )
	{
	  My_Connection = connection;	 
    }
	  
	@Override
	public void run() 
	{
	
	try 
	{
		System.out.println("esperando una solicitud por parte del cliente");	
	    MyInputStream = My_Connection.openInputStream();
		MyOutputStream = My_Connection.openOutputStream();	
		
		int comando = 0;
		
		while(true)
		{			
		comando = MyInputStream.read();
		System.out.println("comando que llega" + comando);
		
		if(comando == COMANDO_SALIR)
		{
		System.out.println("salimos del servidor");
		closeall();
		break; 
		}
		
		if(comando == -1)
		{
	
		closeall();
		resume_connection();	
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
		
	closeall();	
		
	}

	private void processCommand(int command) {
		// TODO Auto-generated method stub
		System.out.println("el proceso es " + command);
		
		try
		{
		
		if(command == COMANDO_ENVIAR)
		{	
	    ChargeFile sendata = new ChargeFile();
	    /////// como capturar la ruta del archivo generado por insuasty; 
	    sendata.SetRouteData("C:/Users/sarita/Desktop/hola.txt");
	    byte[] informacion = sendata.DataFile();
	    String nombrearchivo = sendata.GetNameFile();
	    
	    
	    MyOutputStream.write(2);
        MyOutputStream.write(nombrearchivo.getBytes("UTF-8")); 
        MyOutputStream.write(200);
        MyOutputStream.write(informacion);
        
        
        /*other*/

        /*
        
        BufferedOutputStream bos= new BufferedOutputStream(MyOutputStream);
        FileInputStream  a = sendata.DataFile_f();
        BufferedInputStream bis = new BufferedInputStream(a);
        int n=-1;
        byte[] buffer = new byte[8192];
        while((n = bis.read(buffer))>-1)
        { 
        bos.write(buffer,0,n);
        }
        
        a.close();
        bis.close();
        bos.flush();
        bos.close();
       */ 
        
        }
        
        


		if(command == COMANDO_INICIAR)
		{		
		/* ordenes para iniciar la captura de archivos cuando finalmente termine 
		   respondera con un comando terminado*/		   	
		MyOutputStream.write(COMANDO_TERMINADO);
	    }
		
		if(command == COMANDO_CANCELAR)
		{
		/* ordenes para cancelar la toma de archivos se supone  este deberia buscar 
		* el archivo hasta el momento generado y borrarlo	
		*/
		MyOutputStream.write(COMANDO_CANCELADO);
	    }
		
	   }
		
	   catch(Exception e)
	   {
		
	    System.out.println("error en process command" +e);
	    e.printStackTrace();
	   }
		

		
	}
	
	
	private void closeall()
	{
		
	try
	{
		MyOutputStream.close();
		MyInputStream.close();
		My_Connection.close();
		
	} 
	
	catch (IOException e) 
	
	{
		System.out.println("error al momento de intentar cerrar conexion");
		e.printStackTrace();
	}
	
		
	}
	
	
	private void resume_connection()
	{
    ServerBt service = new ServerBt();
	}


   

}


