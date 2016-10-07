package cn.j2ee.simpleupload;

import java.io.FileOutputStream;
import java.io.IOException;


public class SimpleFile{
	private byte[] data;
	private String name;
	
	public SimpleFile(byte[] data,String name) {	
		this.data = data;
		this.name = name;
	}
	
	public int getFileSize(){
		return this.data.length;
	}
	
	public String getFileName(){
		return this.name.substring(0,this.name.lastIndexOf("."));
	}
	
	public String getFileExt(){
		return this.name.substring(this.name.lastIndexOf(".")+1);
	}
	
	public String save(String path) throws IOException{
		if(!path.matches("^[A-Za-z]:.*$")){
			path =SimpleUploadFactory.getRootPath()+ (path.startsWith("\\")?path.substring(1):path);
		}
		FileOutputStream fos = new FileOutputStream(path);
		fos.write(this.data);
		fos.close();
		return path;
	}
}
