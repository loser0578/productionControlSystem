package cn.lhrj.moudules.jy.company;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.Ret;

import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class CompanyController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/company.html");
	}
	
	public void select() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_order where state = 1 and _status = 2");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		int year = getParaToInt("year");
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < 13; i++) {
			sql.whereEquals("year(success_time)", year+"");
			sql.whereEquals("month(success_time)", i+"");
			List<JyOrder> orderList = new JyOrder().find(sql.toString());
			if(0 == orderList.size()) {
				list.add(100);
			}else {
				int number  = 0;
				for (int j = 0; j < orderList.size(); j++) {
					//未超时数量
					JyOrder order = orderList.get(i);
					Date successTime = order.getSuccessTime();
					Date deliveryTime = order.getDeliveryTime();
					if(0<DateUtils.getDateDiff(DateUtils.DATE_INTERVAL_DAY,deliveryTime,successTime)) {
						number+=1;
					}
				}
				if(0 == number) {
					list.add(0);
				}else {
					int plant =  number*100/orderList.size();
					list.add(plant);
				}
			}
		}
		renderJson(Ret.ok("list", list));
	}
}
