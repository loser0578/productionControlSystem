package cn.lhrj.moudules.sys.dept;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.utils.Constants;
import cn.lhrj.component.base.BaseProjectController;

public class SysDeptController extends BaseProjectController{
	
	SysDeptService srv=SysDeptService.me;
	
	public void index() {
		render("/page/sys/dept.html");
	}
	
    /**
     * 所有用户列表
     */	
	public void list() {
		Kv para=new Kv();
       if (getLoginAccount().getId() != Constants.SUPER_ADMIN) {
    	   para.set("deptFilter", srv.getSubDeptIdList(getLoginAccount().getDeptId()));
        }
		List<Record> records=srv.queryAll(para);
		renderJson(Ret.ok("list", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("dept", srv.queryObject(getParaToInt())));
	}

	
    /**
     * 选择菜单(添加、修改菜单)
     */	
	public void select() {
		Kv para=new Kv();
        //如果不是超级管理员，则只能查询本部门及子部门数据
        if (getLoginAccount().getId() != Constants.SUPER_ADMIN) {
        	para.set("deptFilter", srv.getSubDeptIdList(getLoginAccount().getDeptId()));
        }
        List<Record> list=srv.queryAll(para);
        //添加一级部门
        if (getLoginAccount().getId() == Constants.SUPER_ADMIN) {
        	Record root = new Record();
            root.set("dept_id", 0L);
            root.set("name","一级部门");
            root.set("parent_id", -1L);
            root.set("open", true);  
            list.add(root);
        }
		renderJson(Ret.ok("deptList", list));
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
		srv.delectByIds(getPara("dept_id"));
		renderJson(Ret.ok());
	}
}
