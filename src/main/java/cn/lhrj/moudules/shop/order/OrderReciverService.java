package cn.lhrj.moudules.shop.order;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopOrderLog;
import cn.lhrj.common.model.ShopOrderReciver;
import cn.lhrj.common.model.ShopShipping;
import cn.lhrj.moudules.index.MessageService;

public class OrderReciverService {
    
	
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("order.queryListReciverList", kv);
		Page<Record> records=Db.paginate(page,limit,sPara);
		return records;
	}
	
	public List<Record> queryAll(Kv kv) {
		SqlPara sPara=Db.getSqlPara("category.queryListReciverList", kv);
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
		SqlPara sPara=Db.getSqlPara("order.queryReciverObject", menuid);
		return Db.findFirst(sPara);
	}
    /**
     * 确定收货
     *
     * @param id
     * @return
     */	
	public Ret confirm(String id) {
		ShopOrderReciver order=new ShopOrderReciver().findById(Integer.parseInt(id));
		Long shippingStatus = order.getShippingStatus();//发货状态
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
		ShopOrderReciver o2=FastJson.getJson().parse(string, ShopOrderReciver.class);
		ShopOrderReciver order2=new ShopOrderReciver().findById(o2.getId());
		ShopOrder order=new ShopOrder().findById(order2.getParentId());
	    Long payStatus = order.getPayStatus();//付款状态
        if (2 != payStatus) {
        	 return Ret.fail("msg","此订单未付款！").set("code", 500);
        }
        ShopOrderLog log=new ShopOrderLog();
        log.setCreateTime(new Date());
        log.setOrderId(order.getId());
        
        ShopShipping shipping=new ShopShipping().findById(o2.getShippingId());
        System.out.println("shipping"+shipping);
        System.out.println("shipping"+o2.getShippingId());
        if (null != shipping) {
        	order2.setShippingName(shipping.getName());
        	log.setDetail(order2.getConsignee()+"的礼品已寄出,"+shipping.getName()+":"+order2.getShippingNo());
        }
        order2.setShippingId(o2.getShippingId());
        order2.setShippingNo(o2.getShippingNo());
        order2.setOrderStatus(300L);//订单已发货
        order2.setShippingStatus(1L);//已发货
        order2.update();
        log.save();
    	MessageService.init();
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
	
	public void save(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.save();
	}
	public void update(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.update();
	}
	public void delectByIds(String string) {
		JSONArray array=JSONArray.parseArray(string);
		for (int i = 0; i < array.size(); i++) {
			Db.deleteById("shop_category",array.get(i));
		}
	}

}
