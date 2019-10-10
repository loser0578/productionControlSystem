package cn.lhrj.moudules.sys.role;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.SysRole;
import cn.lhrj.common.model.SysRoleMenu;

public class SysRoleService {
    
	public static final SysRoleService me = new SysRoleService();
	Log log=Log.getLog(SysRoleService.class);
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("role.queryList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}	
	
	public List<Record> queryList(Kv kv) {
		SqlPara sPara=Db.getSqlPara("role.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}	
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
		List<Record> records=Db.find(sPara);
	
        //添加顶级菜单
		Record menu=new Record();
		menu.set("id", 0L);
		menu.set("name","一级菜单");
		menu.set("parent_id",-1L);
		menu.set("open",true);
		records.add(menu);
		return records;
	}
    /**
     * 查询菜单
     */	
	public Record queryObject(long menuid) {
		SqlPara sPara=Db.getSqlPara("role.queryObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("category.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	public void save(String string,Long uid) {
		SysRoleEntity entity=FastJson.getJson().parse(string, SysRoleEntity.class);
		SysRole role=new SysRole();
		role.setRoleName(entity.getRoleName());
		role.setRemark(entity.getRemark());
		role.setCreateUserId(uid);
		role.setCreateTime(new Date());
		role.save();
		if (entity.getMenuIdList().size()>0) {
			for (int i = 0; i < entity.getMenuIdList().size(); i++) {
				SysRoleMenu menu=new SysRoleMenu();
				menu.setRoleId(role.getRoleId());
				menu.setMenuId(entity.getMenuIdList().get(i));
				menu.save();
			}
		}
	}
	public void update(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
			Db.deleteById("shop_category",array.get(i));
		}
	}
}
