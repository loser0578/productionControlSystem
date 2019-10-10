package cn.lhrj.moudules.admin.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfinal.handler.Handler;

public class LoginHandler extends Handler{
	
	private String viewPostfix;
	
	public LoginHandler() {
		viewPostfix = ".html";
	}
	
	@Override
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
	
		
		if ("/".equals(target)) {
			next.handle(target, request, response, isHandled);
			return;
		}
	    
		int index = target.lastIndexOf(viewPostfix);
		if (index != -1) {
			target = target.substring(0, index);
		}

	
		next.handle(target, request, response, isHandled);
		
	}

}
