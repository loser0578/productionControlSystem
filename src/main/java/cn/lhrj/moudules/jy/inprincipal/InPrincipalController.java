package cn.lhrj.moudules.jy.inprincipal;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyPrincipal;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class InPrincipalController extends BaseProjectController{
	
	InPrincipalService is = InPrincipalService.me;
	
	public void index() {
		render("/page/jy/inprincipal.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyPrincipal> records=is.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void delect() {
		int principalId = getParaToInt("id");
		JyPrincipal principal = new JyPrincipal().findFirst("select * from jy_principal where principal_id = ?",principalId);
		principal.setState(0);
		boolean result = principal.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void update() {
		int plantId = getLoginAccount().getPlantId();
		is.update(HttpKit.readData(getRequest()),plantId);
		renderJson(Ret.ok());
	}
	
	public void save() {
		int plantId = getLoginAccount().getPlantId();
		is.save(HttpKit.readData(getRequest()),plantId);
		renderJson(Ret.ok());
	}
	
	public void allList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_principal where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyPrincipal> principalList = new JyPrincipal().find(sql.toString());
		renderJson(Ret.ok("principalList", principalList));
	}
}
