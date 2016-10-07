package cn.j2ee.simpleupload;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class SimpleUpload {
	private HttpServletRequest request;

	private String encoding="ISO-8859-1";
	private boolean isFormDate;
	private byte[] boundary;
	
	private byte[] binRead;
	private int length;
	
	private ProxyRequest proxyRequest;
	private List<SimpleFile> fileList;
	private String fileTypeFilter="";

	SimpleUpload(ServletRequest request) {
		this.request = (HttpServletRequest) request;
		this.proxyRequest = new ProxyRequest();
		this.fileList = new ArrayList<SimpleFile>();
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void upload() throws Exception {
		try {
			if (!this.checkForm())return;
			this.binRead = new byte[this.request.getContentLength()];
			this.length = this.request.getInputStream().readLine(this.binRead, 0, this.binRead.length);
			while(this.handleParam()){
				this.resolveParam();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("upload error");
		}
	}
	
	private void resolveParam(){
		String str = null;
		try {
			str = new String(this.binRead,0,this.length,this.encoding);
			this.buildParam(str);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private void buildParam(String str){
		String name=null,value=null;
		String paramStr = str.substring(0,str.indexOf("\n"));
		name = this.buildParamName(paramStr);
		value = this.buildFileParamValue(paramStr);
		value = value==null?this.buildValue(str):this.buildFile(this.binRead,str,value);
		this.proxyRequest.setParameter(name, value);
	}
	
	private String buildFile(byte[] date,String str,String fileName) {
		if(fileName.matches(""))return fileName;
		String[] strs = str.split("\n");
		String type = fileName.substring(fileName.lastIndexOf(".")+1);
		if(this.fileTypeFilter.indexOf(type)>0)return fileName;
		int index = strs[0].length()+strs[1].length()+strs[2].length()+3;
		try {
			index = str.substring(0,index).getBytes(this.encoding).length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return fileName;
		}
		int endIndex =this.length-this.boundary.length -( date[this.length-3]==this.boundary[0]?5:4);
		SimpleFile mu = new SimpleFile(Arrays.copyOfRange(date, index, endIndex),fileName);
		this.fileList.add(mu);
		return fileName;
	}

	private String buildValue(String str) {
		str = str.split("\n")[2];
		return str.substring(0,str.length()-1);
	}

	private String buildParamName(String str){
		int index = str.indexOf("=")+2;
		return str.substring(index,str.indexOf("\"",index));
	}
	private String buildFileParamValue(String str){
		int index = str.indexOf("filename");
		return index>0?str.substring(index+10,str.indexOf("\"",index+10)):null;
	}
	
	private boolean handleParam() throws IOException{
		if((length = this.request.getInputStream().readLine(this.binRead, 0, this.binRead.length))<0)return false;
		while(!new String(this.binRead,this.length-38,36).matches("-+[a-zA-Z0-9]+-*")){
			length+=this.request.getInputStream().readLine(this.binRead, length, this.binRead.length-length);
		}
		return true;
	}

	private boolean checkForm() throws Exception {
		String str = this.request.getContentType();
		this.boundary =("--"+str.substring(str.lastIndexOf("=") + 1)).getBytes(this.encoding);
		return (this.isFormDate = str.startsWith("multipart/form-data") ? true : false);
	}
	
	public void setNotUploadFileType(String exts){
		this.fileTypeFilter = exts;
	}
	
	public HttpServletRequest getRequest(){
		return this.isFormDate?this.proxyRequest.buildRequest(this.request):this.request;
	}
	
	public List<SimpleFile> getFiles(){
		return this.fileList;
	}
	
	public void saveFiles(String path)throws IOException{
		if(!path.matches("^\\\\?(.+\\\\)*$"))throw new IOException("path exception");
		path = path.startsWith("\\")?path.substring(1):path;
		for(SimpleFile f : this.fileList){
			f.save(path+f.getFileName()+"."+f.getFileExt());
		}
	}
	
	public String getRootPath(){
		return SimpleUploadFactory.getRootPath();
	}
	
}