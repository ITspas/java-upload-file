package cn.j2ee.simpleupload;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class ProxyRequest implements InvocationHandler{
	private HashMap<String, String> parameters;
	
	private HttpServletRequest realRequest;
	
	ProxyRequest() {
		this.parameters = new HashMap<String, String>();
	}
	
	
	void setParameter(String name,String value){
		value= this.parameters.containsKey(name)?this.parameters.get(name)+"\""+value:value;
		this.parameters.put(name, value);
	}
	
	HttpServletRequest buildRequest(HttpServletRequest request){
		this.realRequest = request;
		return (HttpServletRequest) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{HttpServletRequest.class}, this);
	}


	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if(method.getName().startsWith("getParameter"))
			return this.getResult(method.getName(),args[0]);
		return method.invoke(this.realRequest, args);
	}
	
	public Object getResult(String methodName,Object key){
		if(!this.parameters.containsKey(key)){
			return null;	
		}else if("getParameter".equals(methodName)){
			return this.getParam(key)[0];
		}else if("getParameterMap".equals(methodName)) {
			return this.parameters;
		}else if("getParameterNames".equals(methodName)) {
			return Collections.enumeration(this.parameters.keySet());
		}else if("getParameterValues".equals(methodName)) {
			return this.getParam(key);
		}return false;
	}
	
	private String[] getParam(Object key) {
		return this.parameters.get(key).split("\"");
	}
}
