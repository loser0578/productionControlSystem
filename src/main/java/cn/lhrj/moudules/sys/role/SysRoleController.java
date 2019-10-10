package cn.lhrj.moudules.sys.role;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.Constants;
import cn.lhrj.component.base.BaseProjectController;

public class SysRoleController extends BaseProjectController{
	
	SysRoleService srv=SysRoleService.me;
	
	public void index() {
		render("/page/sys/role.html");
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
	
	public void select() {
		Kv para=new Kv();
		 //如果不是超级管理员，则只查询自己所拥有的角色列表
		if (getLoginAccount().getId() != Constants.SUPER_ADMIN) {
			para.set("createUserId", getLoginAccount().getId());
		}
		renderJson(Ret.ok("list", srv.queryList(para)));
	}
	
	
	
	/**
     * 角色信息
     */
	public void info() {
		renderJson(Ret.ok("role", srv.queryObject(getParaToLong())));
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
		srv.save(HttpKit.readData(getRequest()),getLoginAccount().getId());
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
}
