package com.bt.files;

import java.io.File;
import java.io.FileInputStream;

public class ChargeFile 
{

public String routedata; 
public String filename = null;	
public File f; 
	
public ChargeFile(String routedata)
{

f = new File(routedata);
filename = f.getName();	
}	

public FileInputStream DataFile_f()
{ 

    FileInputStream stream_f  = null;	
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
