package cn.lhrj.moudules.jy.outshop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.utils.SQLUtils;

public class OutShopService {
	
	public static final OutShopService me = new OutShopService();
	
	public Page<JyOrder> getPage(int page,int limit,int plantId){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SQLUtils sql = new SQLUtils("from jy_order where is_outshop = 1 and state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyOrder> pageList = new JyOrder().paginate(page,limit,"select * ",sql.toString());
		List<JyOrder> list = pageList.getList();
		for (int i = 0; i < list.size(); i++) {
			JyOrder order = list.get(i);
			Date orderDate = order.getOrderTime();
			String orderTime = sdf.format(orderDate);
			order.put("orderTime", orderTime);
			Date outDate = order.getOutTime();
			String outTime = sdf.format(outDate);
			order.put("outTime", outTime);
		}
		return pageList;
	}
}
