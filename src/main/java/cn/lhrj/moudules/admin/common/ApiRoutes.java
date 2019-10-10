package cn.lhrj.moudules.admin.common;

import com.jfinal.config.Routes;

import cn.lhrj.moudules.api.controller.ApiIndexController;
import cn.lhrj.moudules.api.interceptor.ApiInterceptor;

public class ApiRoutes extends Routes {

	@Override
	public void config() {
		addInterceptor(new ApiInterceptor());
		
		// TODO Auto-generated method stub
		add("/api/index", ApiIndexController.class);
	}

}
