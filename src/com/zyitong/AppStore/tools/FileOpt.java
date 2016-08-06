package com.zyitong.AppStore.tools;
import java.io.File;

/***
 * 文件工具类
 * @author 周双
 *
 */
public class FileOpt {
	/***
	 * 创建文件
	 * @param path
	 */
    public void CreateFilePath(String path){
    	try{
    		File file = new File(path);
    		if(!file.exists())
    			file.mkdirs();
    	}catch(Exception e){};
    }
    
    /***
     * 判断文件是否存在
     * @param filePath
     * @return
     */
    public  boolean exists( String filePath){
        try{
      	 File file = new File(filePath);
      	 return file.exists();
 
         }catch(Exception e){
        	 e.printStackTrace();
         }
         
      	 return false;
      }  
	
	
    /***
     * 删除文件
     * @param path
     * @return
     */
	public boolean deleteFile(String path)
	{
		File file = new File(path);
		return file.delete();
	}


}
