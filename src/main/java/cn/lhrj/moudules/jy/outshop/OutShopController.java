package cn.lhrj.moudules.jy.outshop;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyOrder;
import cn.lhrj.component.base.BaseProjectController;

public class OutShopController extends BaseProjectController{
	
	OutShopService os = OutShopService.me;
	
	public void index() {
		render("/page/jy/outshop.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyOrder> records=os.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void edit() {
		int plantId = getLoginAccount().getPlantId();
		int id = getParaToInt("id");
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ? and plant_id = ?",id,plantId);
		order.setIsOutshop(0);
		order.update();
		renderJson(Ret.ok());
	}
	
	public void invoice() {
		int plantId = getLoginAccount().getPlantId();
		int id = getParaToInt("id");
		int is_invoice = getParaToInt("invoice");
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ? and plant_id = ?",id,plantId);
		order.setIsInvoice(is_invoice);
		order.update();
		renderJson(Ret.ok());
	}
}
