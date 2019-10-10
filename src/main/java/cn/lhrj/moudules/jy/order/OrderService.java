package cn.lhrj.moudules.jy.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyAcceptParts;
import cn.lhrj.common.model.JyAcceptProcess;
import cn.lhrj.common.model.JyAcceptProduct;
import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyReport;
import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.SQLUtils;

public class OrderService {
	
	public static final OrderService me = new OrderService();
	
	public Ret update(String string) {
		return Ret.ok();
	}
	
	public List<JyOrder> query(String string,int plantId){
		JSONObject object = JSONObject.parseObject(string);
		SQLUtils sql = new SQLUtils("select * from jy_order where state = 1 and is_shipment = 0 and is_outshop = 0 ");
		if (-1 == plantId) {

		}else {
		
			sql.whereEquals("plant_id", plantId);
		}
		sql.whereEquals("plant_id", plantId);
		if(!("".equals(object.getString("orderId")))) {
			sql.whereEquals("order_id", object.getString("orderId"));

		}
		if(!("".equals(object.getString("batchId")))) {
			sql.whereEquals("batch_id", object.getString("batchId"));

		}
		if(!("".equals(object.getString("principal")))) {
			sql.whereEquals("principal", object.getString("principal"));

		}
		if(!("".equals(object.getString("level")))) {
			sql.whereEquals("level", object.getString("level"));

		}
		if(!("".equals(object.getString("status")))) {
			sql.whereEquals("_status", object.getString("status"));
		}
		List<JyOrder> orders = new JyOrder().find(sql.toString());
		if(orders.size()>0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < orders.size(); i++) {
				JyOrder order = orders.get(i);
				//交货日期
				Date deliveryTime = order.getDeliveryTime();
				String deliveryTimeString = sdf.format(deliveryTime);
				order.put("delivery_time", deliveryTimeString);
				order.put("realPlan", countOrder(order.getId())+"%");
				order.put("progress", countOrder(order.getId()));
				//距离交货日期判断
				Date nowTime = DateUtils.getNowDate();
				//时间差 超时为负数
				double subTime = DateUtils.getDateDiff(DateUtils.DATE_INTERVAL_DAY,deliveryTime,nowTime);
				//返回时间 +1
				int disTime = (int)subTime+1;
				if(disTime>20) {
					order.put("disTime", disTime+"天");
					order.put("color", "#23C7E8");
				}else if (disTime>10) {
					order.put("disTime", disTime+"天");
					order.put("color", "#00E849");
				}else if(disTime>5){
					order.put("disTime", disTime+"天");
					order.put("color", "#F5F500");
				}else if (disTime<=5&&disTime>0) {
					order.put("disTime", disTime+"天");
					order.put("color", "#FF0000");
				}else {
					order.put("disTime", "超时");
					order.put("color", "#9900FF");
				}
			}
		}else {
			orders = new ArrayList<JyOrder>();
		}
		return orders;
	}
	
	public Page<JyOrder> getPage(int page,int limit,int plantId){
		SQLUtils sql = new SQLUtils("from jy_order where state = 1 and is_shipment = 0 and is_outshop = 0 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyOrder> orderPageList = new JyOrder().paginate(page, limit, "select *",sql.toString());
		List<JyOrder> ordersList = orderPageList.getList();
		if(ordersList.size()>0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < ordersList.size(); i++) {
				JyOrder order = ordersList.get(i);
				//交货日期
				Date deliveryTime = order.getDeliveryTime();
				String deliveryTimeString = sdf.format(deliveryTime);
				order.put("delivery_time", deliveryTimeString);
				order.put("realPlan", countOrder(order.getId())+"%");
				order.put("progress", countOrder(order.getId()));
				//距离交货日期判断
				Date nowTime = DateUtils.getNowDate();
				//时间差 超时为负数
				double subTime = DateUtils.getDateDiff(DateUtils.DATE_INTERVAL_DAY,deliveryTime,nowTime);
				//返回时间 +1
				int disTime = (int)subTime+1;
				if(disTime>20) {
					order.put("disTime", disTime+"天");
					order.put("color", "#23C7E8");
				}else if (disTime>10) {
					order.put("disTime", disTime+"天");
					order.put("color", "#00E849");
				}else if(disTime>5){
					order.put("disTime", disTime+"天");
					order.put("color", "#F5F500");
				}else if (disTime<=5&&disTime>0) {
					order.put("disTime", disTime+"天");
					order.put("color", "#FF0000");
				}else {
					order.put("disTime", "超时");
					order.put("color", "#9900FF");
				}
			}
		}else {
			ordersList = new ArrayList<JyOrder>();
		}
		return orderPageList;
	}
	
	public Ret save(String string) {
		return Ret.ok();
	}
	
	//计算单个订单的进度
	public Integer countOrder(int orderId) {
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",orderId);
		String productString = order.getAcceptProduct();
		JSONArray productArray = JSONArray.parseArray(productString);
		//所有产品的进度
		int allProductPlan = 0;
		for (int i = 0; i < productArray.size(); i++) {
			int productId = productArray.getIntValue(i);
			allProductPlan += countProduct(productId);
		}
		if(allProductPlan == 0) {
			return 0;
		}else {
			int plan = allProductPlan/productArray.size();
			if(100==plan) {
				return 100;
			}
			return plan;
		}
	}
	
	//计算单个产品的进度
	public Integer countProduct(int productId) {
		JyAcceptProduct product = new JyAcceptProduct().findFirst("select * from jy_accept_product where id = ?",productId);
		String partsString = product.getAcceptParts();
		JSONArray partsArray = JSONArray.parseArray(partsString);
		int allPartsPlan = 0;
		for (int i = 0; i < partsArray.size(); i++) {
			int partsId = partsArray.getIntValue(i);
			allPartsPlan += countParts(partsId);
		}
		if (allPartsPlan == 0) {
			return 0;
		}else {
			int plan = allPartsPlan/partsArray.size();
			if(100==plan) {
				return 100;
			}
			return plan;
		}
	}
	
	//计算单个部件的进度 
	public Integer countParts(int partsId) {
		JyAcceptParts parts = new JyAcceptParts().findFirst("select * from jy_accept_parts where id = ?",partsId);
		//所需工序
		String acceptProcessString = parts.getAcceptProcess();
		JSONArray acceptProcessArray = JSONArray.parseArray(acceptProcessString);
		//一个部件所有工序完成数量
		int allProcessNumber = 0;
		for (int i = 0; i < acceptProcessArray.size(); i++) {
			int processId = acceptProcessArray.getIntValue(i);
			JyAcceptProcess acceptProcess = new JyAcceptProcess().findFirst("select * from jy_accept_process where id = ?",processId);
			//一个部件中的一个工序完成数量
			int processRequireNumber = acceptProcess.getRequireNumber();
			allProcessNumber += processRequireNumber;
		}
		//需要生产部件的数量
		int number = parts.getPartsNumber();
		//部件数量x工序数量(系数)
		int coefficient = number*(acceptProcessArray.size());
		//单个零部件进度
		if(allProcessNumber == 0) {
			return 0;
		}else {
			int pNumber = allProcessNumber * 100;
			int plan = pNumber/coefficient;
			if(100 == plan) {
				return 100;
			}
			return plan;
		}
	}

	public Page<JyAcceptProduct> getAcProductPage(int page, int limit, int orderId,int plantId) {
		SQLUtils sql = new SQLUtils("from jy_accept_product where 1=1 ");
		if (-1 == plantId) {
			sql.whereEquals("parent_order_id", orderId);
		}else {
			sql.whereEquals("parent_order_id", orderId);
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyAcceptProduct> acProductPageList = new JyAcceptProduct().paginate(page, limit, "select * ",sql.toString());
		List<JyAcceptProduct> productList = acProductPageList.getList();
		for (int i = 0; i < productList.size(); i++) {
			JyAcceptProduct acAcceptProduct = productList.get(i);
			int productId = acAcceptProduct.getId();
			int productPlan = countProduct(productId);
			acAcceptProduct.put("productPlan", productPlan);
			List<JyAcceptParts> acAcceptPartsList = new JyAcceptParts().find("select * from jy_accept_parts where parent_product_id = ?",productId);
			//所有工序 
			List<JyProcess> processList = new JyProcess().find("select * from jy_process where state = 1 order by sort ASC");
			//获取所有该产品部件遍历
			for (int j = 0; j < acAcceptPartsList.size(); j++) {
				JyAcceptParts acceptParts = acAcceptPartsList.get(j);
				int acceptPartsId = acceptParts.getId();
				//计算部件进度
				int partsPlan = countParts(acceptPartsId);
				System.out.println();
				acceptParts.put("partsPlan", partsPlan+"");
				//用一个数组存所有工序的id 用来删除和循环
				List<String> processIdList = new ArrayList<String>();
				for (int u = 0; u < processList.size(); u++) {
					JyProcess process = processList.get(u);
					int processId = process.getProcessId();
					processIdList.add(processId+"");
				}
				//取得部件下所有工序
				List<JyAcceptProcess> acceptProcessList = new JyAcceptProcess().find("select * from jy_accept_process where parent_parts_id = ?",acceptPartsId);
				for (int k = 0; k < acceptProcessList.size(); k++) {
					JyAcceptProcess  acceptProcess = acceptProcessList.get(k);
					//这个工序数据工序表里的id
					String processId = acceptProcess.getProcessId()+"";
					if (processIdList.contains(processId)) {			
						processIdList.remove(processId);
						acceptParts.put("process_id"+processId,acceptProcess.getRequireNumber());
					}
				}
				for (int k = 0; k < processIdList.size(); k++) {
					acceptParts.put("process_id"+processIdList.get(k), "无需生产");
				}
			}
			acAcceptProduct.put("acceptPartsList", acAcceptPartsList);
		}
		return acProductPageList;
	}
	
	public List<JyAcceptParts> getAcParts(int productId,int plantId){
		SQLUtils sql = new SQLUtils("select * from jy_accept_product where 1=1 ");
		if (-1 == plantId) {
			sql.whereEquals("id", productId);
		}else {
			sql.whereEquals("id", productId);
			sql.whereEquals("plant_id", plantId);
		}
		JyAcceptProduct product = new JyAcceptProduct().findFirst(sql.toString());
		String partsString = product.getAcceptParts();
		JSONArray partsArray = JSONArray.parseArray(partsString);
		List<JyAcceptParts> partsList = new ArrayList<JyAcceptParts>();
		for (int i = 0; i < partsArray.size(); i++) {
			int partsId = partsArray.getIntValue(i);
			JyAcceptParts parts = new JyAcceptParts().findFirst("select * from jy_accept_parts where id = ? and plant_id = ?",partsId,plantId);
			partsList.add(parts);
		}
		return partsList;
	}
	
	public List<JyAcceptProcess> getAcProcess(int partsId,int plantId){
		SQLUtils sql = new SQLUtils("select * from jy_accept_process where 1=1 ");
		if (-1 == plantId) {
			sql.whereEquals("parent_parts_id", partsId);
		}else {
			sql.whereEquals("parent_parts_id", partsId);
			sql.whereEquals("plant_id", plantId);
		}
		List<JyAcceptProcess> processList = new JyAcceptProcess().find(sql.toString());
		return processList;
	}
	
	public void saveReport(String string,int plantId) {
		Date nowDate = new Date();
		JSONObject reportObject = JSONObject.parseObject(string);
		JyReport report = new JyReport();
		int processId = reportObject.getIntValue("process_id");
		report.setProcessId(processId);
		report.setReportTime(reportObject.getDate("report_time"));
		//实际生产
		int requireNumber = reportObject.getIntValue("require_number");
		report.setRequireNumber(requireNumber);
		int scrapNumber = reportObject.getIntValue("scrap_number");
		report.setScrapNumber(scrapNumber);
		report.setDelectNumber(reportObject.getIntValue("delect_number"));
		JyAcceptProcess acceptProcess = new JyAcceptProcess().findFirst("select * from jy_accept_process where id = ?",processId);
		report.setOrderId(acceptProcess.getParentOrderId());
		report.setProductId(acceptProcess.getParentProductId());
		report.setPartsId(acceptProcess.getParentPartsId());
		report.setParentProcessId(acceptProcess.getProcessId());
		report.setPlantId(plantId);
		report.save();
		//这里开始计算各子父级生产进度
		
		//先更新工序表
		//部件实际开始时间
		Date ProcessStartTime = acceptProcess.getRequireStart();
		if(null == ProcessStartTime) {
			acceptProcess.setRequireStart(nowDate);
		}
		//工序已经生产数量
		int processNumber = acceptProcess.getRequireNumber();
		//这次生产数量+之前的生产数量
		int processAddNumber = requireNumber+processNumber;
		acceptProcess.setRequireNumber(processAddNumber);
		//早前的报废数量
		int beforeScrapNumber = acceptProcess.getScrapNumber();
		acceptProcess.setScrapNumber(beforeScrapNumber+scrapNumber);
		//部件数量就是工序数量
		int acceptPartsId = acceptProcess.getParentPartsId();
		JyAcceptParts acceptParts = new JyAcceptParts().findFirst("select * from jy_accept_parts where id = ?",acceptPartsId);
		//部件需要生产的数量
		int partsNumber = acceptParts.getPartsNumber();
		if(processAddNumber>=partsNumber) {
			acceptProcess.setStatus(2);
			acceptProcess.setSuccessTime(nowDate);
			acceptProcess.setRequireEnd(nowDate);
		}else {
			acceptProcess.setStatus(1);
		}
		acceptProcess.update();
		
		//部件实际生产数量用工序的完成度算
		int partsCount = countParts(acceptPartsId);
		int partsRequireNumber = partsCount*partsNumber/100;
		acceptParts.setRequireNumber(partsRequireNumber);
		if(null == acceptParts.getRequireStart()) {
			acceptParts.setStatus(1);
			acceptParts.setRequireStart(nowDate);
		}
		if(100 <= partsCount) {
			acceptParts.setStatus(2);
			acceptParts.setSuccessTime(nowDate);
			acceptParts.setRequireEnd(nowDate);
		}
		acceptParts.update();
		
		/** 计算产品*/
		//找到该产品
		JyAcceptProduct acceptProduct = new JyAcceptProduct().findFirst("select * from jy_accept_product where id = ?",acceptParts.getParentProductId());
		//产品需要生产的数量
		int productNumber = acceptProduct.getProductNumber();
		//完成度
		int productCount = countProduct(acceptParts.getParentProductId());
		//实际生产的数量
		int productRequireNumber = productCount*productNumber/100;
		acceptProduct.setRequireNumber(productRequireNumber);
		if(null == acceptProduct.getRequireStart()) {
			acceptProduct.setRequireStart(nowDate);
		}
		//产品状态
		if(100<=productCount) {
			acceptProduct.setStatus(2);
			acceptProduct.setRequireEnd(nowDate);
			acceptProduct.setSuccessTime(nowDate);
		}else {
			acceptProduct.setStatus(1);
		}
		acceptProduct.update();
		
		/** 计算订单*/
		//订单
		int orderId = acceptProduct.getParentOrderId();
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ? and plant_id = ?",orderId,plantId);
		int orderCount = countOrder(orderId);
		if(100<=orderCount) {
			order.setStatus(2);
			order.setSuccessTime(nowDate);
		}else {
			order.setStatus(1);
		}
		order.update();
	}

	
	//写在控制器里了 这里不需要了
	public void delect(int id) {
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",id);
		order.setState(0);
		order.update();
		List<JyAcceptProduct> products = new JyAcceptProduct().find("select * from jy_accept_product where parent_order_id = ?",id);
		for (int i = 0; i < products.size(); i++) {
			JyAcceptProduct product = products.get(i);
			product.setState(0);
			product.update();
		}
		List<JyAcceptParts> partsList = new JyAcceptParts().find("select * from jy_accept_parts where parent_order_id = ?",id);
		for (int i = 0; i < partsList.size(); i++) {
			JyAcceptParts parts = partsList.get(i);
			parts.setState(0);
			parts.update();
		}
		List<JyAcceptProcess> processes = new JyAcceptProcess().find("select * from jy_accept_process where parent_order_id = ?",id);
		for (int i = 0; i < processes.size(); i++) {
			JyAcceptProcess process = processes.get(i);
			process.setState(0);
			process.update();
		}
	}
	
	//部件报工表格
	public Page<JyReport> getReportPage(int page,int limit,int partsId,int plantId){
		SQLUtils sql = new SQLUtils("from jy_report where 1=1 ");
		if (-1 == plantId) {
			sql.whereEquals("parts_id", partsId);
			
		}else {
			sql.whereEquals("parts_id", partsId);
			sql.whereEquals("plant_id", plantId);
		}
		Page<JyReport> reportPage = new JyReport().paginate(page, limit, "select *",sql.toString());
		List<JyReport> reportList = reportPage.getList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < reportList.size(); i++) {
			JyReport report = reportList.get(i);
			JyAcceptProcess process = new JyAcceptProcess().findFirst("select * from jy_accept_process where id = ?",report.getProcessId());
			report.put("process_name", process.getProcessName());
			JyAcceptParts parts = new JyAcceptParts().findFirst("select * from jy_accept_parts where id = ?",report.getPartsId());
			report.put("parts_name", parts.getPartsName());
			JyAcceptProduct product = new JyAcceptProduct().findFirst("select * from jy_accept_product where id = ?",report.getProductId());
			report.put("product_name", product.getProductName());
			JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",report.getOrderId());
			report.put("order_id", order.getOrderId());
			report.put("batch_id", order.getBatchId());
			report.put("report_time", sdf.format(report.getReportTime()));
		}
		return reportPage;
	}
	
		//部件报工筛选表格
		public Page<JyReport> getReportQuery(int page,int limit,int partsId,int processId,int plantId){
			SQLUtils sql = new SQLUtils(" from jy_report a left join jy_process b on a.parent_process_id = b.process_id where 1=1 ");
			if (-1 == plantId) {
				sql.whereEquals("b.process_id", processId);
				sql.whereEquals("a.parts_id", partsId);
				
			}else {
				sql.whereEquals("b.process_id", processId);
				sql.whereEquals("a.parts_id", partsId);
				sql.whereEquals("b.plant_id", plantId);
			}
			Page<JyReport> reportPage = new JyReport().paginate(page, limit, "select a.*",sql.toString());
			List<JyReport> reportList = reportPage.getList();
			for (int i = 0; i < reportList.size(); i++) {
				JyReport report = reportList.get(i);
				JyAcceptProcess process = new JyAcceptProcess().findFirst("select * from jy_accept_process where id = ?",report.getProcessId());
				report.put("process_name", process.getProcessName());
				JyAcceptParts parts = new JyAcceptParts().findFirst("select * from jy_accept_parts where id = ?",report.getPartsId());
				report.put("parts_name", parts.getPartsName());
				JyAcceptProduct product = new JyAcceptProduct().findFirst("select * from jy_accept_product where id = ?",report.getProductId());
				report.put("product_name", product.getProductName());
				JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",report.getOrderId());
				report.put("order_id", order.getOrderId());
				report.put("batch_id", order.getBatchId());
			}
			//计算合计
			SQLUtils sql2 = new SQLUtils("select a.* from jy_report a left join jy_process b on a.parent_process_id = b.process_id where 1=1 ");
			if (-1 == plantId) {
				sql2.whereEquals("b.process_id", processId);
				sql2.whereEquals("a.parts_id", partsId);
				
			}else {
				sql2.whereEquals("b.process_id", processId);
				sql2.whereEquals("a.parts_id", partsId);
				sql2.whereEquals("b.plant_id", plantId);
			}
			List<JyReport> reportListAdd = new JyReport().find(sql2.toString());
			int requireNumberAdd = 0;
			int scrapNumberAdd = 0;
			int delectNumberAdd = 0;
			if(reportListAdd.size()>0) {
				for (int i = 0; i < reportListAdd.size(); i++) {
					JyReport report = reportListAdd.get(i);
					requireNumberAdd += report.getRequireNumber();
					scrapNumberAdd += report.getScrapNumber();
					delectNumberAdd += report.getDelectNumber();
				}
			}
			JyReport report = new JyReport();
			report.setRequireNumber(requireNumberAdd);
			report.setScrapNumber(scrapNumberAdd);
			report.setDelectNumber(delectNumberAdd);
			report.put("product_name", "合计");
			reportList.add(report);
			return reportPage;
		}
}
