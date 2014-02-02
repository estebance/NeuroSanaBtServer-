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
import java.io.PrintWriter;
import java.math.BigInteger;

import javax.microedition.io.StreamConnection;

import com.bt.files.ChargeFile;
import com.bt.server.ServerBt;


//hilo para el manejo de la conexion bluetooth 

public class ManageConnectionThread extends Thread 
{
	private StreamConnection My_Connection;		
    private InputStream MyInputStream;
    private OutputStream MyOutputStream;
	private SocketThread socket_python;  // socket python     
    
	
	/* Comandos de interfaz con raspberry y servidor */
    private static final int COMANDO_SALIR =      1;
	private static final int COMANDO_ENVIAR =     2;
	private static final int COMANDO_CANCELAR=    3;
	private static final int COMANDO_INICIAR=     4;  
	private static final int COMANDO_TERMINADO=   5;  // responde
	private static final int COMANDO_CANCELADO=   6;  // respuesta
	private static final int ACK_EDF =            9;  
	private static final int ACK_RUTA=            8;
	private static final int COMANDO_GETRUTA=     7;  
	private static final int COMANDO_VERIFICAR=  11;
	private int COMANDO_ERROR =                  13;   // respuesta 
	
	
	/* comandos de manejo del servidor */

	private static final int COMANDO_INICIADO=    7; //respuesta 		
	
    

	
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
		int comando = 0;
		System.out.println("esperando una solicitud por parte del cliente");	
	    MyInputStream = My_Connection.openInputStream();
		MyOutputStream = My_Connection.openOutputStream();			
		
		
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
	closeall();
	resume_connection();
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
		/*aqui llama a la ruta del archivo evalua la funcion en python getroutedata() y esta  devuelve un string con la ruta del archivo*/	
	    int command_python = 0;
	    String file_path = null;
		socket_python.setinfo(Integer.toString(COMANDO_ENVIAR)); // envio la orden a python 
		sleep();
		
		if(socket_python.getstate() != null)  // capturo lo que regresa python 
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
	    /////// como capturar la ruta del archivo generado ////// 
	    String nombrearchivo = sendata.GetNameFile();
	    System.out.println("El nombre del archivo es" + nombrearchivo); // enviamos la ruta del archivo 
	    
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
        //bos.flush(); // este reemplaza al del anterior comentario 
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
		int command_python = 0;
		/* ordenes para iniciar la captura de archivos cuando finalmente termine  respondera con un comando terminado*/		   	
		
		MyOutputStream.write(COMANDO_INICIADO);	 // le envio a android comando iniciado 	
	    MyOutputStream.flush();
	    socket_python.setinfo(Integer.toString(COMANDO_INICIAR)); // le envio a python comando iniciar 
	    while(true)
	    {

		if(socket_python.getstate() != null)
		{
		command_python = Integer.parseInt(socket_python.getstate());	
		}		    		    
	    int a = 0 ; 
	    try
	    {
	    if(MyInputStream.available() != 0)
	    {	
	    a = MyInputStream.read();	
	    }

	    }
	    catch(Exception e)
	    {
	    closeall();
	    resume_connection();	
	    e.printStackTrace();	
	    }
	    
	    if(a == COMANDO_CANCELAR)   // recibo de android comando cancelar 
		{
			System.out.println("entro en cancelar");
	    	socket_python.setinfo(Integer.toString(COMANDO_CANCELAR));
			MyOutputStream.write(COMANDO_CANCELADO);		
			MyOutputStream.flush(); 
			break;
		} 		    	
	    	
	    		    	
	    if(command_python == COMANDO_ERROR)		    	
	    {
	    	command_python = 0;
	        MyOutputStream.write(COMANDO_ERROR);		
		    MyOutputStream.flush(); 
		    break;
	    	
	    }
	    
	    if(command_python == COMANDO_TERMINADO)
	    {
	    	System.out.println("vamos a salir");
	        MyOutputStream.write(COMANDO_TERMINADO); // antes era finalizado 		
		    MyOutputStream.flush(); 
		    break;
	    }	    
	    
	    
	    
	    }
	    }
					
		if(command == COMANDO_VERIFICAR)
		{
			PrintWriter temp_out = new PrintWriter(MyOutputStream,true);
			int command_python = 0;	
			MyOutputStream.write(COMANDO_VERIFICAR);  
		    MyOutputStream.flush();	
			socket_python.setinfo(Integer.toString(COMANDO_VERIFICAR));
			while(true){
			sleep();
			sleep();
			if(socket_python.getstate() != null)
			{	
			 command_python = Integer.parseInt(socket_python.getstate());	
			 System.out.println("regresa el numero_:"+command_python);
 			} 	

			if(command_python == COMANDO_ERROR)
			{
					
				temp_out.println(COMANDO_ERROR);
				temp_out.flush();
				//MyOutputStream.write(COMANDO_FALLA);
			    //MyOutputStream.flush();	
			    break;
			      
			}
			else
			{
				if( command_python != 0)
				{	
				System.out.println("regresa el numero_:"+command_python);
				temp_out.println(command_python);
				temp_out.flush();
				//MyOutputStream.write(255);
			    //MyOutputStream.flush();		
			    break;
				}
			}
			
			}
			
		   }// metodo 	
		
		
	   }
		
	   catch(Exception e)
	   {
		
	    System.out.println("error en process command" +e);
		closeall();
		resume_connection();
	    e.printStackTrace();
	   }
		

		
	}
	
	
	public void closeall()
	{
		
	try
	{
		MyOutputStream.close();
		MyInputStream.close();
		My_Connection.close();
		socket_python.close_connection();
		
	} 
	
	catch (IOException e) 
	
	{
		System.out.println("error al momento de intentar cerrar conexion");
		e.printStackTrace();
	}
	
		
	}
	
	
	public void resume_connection()
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


