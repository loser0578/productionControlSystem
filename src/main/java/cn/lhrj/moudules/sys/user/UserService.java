package cn.lhrj.moudules.sys.user;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyPlant;
import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.model.SysUserRole;

public class UserService {
    
	public static final UserService me = new UserService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("sysuser.queryList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
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
		SqlPara sPara=Db.getSqlPara("sysuser.queryObject", menuid);
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
	
	public void save(String shopSpecification) {
		System.out.println(shopSpecification);
		JSONObject object = JSONObject.parseObject(shopSpecification);
		SysUser user = new SysUser();
		user.setStatus(object.getIntValue("status"));
		user.setUsername(object.getString("username"));
		user.setPlantId(object.getIntValue("plantId"));
		String password = "888888";
		password = HashKit.md5(password.trim());
		user.setPassword(password);
		JyPlant plant = new JyPlant().findFirst("select * from jy_plant where id = ?",object.getIntValue("plantId"));
		user.setPlantName(plant.getName());
		user.save();
		SysUserRole role = new SysUserRole();
		role.setUserId(user.getId());
		Long  roleList = object.getLongValue("roleIdList");
		role.setRoleId(roleList);
		role.save();
	}
	public void update(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.update();
	}
	public void delectByIds(String string) {
		System.out.println("String"+string);
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			int id = array.getIntValue(i);
			System.out.println("id"+id);
			SysUser user = new SysUser().findFirst("select * from sys_user where id = ?",id);
			user.setStatus(0);
			user.update();
		}
	}
	public Ret resetPassword(String password,String newpassword,Long id) {
		password = HashKit.md5(password.trim());
		System.out.println("password"+password);
		SysUser loginAccount = new SysUser().findById(id);
		System.out.println("loginAccount"+loginAccount);
		if (loginAccount == null) {
			return Ret.fail("msg", "用户名不正确").set("code", -1);
		}
		// 未通过密码验证
		System.out.println(loginAccount.getPassword().equals(password));
		if (loginAccount.getPassword().equals(password) == false) {
			return Ret.fail("msg", "密码不正确").set("code", -1);
		}
		newpassword = HashKit.md5(newpassword.trim());
		loginAccount.setPassword(newpassword);
		loginAccount.update();
		return Ret.ok().set("code", 1);
	}
}
