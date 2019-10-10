package cn.lhrj.moudules.jy.plant;

import java.util.List;

import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyPlant;
import cn.lhrj.component.base.BaseProjectController;

public class PlantController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/plant.html");
	}
	
	public void list() {
		Page<JyPlant> plantPage = new JyPlant().paginate(getParaToInt("page"),getParaToInt("limit"),"select *","from jy_plant where state = 1");
		renderJson(Ret.ok("page", plantPage));
	}
	
	public void allList() {
		List<JyPlant> plants = new JyPlant().find("select * from jy_plant where state = 1");
		renderJson(Ret.ok("list", plants));
	}
	
	public void delect() {
		int plantId = getParaToInt("id");
		JyPlant plant = new JyPlant().findFirst("select * from jy_plant where id = ?",plantId);
		plant.setState(0);
		boolean result = plant.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void save() {
		JyPlant plant=FastJson.getJson().parse(HttpKit.readData(getRequest()), JyPlant.class);
		plant.setState(1);
		plant.save();
		renderJson(Ret.ok());
	}
}
