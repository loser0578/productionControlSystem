package cn.lhrj.moudules.jy.repertory;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyClient;
import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.model.JyRepertory;
import cn.lhrj.common.model.JyRepertoryLog;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class RepertoryController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/repertory.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils(" from jy_repertory where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyRepertory> pageRepertory = new JyRepertory().paginate(getParaToInt("page"),getParaToInt("limit"),"select *",sql.toString());
		renderJson(Ret.ok("page", pageRepertory));
	}
	
	public void getClientList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_client where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyRepertory> repertoryList = new JyRepertory().find(sql.toString());
		renderJson(Ret.ok("list", repertoryList));
	}
	
	public void save() {
		String string = HttpKit.readData(getRequest());
		System.out.println(string);
		JSONObject object = JSONObject.parseObject(string);
		System.out.println(object);
		int id = object.getIntValue("id");
		int clientId = object.getIntValue("client_id");
		int number = object.getIntValue("number");
		JyRepertory repertory = new JyRepertory().findFirst("select * from jy_repertory where id  = ?",id);
		int R2 = repertory.getResidue();
		int n = 0;
		if(number >= R2) {
			repertory.setResidue(0);
			n = R2;
		}else {
			repertory.setResidue(R2-number);
			n = number;
		}
		repertory.update();
		JyRepertoryLog log = new JyRepertoryLog();
		log.setRepertoryId(repertory.getId());
		log.setProductId(repertory.getProductId());
		log.setProductName(repertory.getProductName());
		log.setClientId(clientId);
		JyClient client = new JyClient().findFirst("select * from jy_client where client_id = ?",clientId);
		log.setClientName(client.getClientName());
		log.setCreateTime(new Date());
		log.setNumber(n);
		log.setPlantId(repertory.getPlantId());
		log.save();
		renderJson(Ret.ok());
	}
	
	public void lookLog() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils(" from jy_repertory_log where 1=1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyRepertoryLog> log = new JyRepertoryLog().paginate(getParaToInt("page"),getParaToInt("limit"),"select * ",sql.toString());
		renderJson(Ret.ok("page", log));
	}
	
	public void invoice() {
		int id = getParaToInt("id");
		int is_invoice = getParaToInt("invoice");
		System.out.println(is_invoice);
		JyRepertoryLog log = new JyRepertoryLog().findFirst("select * from jy_repertory_log where id = ?",id);
		log.setIsInvoice(is_invoice);
		log.update();
		renderJson(Ret.ok());
	}
}
