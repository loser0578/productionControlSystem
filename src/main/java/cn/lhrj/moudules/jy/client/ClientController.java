package cn.lhrj.moudules.jy.client;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyClient;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class ClientController extends BaseProjectController{
	
	ClientService cs = ClientService.me;
	
	public void index() {
		render("/page/jy/client.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyClient> records = cs.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void delect() {
		int plantId = getAccountId();
		int clientId = getParaToInt("id");
		SQLUtils sql = new SQLUtils("select * from jy_client where 1=1 ");
		if (-1 == plantId) {
			sql.whereEquals("client_id", clientId);
		}else {
			sql.whereEquals("client_id", clientId);
			sql.whereEquals("plant_id", plantId);
		}
		JyClient client = new JyClient().findFirst(sql.toString());
		client.setState(0);
		boolean result = client.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void update() {
		cs.update(HttpKit.readData(getRequest()),getLoginAccount().getPlantId());
		renderJson(Ret.ok());
	}
	
	public void save() {
		cs.save(HttpKit.readData(getRequest()),getLoginAccount().getPlantId());
		renderJson(Ret.ok());
	}
	
	public void allList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_client where state = 1");
		if (-1 == plantId) {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyClient> clientList = new JyClient().find(sql.toString());
		renderJson(Ret.ok("clientList", clientList));
	}
}
