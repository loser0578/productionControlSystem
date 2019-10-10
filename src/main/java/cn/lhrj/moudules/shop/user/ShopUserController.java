package cn.lhrj.moudules.shop.user;



import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import cn.lhrj.common.model.ShopUser;
import cn.lhrj.component.base.BaseProjectController;

public class ShopUserController extends BaseProjectController{
	
	UserService srv=UserService.me;
	
	public void index() {
		render("/page/shop/shopuser.html");
	}
	public void list() {
		Kv para=new Kv();
		System.out.println("para:"+para);
		int page = getParaToInt("page");
		System.out.println("page"+page);
		ShopUser model=getModel(ShopUser.class,"",true);
		if (model._getAttrValues().length != 0) {
			para.set("orderStatus", model.get("order_status"));
			para.set("orderType", model.get("order_type"));
			para.set("orderSn", model.get("order_sn"));
			para.set("orderStatus", model.get("order_status"));

		}

		Page<Record> records=srv.paginate(getParaToInt("page"),getParaToInt("limit"),para);
		renderJson(Ret.ok("page", records));
	}
	/**
     * 菜单信息
     */
	public void info() {
		renderJson(Ret.ok("user", srv.queryObject(getParaToLong())));
	}
	
	/**
     * 充值
     */
	public void reCharge() {
		
		String requestPara=HttpKit.readData(getRequest());
		Kv shopUser=FastJson.getJson().parse(requestPara, Kv.class);
		renderJson(Ret.ok("user", srv.userRecharge(shopUser.getInt("uid"),shopUser.getInt("rid"))));
	}
	/**
     * 面额
     */
	public void getAmountid() {
		renderJson(srv.getAmountid());
	}
	
    /**
     * 查看所有列表
     */
	public void queryAll() {
		renderJson(Ret.ok("list",srv.queryAll()));
	}
	
	
	
    /**
     * 确定收货
     *
     * @param id
     * @return
     */
	public void confirm() {
	
		
		renderJson(srv.confirm(HttpKit.readData(getRequest())));
	
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
		srv.delectByIds(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
}
