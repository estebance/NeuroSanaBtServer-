package com.bt.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketThread  
{
	public CaptureSignalThread mythread; 
	public String data_from_python = null ;
	public String file_path = null ;
    private int PORT = 8083;
    
    public ServerSocket server = null;
    public Socket cliente = null;	
	

public SocketThread()
{
	 ServerSocket temp_server = null;
	 Socket temp_cliente = null; 
	 try
	 {
	 temp_server = new ServerSocket(PORT);  
	 System.out.println("Esperando a Python en :8080");
	 while(true)
	 {
	 temp_cliente = temp_server.accept();   
	 if(temp_cliente != null)
	 {
	 break; 	
	 } 	    
	 }
	 }
	 catch(Exception e)
	 {
	 e.printStackTrace();	 
	 }
	 server = temp_server;
	 cliente = temp_cliente;
	 mythread = new CaptureSignalThread();
	 mythread.start();	
}

public void setinfo (String data)
{ 
	   CaptureSignalThread send;
      /* sincronizando para poder ser empleado */
      synchronized (this) 
      {

       send = mythread;
       send.write(data);
       
      }
}


public String getstate()
{	
 return data_from_python;
}

public String get_file_path()
{
return file_path ; 	
}


// inicio clase secundaria 

public class CaptureSignalThread extends Thread 
{
    //Temporales
    private String COMANDO_ENVIAR    = "2";
    private String COMANDO_CANCELAR  = "3";
    private String COMANDO_INICIAR   = "4"; ;
    private String COMANDO_TERMINADO = "5";
    private String COMANDO_CANCELADO = "6";
    private String COMANDO_ERROR     = "-1"; 
    //
    private String ACK_EDF           = "9";
    private String ACK_RUTA          = "8";
    private String COMANDO_GETRUTA   = "7";
    
	private String COMANDO_VERIFICAR = "11";
	private String COMANDO_VERIFICADO = "12"; 
    // 
    private String addrfile  =  null;
    private String fromPython = null;
    private String toPython = null;
    

    public BufferedReader in = null;
    public PrintWriter out = null;
    

public CaptureSignalThread()
{
    BufferedReader temp_in = null;
    PrintWriter temp_out = null;
	
	try
	{

    System.out.println("CONECTADO");

	temp_in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
	temp_out = new PrintWriter(cliente.getOutputStream(),true);
    }
     
    catch (Exception e) 
    {
	e.printStackTrace();
	}

	in = temp_in;
	out = temp_out; 
}	

public void run() 
{
	
while(true)
{
    System.out.println("Escucho a Python");  
    try 
    {
	fromPython = in.readLine();
	} 
    catch (Exception e) 
    {
   	e.printStackTrace();
	}
    
    System.out.println("mensaje de python: " + fromPython);  	
	
    if (fromPython.equals(COMANDO_TERMINADO))
    {
    	System.out.println("Listo ya termino");
    	set_data_python(COMANDO_TERMINADO);
    }
    
    else if(fromPython.equals(COMANDO_ERROR))
    {                    
        System.out.println("HAY UN ERROR");
        set_data_python(COMANDO_ERROR);
    }


    else if(fromPython.equals(COMANDO_CANCELADO))
    {                    
    	 System.out.println("COMANDO CANCELAR");
    	 set_data_python(COMANDO_CANCELADO);
    }

    else if(fromPython.equals(ACK_RUTA))
    {                    
        set_data_python(ACK_RUTA);
        // escribo que necesito la ruta del archivo
        write(COMANDO_GETRUTA);
        try 
        {
		addrfile = in.readLine();
		set_file_path(addrfile);
		} 
        catch (IOException e) 
        {
	     e.printStackTrace();
		}              
        System.out.println(addrfile);
    }      
    
    else if(fromPython.equals(ACK_EDF))
    {                    
        write(ACK_EDF);
    }  
    
    else if(fromPython.equals(COMANDO_VERIFICADO))
    {
    	set_data_python(COMANDO_VERIFICADO);
    }	
    
}	
	
	

} // final run 

 public void write(String data)
 {	
  out.println(data);	
 }

 public void set_data_python(String command)
 {
 data_from_python = command	;
 }

 public void set_file_path(String path)
 {
 file_path= path	;
 }


	
} // fin segunda clase



} // fin clase principal 
