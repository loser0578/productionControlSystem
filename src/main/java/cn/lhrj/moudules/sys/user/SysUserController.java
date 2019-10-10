package cn.lhrj.moudules.sys.user;

import com.jfinal.core.paragetter.Para;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.SysUser;
import cn.lhrj.component.base.BaseProjectController;

public class SysUserController extends BaseProjectController{
	
	UserService srv=UserService.me;
	
	public void index() {
		render("/page/sys/user.html");
	}
	
    /**
     * 所有用户列表
     */	
	public void list() {
		Kv para=new Kv();
		SysUser model=getModel(SysUser.class,"",true);
		System.out.println("model"+model);
		if (model._getAttrValues().length != 0) {
			para.set("username", model.get("username"));
		}

		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("user", getLoginAccount()));
	}
	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void getCategorySelect() {
		renderJson(Ret.ok("list", srv.select()));
	}
    /**
     * 保存
     */	
	public void save() {
		srv.save(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 修改
     */
	public void update() {	
		srv.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 删除
     */	
	public void delete() {	
		srv.delectByIds(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /* 
     *	 修改密码
     */	
	public void resetPassword(@Para("password") String password,@Para("newPassword") String newpassword) {
		renderJson(srv.resetPassword(password, newpassword, getLoginAccount().getId()));
	}

}
