package com.zyitong.AppStore.tools;
import java.io.File;
public class FileOpt {
	
	public FileOpt()
	{
	}
    public  void CreateFilePath(String path)
    {
    	try{
    		File file = new File(path);
    		if(!file.exists())
    			file.mkdirs();
    	}catch(Exception e){};
    }
    public  boolean exists( String fileName){
        try{
      	 File file = new File(fileName);
      	 return file.exists();
 
         }catch(Exception e){
        	 e.printStackTrace();
         }
         
      	 return false;
      }  
	
	
	public boolean deleteFile(String path)
	{
		File file = new File(path);
		return file.delete();
	}


}
