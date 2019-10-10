package com.lhrj.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class CorsInterceptor implements Interceptor{

	@Override
	public void intercept(Invocation inv) {
		inv.getController().getResponse().addHeader("Access-Control-Allow-Origin", "*");
/*		inv.getController().getResponse().addHeader("Access-Control-Allow-Credentials", "true");
		
		inv.getController().getResponse().addHeader("Access-Control-Allow-Headers", "Content-Type");*/
		inv.getController().getResponse().addHeader("Access-Control-Allow-Headers", "Origin, x-token,X-Requested-With,Content-Type, Accept");
		inv.getController().getResponse().addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
		inv.getController().getResponse().addHeader("Access-Control-Allow-Credentials", "true"); //该行代码表示允许跨域发送Cookie

	    inv.invoke();
	}
}
