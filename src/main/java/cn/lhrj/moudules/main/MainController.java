package cn.lhrj.moudules.main;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;

import cn.lhrj.common.model.SysMenu;
import cn.lhrj.component.base.BaseProjectController;
import cn.lhrj.moudules.admin.AdminService;

public class MainController extends BaseProjectController{

	@Inject
	MainService service;
	
	/**
	 * 登录
	 * @throws Exception 
	 */
	public void index() throws Exception {
		//在这个位置判断登陆的逻辑
		render("main.html");
	}
	public void getMenuList() {
		List<SysMenu> menuList =new AdminService().getUserMenuList(getLoginAccount().getId());
		renderJson(Ret.ok("menuList", menuList));
	}
	public void getdata() {
		JSONArray totalarray=new JSONArray();	
		JSONArray array4=service.getVisitDate();	
		JSONArray array3=service.getUserDate();
		JSONArray array2=service.getSellDate();
		JSONArray array1=service.getGoodsDate();
		totalarray.add(0, array1);
		totalarray.add(1, array2);
		totalarray.add(2, array3);
		totalarray.add(3, array4);
		renderJson(totalarray);	
	}
	public void getMonthsellsDate() {
		JSONArray array=service.getMonthSellsDate();
		renderJson(array);
	}
}
