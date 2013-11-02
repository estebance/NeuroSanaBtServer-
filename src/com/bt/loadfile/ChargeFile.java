package com.bt.loadfile;

import java.io.File;
import java.io.FileInputStream;

public class ChargeFile 
{

String routedata; 
// inicializamos por si se presentan problemas
String filename = "nada";	
	
public ChargeFile()
{
   	
}	
	
public byte[] DataFile()
{ 
	
	    try
	    {
	    // obtener file como bytes
	    FileInputStream stream = new FileInputStream(routedata);
	    File f = new File(routedata);
	    int size = 1024;
	    byte[] file = new byte[size];
	    stream.read(file);
	    // algo se podra hacer con esto ? podria ser un get
	    filename = f.getName();
	    System.out.println(stream +"\n"+"Nombre Archivo"+filename);
	    //////
	    return file;
	    }
	    catch(Exception e)
	    {
	    e.printStackTrace();	
	    return null;
	    }
}



public FileInputStream DataFile_f()
{ 
	FileInputStream stream_f  = null;
	
    File f = new File(routedata);
    try
    {
    stream_f = new FileInputStream(f);	
    }
    catch(Exception e)
    {
    e.printStackTrace();	
    }
    
   return stream_f; 
}


public void SetRouteData( String routedata)
{
this.routedata = routedata;	
}

public String GetNameFile()
{
return filename;	
}
	
}
