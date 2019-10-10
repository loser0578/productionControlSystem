package cn.lhrj.moudules.jy.staff;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.weixin.sdk.msg.out.News;

import cn.lhrj.common.model.JyPlant;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.model.SysUserRole;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class StaffController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/staff.html");
	}
	
	public void list() {
		int plantId = getLoginAccount().getPlantId();
		Page<SysUser> userPage = new SysUser().paginate(getParaToInt("page"),getParaToInt("limit"),"select a.* ","from sys_user a left join sys_user_role b on a.id = b.user_id where a.plant_id = ? and b.role_id = 7 and a.status = 1",plantId);
		renderJson(Ret.ok("page",userPage));
	}
	
	public void delect() {
		int id = getParaToInt("id");
		SysUser user = new SysUser().findFirst("select * from sys_user where id = ?",id);
		user.setStatus(0);
		user.update();
		renderJson(Ret.ok());
	}
	
	public void save() {
		System.out.println("xxxxxxxxxx");
		String string  = HttpKit.readData(getRequest());
		JSONObject object = JSONObject.parseObject(string);
		String username = object.getString("username");
		int plantId = getLoginAccount().getPlantId();
		SysUser user = new SysUser();
		user.setUsername(username);
		String password = "888888";
		password = HashKit.md5(password.trim());
		user.setPassword(password);
		JyPlant plant = new JyPlant().findFirst("select * from jy_plant where id = ?",plantId);
		user.setPlantName(plant.getName());
		user.setStatus(1);
		user.setCreateTime(new Date());
		user.setPlantId(plantId);
		user.save();
		SysUserRole role = new SysUserRole();
		role.setRoleId(7L);
		role.setUserId(user.getId());
		role.save();
		renderJson(Ret.ok());
	}
}
