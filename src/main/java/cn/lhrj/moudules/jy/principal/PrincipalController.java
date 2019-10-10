package cn.lhrj.moudules.jy.principal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;
import net.sf.ehcache.search.expression.And;

public class PrincipalController extends BaseProjectController{

	PrincipalService ds = PrincipalService.me;
	
	public void index() {
		render("/page/jy/principal.html");
	}
	
	public void getDeliveryEcharts() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_order where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyOrder> orderList = new JyOrder().find(sql.toString());
		//所有订单负责人List(未去重)
		List<String> allPrincipalList = new ArrayList<String>();
		//已去重负责人list
		List<String> rincipalList = new ArrayList<String>();
		//存入所有负责人
		for (int i = 0; i < orderList.size(); i++) {
			String principal = orderList.get(i).getPrincipal();
			allPrincipalList.add(principal);
		}
		//负责人list去重
		for (int i = 0; i < allPrincipalList.size(); i++) {
			String principal = allPrincipalList.get(i);
			if(!(rincipalList.contains(principal))) {
				rincipalList.add(principal);
			}
		}
		//返回的数据对象
		JSONObject object = new JSONObject();
		List<Integer> ratioList = new ArrayList<Integer>();
		//遍历负责人数组 根据负责人查询该人订单
		for (int i = 0; i < rincipalList.size(); i++) {
			//超时次数
			int count = 0;
			String principal = rincipalList.get(i);
			SQLUtils sql2 = new SQLUtils("select * from jy_order where state = 1 and `_status` = 2");
			if (-1 == plantId) {
				sql2.whereEquals("principal", principal);
			}else {
				sql2.whereEquals("principal", principal);
				sql2.whereEquals("plant_id", plantId);
			}
			List<JyOrder> orders = new JyOrder().find(sql2.toString());
			if (null == orders) {
				ratioList.add(100);
			}else {
				for (int j = 0; j < orders.size(); j++) {
					//完成时间
					Date successDate = orders.get(j).getSuccessTime();
					//需求时间
					Date deliveryDate = orders.get(j).getDeliveryTime();
					//完成和要求比 如果完成时间小于要求时间 那返回负数（已完成） 正数都是超时
					double time = DateUtils.getDateDiff(DateUtils.DATE_INTERVAL_DAY,successDate,deliveryDate);
					if(time>0.00) {
						count += 1;
					}
				}
				if (0 == count) {
					ratioList.add(100);
				}else {
					//算百分比
					int size = orders.size();//总数
					int ratio = 100-(size/count*100);
					ratioList.add(ratio);
				}
			}
			object.put("principal", rincipalList);
			object.put("ratio", ratioList);
		}
		renderJson(Ret.ok("list", object));
	}
	
	public void getQueryEcharts() {
		int plantId = getAccountId();
		String timeString= HttpKit.readData(getRequest());
		JSONObject selectObject = JSONObject.parseObject(timeString);
		String year = "";
		String quarter = "";
		String month = "";
		//获取年份
		if ("".equals(selectObject.getString("year"))) {
			Date timeDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			year = sdf.format(timeDate);
		}else {
			year = selectObject.getString("year");
		}
		//获取季度
		if("".equals(selectObject.getString("quarter"))) {
			
		}else {
			quarter = selectObject.getString("quarter");
		}
		//获取月份
		if ("".equals(selectObject.getString("month"))) {

		}else {
			month = selectObject.getString("month");
		}
		//判断条件去拼装
		SQLUtils sql = new SQLUtils("select * from jy_order where state = 1 and year(delivery_time) = ? and principal = ? and `_status` = 2 ");
		if (-1 == plantId) {
			if("".equals(quarter)) {
				if ("".equals(month)) {
					sql.append(" and plant_id = "+plantId);
				}else {
					sql.append(" and month(delivery_time) = "+month);
					sql.append(" and plant_id = "+plantId);
				}
			}else {
				if("".equals(month)) {
					sql.append(" and QUARTER(delivery_time) = "+quarter);
					sql.append(" and plant_id = "+plantId);
				}else {
					sql.append(" and month(delivery_time) = "+month);
					sql.append(" and plant_id = "+plantId);
				}
			}
		}else {
			sql.whereEquals("plant_id", plantId);
			if("".equals(quarter)) {
				if ("".equals(month)) {
					sql.append(" and plant_id = "+plantId);
				}else {
					sql.append(" and month(delivery_time) = "+month);
					sql.append(" and plant_id = "+plantId);
				}
			}else {
				if("".equals(month)) {
					sql.append(" and QUARTER(delivery_time) = "+quarter);
					sql.append(" and plant_id = "+plantId);
				}else {
					sql.append(" and month(delivery_time) = "+month);
					sql.append(" and plant_id = "+plantId);
				}
			}
		}
		SQLUtils sql2 = new SQLUtils("select * from jy_order where state = 1 ");
		if (-1 == plantId) {
		}else {
			sql2.whereEquals("plant_id", plantId);
		}
		List<JyOrder> orderList = new JyOrder().find(sql2.toString());
		//所有订单负责人List(未去重)
		List<String> allPrincipalList = new ArrayList<String>();
		//已去重负责人list
		List<String> rincipalList = new ArrayList<String>();
		//存入所有负责人
		for (int i = 0; i < orderList.size(); i++) {
			String principal = orderList.get(i).getPrincipal();
			allPrincipalList.add(principal);
		}
		//负责人list去重
		for (int i = 0; i < allPrincipalList.size(); i++) {
			String principal = allPrincipalList.get(i);
			if(!(rincipalList.contains(principal))) {
				rincipalList.add(principal);
			}
		}
		//返回的数据对象
		JSONObject object = new JSONObject();
		List<Integer> ratioList = new ArrayList<Integer>();
		//遍历负责人数组 根据负责人查询该人订单
		for (int i = 0; i < rincipalList.size(); i++) {
			//超时次数
			int count = 0;
			String principal = rincipalList.get(i);
			List<JyOrder> orders = new JyOrder().find(sql.toString(),year,principal);
			if (null == orders) {
				ratioList.add(100);
			}else {
				for (int j = 0; j < orders.size(); j++) {
					//完成时间
					Date successDate = orders.get(j).getSuccessTime();
					//需求时间
					Date deliveryDate = orders.get(j).getDeliveryTime();
					//完成和要求比 如果完成时间小于要求时间 那返回负数（已完成） 正数都是超时
					double time = DateUtils.getDateDiff(DateUtils.DATE_INTERVAL_DAY,successDate,deliveryDate);
					if(time>0.00) {
						count += 1;
					}
				}
				if (0 == count) {
					ratioList.add(100);
				}else {
					//算百分比
					int size = orders.size();//总数
					int ratio = 100-(size/count*100);
					ratioList.add(ratio);
				}
			}
			object.put("principal", rincipalList);
			object.put("ratio", ratioList);
		}
		System.out.println(object);
		renderJson(Ret.ok("list", object));
	}
}
