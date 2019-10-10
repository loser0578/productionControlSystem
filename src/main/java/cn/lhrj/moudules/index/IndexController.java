package cn.lhrj.moudules.index;

import java.util.List;

import com.jfinal.kit.Ret;

import cn.lhrj.common.model.SysMenu;
import cn.lhrj.component.base.BaseProjectController;
import cn.lhrj.moudules.admin.AdminService;

public class IndexController extends BaseProjectController{

	
	/**
	 * 登录
	 * @throws Exception 
	 */
	
	public void index() throws Exception {

		//在这个位置判断登陆的逻辑
		render("index.html");

	}
	public void getMenuList() {
		System.out.println(getLoginAccount().getId());
		List<SysMenu> menuList =new AdminService().getUserMenuList(getLoginAccount().getId());
		renderJson(Ret.ok("menuList", menuList));
	}
	public void getMessageList() {
		renderJson("messageList",new MessageService().getMessage());
	}

}
