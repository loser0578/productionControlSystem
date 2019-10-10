package cn.lhrj.moudules.shop.user;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopConsumeLog;
import cn.lhrj.common.model.ShopDenomination;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopShipping;
import cn.lhrj.common.model.ShopUser;
import cn.lhrj.common.utils.DateUtils;

public class UserService {
    
	public static final UserService me = new UserService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("shopuser.queryList", kv);
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
		SqlPara sPara=Db.getSqlPara("shopuser.queryObject", menuid);
		return Db.findFirst(sPara);
	}
	
	/**
     * 充值
     */
	public Ret userRecharge(int uid,int rid) {
		/*
		 * Record user = new Record(); user = Db.findById("shop_user", uid);
		 */
		
		ShopUser user = new ShopUser().findFirst("select * from shop_user where id = ?",uid);
		ShopDenomination deno = new ShopDenomination().findFirst("select * from shop_denomination where id = ?",rid);
		//充值的钱
		double denoMoney = deno.getDenomination();
		//用户钱包余额
		double beforeMoney = user.getDouble("money");
		//充值成功之后的钱
		double changeMoney = beforeMoney + denoMoney;
		user.set("money", changeMoney);
		//新增log
		ShopConsumeLog log = new ShopConsumeLog();
		log.setUserId(uid);
		log.setCreateTime(DateUtils.getNowDate());
		log.setUpdateTime(DateUtils.getNowDate());
		log.setOrderId("后台充值");
		log.setType(1);
		log.setStatus(1);
		log.setState(2);
		log.setBeforeMoney(beforeMoney);
		log.setAfterMoney(changeMoney);
		log.setChangeMoney(denoMoney);
		boolean result = Db.tx(() -> {
			boolean success2 = log.save();
			boolean success = user.update();
			if (success&&success2) {
				return true;
			}else {
				return false;
			}
		});
		if (result) {
			return Ret.ok();
		}else {
			return Ret.fail("msg", "充值失败");
		}
	}
	/**
     * 获取面额
     */
	public List<Record> getAmountid() {
		List<Record> denominations = Db.find("select * from shop_denomination where _status = 1 order by denomination asc");
		return denominations;
	}

	
    /**
     * 查看所有列表
     */
	public List<Record> queryAll() {
		SqlPara sPara=Db.getSqlPara("shopuser.queryList");
		return Db.find(sPara);	
	}
    /**
     * 确定收货
     *
     * @param id
     * @return
     */	
	public Ret confirm(String id) {
		ShopOrder order=new ShopOrder().findById(Integer.parseInt(id));
		Long shippingStatus = order.getShippingStatus();//发货状态
	    Long payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
            return Ret.fail("msg","此订单未付款，不能确认收货！").set("code", 500);
        }
        if (4 == shippingStatus) {
        	 return Ret.fail("msg","此订单处于退货状态，不能确认收货！").set("code", 500);
        }
        if (0 == shippingStatus) {
        	return Ret.fail("msg","此订单未发货，不能确认收货！").set("code", 500);
        }
        order.setShippingStatus(2L);
        order.setOrderStatus(301L);
        order.update();
        return Ret.ok();
	}
    /**
          * 发货
     *
     * @param order
     * @return
     */
	public Ret sendGoods(String string){
		ShopOrder order2=FastJson.getJson().parse(string, ShopOrder.class);
		ShopOrder order=new ShopOrder().findById(order2.getId());
	    Long payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
        	 return Ret.fail("msg","此订单未付款！").set("code", 500);
        }
        ShopShipping shipping=new ShopShipping().findById(order.getShippingId());
        if (null != shipping) {
            order.setShippingName(shipping.getName());
        }
        order.setOrderStatus(300L);//订单已发货
        order.setShippingStatus(1L);//已发货
        return Ret.ok();
	}
	
	
    /**
     * 获取不包含按钮的菜单列表
     */	
	public List<Record> select() {
	    Kv kv=Kv.by("parentId", "0");
        //查询列表数据
		SqlPara sPara=Db.getSqlPara("goods.queryList", kv);
		List<Record> records=Db.find(sPara);
		return records;
	}
	
	public void save(String string) {
		ShopUser shopUser=FastJson.getJson().parse(string, ShopUser.class);
		shopUser.save();
	}
	public void update(String string) {
		ShopUser shopUser=FastJson.getJson().parse(string, ShopUser.class);
		shopUser.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {

			Db.deleteById("shop_user",array.get(i));
		}
	}
}
