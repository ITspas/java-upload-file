package cn.j2ee.simpleupload;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

public final class SimpleUploadFactory {
	private static ServletContext servletContext;

	public static SimpleUpload newMiniUpload(PageContext pageContext){
		return SimpleUploadFactory.newMiniUpload(pageContext.getServletContext(),pageContext.getRequest());
	}
	
	public static SimpleUpload newMiniUpload(ServletContext servletContext,ServletRequest request){
		SimpleUploadFactory.servletContext = servletContext;
		return new SimpleUpload(request);
	}
	
	static String getRootPath(){
		return servletContext!=null?servletContext.getRealPath("/"):null;
	}
}
