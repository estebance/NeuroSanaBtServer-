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
    private InputStream MyInputStream;
    private OutputStream MyOutputStream;
	private SocketThread socket_python;  // socket python     
    
    private static final int COMANDO_SALIR = 1;
	private static final int COMANDO_ENVIAR = 2;
	private static final int COMANDO_CANCELAR=3;
	private static final int COMANDO_INICIAR=4; ;
	private static final int COMANDO_TERMINADO=5;
	private static final int COMANDO_CANCELADO=6;
	private static final int COMANDO_INICIADO=7;		
	private static final int COMANDO_FINALIZADO=8;	
	private static final int ACK_EDF = 9;
    private static final int ACK_RUTA= 8;
    private static final int COMANDO_GETRUTA= 7;
    private static final int COMANDO_FALLA= 10;
	private static final int COMANDO_VERIFICAR = 11;
	private static final int COMANDO_VERIFICADO = 12; 
	private static final int COMANDO_ERROR = -1;
	
	
	

	
    public ManageConnectionThread( StreamConnection connection )
	{
	  My_Connection = connection;	
	  socket_python = new SocketThread();
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
		
		if(comando == COMANDO_SALIR || comando == -1)
		{
		System.out.println("salimos del servidor");
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
		/*aqui llamo a la ruta del archivo evaluo la funcion en python getroutedata() y esta me devuelve un string con la ruta del archivo*/	
	    int command_python = 0;
	    String file_path = null;
		socket_python.setinfo(Integer.toString(COMANDO_ENVIAR));
		sleep();
		if(socket_python.getstate() != null)
		{
		command_python = Integer.parseInt(socket_python.getstate());	
		}	
		if(command_python == ACK_RUTA)
		{
		sleep();		
		file_path = socket_python.get_file_path();	
		if(file_path != null)
		{	
		ChargeFile sendata = new ChargeFile(file_path);
	    /////// como capturar la ruta del archivo generado por insuasty; 
	    String nombrearchivo = sendata.GetNameFile();
	    System.out.println("El nombre del archivo es" + nombrearchivo);
	    MyOutputStream.write(COMANDO_ENVIAR);
	    MyOutputStream.flush();
        MyOutputStream.write((nombrearchivo+"").getBytes()); 
	    MyOutputStream.flush();
        /*other*/
     
        BufferedOutputStream bos= new BufferedOutputStream(MyOutputStream);
        FileInputStream  a = sendata.DataFile_f();
        BufferedInputStream bis = new BufferedInputStream(a);
        int n=-1;
        byte[] buffer = new byte[64];
        while((n = bis.read(buffer)) !=-1)
        { 
        bos.write(buffer,0,n);
        bos.flush();
        }
        System.out.println("salimos");
        bos.write(1);
        bos.flush();
        a.close();
        bis.close();
        sleep();        		
		}// fin del if file_path
		} // fin del if 
		
        MyOutputStream.write(COMANDO_TERMINADO);
        MyOutputStream.flush();
        
        
        }
        
        

		if(command == COMANDO_INICIAR)
		{		
		/* ordenes para iniciar la captura de archivos cuando finalmente termine 
		   respondera con un comando terminado*/		   	
		int command_python = 0;
		MyOutputStream.write(COMANDO_INICIADO);		
	    MyOutputStream.flush();
	    socket_python.setinfo(Integer.toString(COMANDO_INICIAR));
	    while(true)
	    {
	    if(socket_python.getstate() != null)
	    {
	    command_python = Integer.parseInt(socket_python.getstate());	
	    }	
	    		    	
	    if(command_python == COMANDO_ERROR)	
	    {
	        MyOutputStream.write(COMANDO_FALLA);		
		    MyOutputStream.flush(); 
		    break;
	    	
	    }
	    
	    if(command_python == COMANDO_TERMINADO)
	    {
	        MyOutputStream.write(COMANDO_FINALIZADO);		
		    MyOutputStream.flush(); 
		    break;
	    }
	    
	    }
	    
	    }
		
		if(command == COMANDO_CANCELAR)
		{
		int command_python = 0;	
		socket_python.setinfo(Integer.toString(COMANDO_CANCELAR));
		sleep();	
		if(socket_python.getstate() != null)
		{
		 command_python = Integer.parseInt(socket_python.getstate());	
		}	
		
		if(command_python == COMANDO_CANCELADO)
		{
			MyOutputStream.write(COMANDO_CANCELADO);
		    MyOutputStream.flush();	
		}
		if(command_python == COMANDO_ERROR)
		{
			MyOutputStream.write(COMANDO_ERROR);
		    MyOutputStream.flush();	
			
		}	
		/* ordenes para cancelar la toma de archivos se supone  este deberia buscar 
		* el archivo hasta el momento generado y borrarlo	
		*/

	    }
		
		
		if(command == COMANDO_VERIFICAR)
		{
			MyOutputStream.write(COMANDO_VERIFICAR);
		    MyOutputStream.flush();	
		    
			int command_python = 12;	
			/*
			socket_python.setinfo(Integer.toString(COMANDO_VERIFICAR));
			sleep();	
			if(socket_python.getstate() != null)
			{
			 command_python = Integer.parseInt(socket_python.getstate());	
			}*/	
			
			if(command_python == COMANDO_VERIFICADO)
			{
				MyOutputStream.write(COMANDO_VERIFICADO);
			    MyOutputStream.flush();	
			}
			if(command_python == COMANDO_ERROR)
			{
				MyOutputStream.write(COMANDO_ERROR);
			    MyOutputStream.flush();	
				
			}					
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

    public void sleep()
    {
    try
    {	
    Thread.sleep(500);
    }
    catch(Exception e)
    {
    e.printStackTrace();	
    }
    }
   

}


