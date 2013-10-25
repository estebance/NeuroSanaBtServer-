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
   // private  OutputStream MyOutStream;
   // private  InputStream MyInputStream;
    private static final int COMANDO_SALIR = 1;
	private static final int COMANDO_ENVIAR = 2;
	private static final int COMANDO_CANCELAR=3;
	private static final int COMANDO_INICIAR=4; ;
	private static final int COMANDO_TERMINADO=5;
	private static final int COMANDO_CANCELADO=5;
	
	
	
	
   public ManageConnectionThread( StreamConnection connection )
	{
	  My_Connection = connection;	 
    //  InputStream tmpInput = null;
    //  OutputStream tmpOutput = null;
    // try 
    //  {
    //      tmpInput = My_Connection.openInputStream();
    //      tmpOutput = My_Connection.openOutputStream();
    //  } 
    //  catch (IOException e) {}

     // MyInputStream = tmpInput;
     // MyOutStream = tmpOutput;
  
  }
	  
	  
	  
	
	
	
	@Override
	public void run() 
	{
	
	try 
	{
		System.out.println("esperando una solicitud por parte del cliente");
		//DataInputStream entrada = new DataInputStre,am(MyInputStream);	
		InputStream MyInputStream = My_Connection.openInputStream();
		while(true)
		{			
		System.out.println(MyInputStream.read());			
		//int comando = Integer.parseInt(MyInputStream.read());
		int comando = MyInputStream.read();
	    
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
		
		MyInputStream.close();
		
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
		OutputStream MyOutputStream = My_Connection.openOutputStream();	
		
		if(command == COMANDO_ENVIAR)
		{	
	    ChargeFile sendata = new ChargeFile();
	    /////// como capturar la ruta del archivo generado por insuasty; 
	    sendata.SetRouteData("una ruta");
	    byte[] informacion = sendata.DataFile();
	    String nombrearchivo = sendata.GetNameFile();

        MyOutputStream.write(informacion);
        MyOutputStream.close();
	
		}

		if(command == COMANDO_INICIAR)
		{		
		/* ordenes para iniciar la captura de archivos cuando finalmente termine 
		   respondera con un comando terminado*/		      
		      MyOutputStream.write(COMANDO_TERMINADO);
		      MyOutputStream.close(); 
		}
		
		if(command == COMANDO_CANCELAR)
		{
			/* ordenes para cancelar la toma de archivos se supone  este deberia buscar 
			 * el archivo hasta el momento generado y borrarlo	
			 */
		      MyOutputStream.write(COMANDO_CANCELADO);
		      MyOutputStream.close(); 
			
	     }
		
		 MyOutputStream.close(); 
		
		 }
		 catch(Exception e)
		 {
		 System.out.println("error en process command" +e); 	
		 }
		

		
	}
	
	
	private void closeall()
	{
		
	try
	{
		My_Connection.close();
	} 
	
	catch (IOException e) 
	
	{
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
		
	}


   

}


