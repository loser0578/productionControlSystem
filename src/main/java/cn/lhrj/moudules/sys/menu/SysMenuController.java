package cn.lhrj.moudules.sys.menu;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.component.base.BaseProjectController;

public class SysMenuController extends BaseProjectController{
	
	SysMenuService srv=SysMenuService.me;
	
	public void index() {
		render("/page/sys/menu.html");
	}
	public void queryAll() {
		Kv para = Kv.by("menuName", null).set("parentName", null).set("sidx", null).set("offset",null);
		List<Record> records=srv.queryAll(para);
		renderJson(Ret.ok("list", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("menu", srv.queryObject(getParaToLong())));
	}
	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void select() {
		renderJson(Ret.ok("menuList", srv.select()));
	}
    /**
     * 新增(添加、修改菜单)
     */	
	public void save() {
		srv.save(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
    /**
     * 更新(添加、修改菜单)
     */	
	public void update() {
		srv.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
	public void delete() {
		String string=HttpKit.readData(getRequest());
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			if (Integer.parseInt(array.get(i).toString())<30) {
				renderJson(Ret.fail("msg", "系统菜单，不能删除"));
				return;
			}
		}
		srv.delectByIds(string);
		renderJson(Ret.ok());
	}
	   /**
     * 角色授权菜单
     */

    public void perms() {
        //查询列表数据
        List<Record> menuList = null;
        Long roleId=getPara() == null ? 0L  : getParaToLong();
        //只有超级管理员，才能查看所有管理员列表
        if (roleId.equals(0L)) {
            menuList = srv.queryAll(new Kv());
        } else {
        	System.out.println(getParaToLong());
            menuList = srv.queryUserList(getParaToLong());
        }

        renderJson(Ret.ok("menuList", menuList));
    }	
	
	
	
}
