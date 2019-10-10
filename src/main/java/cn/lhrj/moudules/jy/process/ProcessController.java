package cn.lhrj.moudules.jy.process;

import java.util.List;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class ProcessController extends BaseProjectController{
	
	ProcessService ps = ProcessService.me;
	
	public void index() {
		render("/page/jy/process.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyProcess> records=ps.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void delect() {
		int processId = getParaToInt("id");
		JyProcess process = new JyProcess().findFirst("select * from jy_process where process_id = ?",processId);
		process.setState(0);
		boolean result = process.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void update() {
		ps.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
	
	public void save() {
		System.out.println(getLoginAccount());
		int plantId = getLoginAccount().getPlantId();
		System.out.println("xx"+plantId);
		ps.save(HttpKit.readData(getRequest()),plantId);
		renderJson(Ret.ok());
	}
	
	public void allList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_process where state = 1");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyProcess> processList = new JyProcess().find(sql.toString());
		renderJson(Ret.ok("processList", processList));
	}
}
