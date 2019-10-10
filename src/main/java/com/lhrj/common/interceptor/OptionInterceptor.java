/**
 * 
 */
package com.lhrj.common.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * @author Administrator
 *
 */
public class OptionInterceptor implements Interceptor{

	/* (non-Javadoc)
	 * @see com.jfinal.aop.Interceptor#intercept(com.jfinal.aop.Invocation)
	 */
	@Override
	public void intercept(Invocation inv) {
		Controller c = inv.getController();
		if (c.getRequest().getMethod().equals("OPTIONS")) {
			c.renderNull();
			return;
		}
		inv.invoke();
	}

}
