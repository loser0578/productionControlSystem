package cn.lhrj.moudules.shop.order;

import java.util.List;

import com.jfinal.json.FastJson;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopShipping;

public class OrderService {
    
	public static final OrderService me = new OrderService();
	
	public Page<Record> paginate(int page,int limit,Kv kv) {
		SqlPara sPara=Db.getSqlPara("order.queryList", kv);
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
		SqlPara sPara=Db.getSqlPara("order.queryAdminObject", menuid);
		return Db.findFirst(sPara);
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
	
	public void save(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.save();
	}
	public void update(String shopSpecification) {
		ShopCategory shopCategory=FastJson.getJson().parse(shopSpecification, ShopCategory.class);
		shopCategory.update();
	}


}
