package cn.lhrj.moudules.admin;

import cn.lhrj.component.base.BaseProjectController;

public class LogotController extends BaseProjectController{

	
	public void logout() {
		render("login.html");
	}
}
