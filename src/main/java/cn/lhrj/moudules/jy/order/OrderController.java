package cn.lhrj.moudules.jy.order;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import cn.lhrj.common.model.JyAcceptParts;
import cn.lhrj.common.model.JyAcceptProcess;
import cn.lhrj.common.model.JyAcceptProduct;
import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyPrincipal;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.model.JyRepertory;
import cn.lhrj.common.model.JyReport;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class OrderController extends BaseProjectController{
	
	OrderService os = OrderService.me;
	
	public void index() {
		render("/page/jy/order.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyOrder> records=os.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void principalList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_principal where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyPrincipal> records = new JyPrincipal().find(sql.toString());
		renderJson(Ret.ok("list", records));
	}
	
	public void update() {
		os.update(HttpKit.readData(getRequest()));
		renderJson(Ret.ok());
	}
	
	public void updateOutShop() {
		int id  = getParaToInt("id");
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ? ",id);
		order.setIsOutshop(1);
		order.setOutTime(new Date());
		order.update();
		renderJson(Ret.ok());
	}
	
	public void updateShipMent() {
		int id  = getParaToInt("id");
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",id);
		order.setIsShipment(1);
		order.update();
		String acceptProductString = order.getAcceptProduct();
		JSONArray productArray = JSONArray.parseArray(acceptProductString);
		for (int i = 0; i < productArray.size(); i++) {
			int productId = productArray.getIntValue(i);
			JyAcceptProduct acceptProduct = new JyAcceptProduct().findFirst("select * from jy_accept_product where id = ?",productId);
			JyRepertory repertory = new JyRepertory().findFirst("select * from jy_repertory where product_id = ?",acceptProduct.getProductId());
			int repertoyAddNumber = acceptProduct.getProductNumber();
			int repertoyOldNumber = repertory.getRepertoy();
			repertory.setRepertoy(repertoyAddNumber + repertoyOldNumber);
			int residueAddNumber = acceptProduct.getProductNumber();
			int residueOldNumber = repertory.getResidue();
			repertory.setResidue(residueAddNumber+residueOldNumber);
			repertory.update();
		}
		renderJson(Ret.ok());
	}
	
	public void save() {
		int plantId = getLoginAccount().getPlantId();
		//接收前端的数据 转object
		String orderString= HttpKit.readData(getRequest());
		System.out.println("aaaaa"+orderString);
		JSONObject orderObject = JSONObject.parseObject(orderString);
		//存入数据库的订单数据
		JyOrder order = new JyOrder();
		//订单数据中的产品id数组
		List<Integer> productList = new ArrayList<Integer>();
		order.setOrderTime(orderObject.getDate("orderDate"));
		order.setClient(orderObject.getString("client"));
		order.setBatchId(orderObject.getString("batchNumber"));
		order.setOrderId(orderObject.getString("orderNumber"));
		order.setPlantId(plantId);
		String principal = orderObject.getString("principal");
		order.setPrincipal(principal);
		order.setDeliveryTime(orderObject.getDate("deliveryDate"));
		order.setLevel(orderObject.getInteger("level"));
		order.setState(1);
		order.save();
		String productsString = orderObject.getString("products");
		JSONArray productsArray = JSONArray.parseArray(productsString);
		//每个产品
		for (int i = 0; i < productsArray.size(); i++) {
			//部件id数组
			List<Integer> partsList = new ArrayList<Integer>();
			JSONObject productObject = JSONObject.parseObject(productsArray.getString(i));
			if(1 == productObject.getIntValue("status")) {
				int productId = productObject.getIntValue("product");
				JyProduct product = new JyProduct().findFirst("select product_id,product_name,img,parts_number from jy_product where product_id = ?",productId);
				String partsNumberString = product.getPartsNumber();
				//存入数据库的订单产品数据
				JyAcceptProduct acceptProduct = new JyAcceptProduct();
				acceptProduct.setAcceptPartsNumber(partsNumberString);
				acceptProduct.setProductId(productId);
				acceptProduct.setProductName(product.getProductName());
				int productNumber = productObject.getIntValue("number");
				System.out.println("产品数量是"+productNumber);
				acceptProduct.setProductNumber(productNumber);
				acceptProduct.setRemark(productObject.getString("remark"));
				acceptProduct.setPrincipal(principal);
				acceptProduct.setImg(product.getImg());
				acceptProduct.setPlantId(plantId);
				acceptProduct.setPredictStart(productObject.getDate("predict_start"));
				acceptProduct.setPredictEnd(productObject.getDate("predict_end"));
				acceptProduct.save();
				//订单的部件循环
				JSONArray partsArray = JSONArray.parseArray(productObject.getString("parts"));
				for (int j = 0; j < partsArray.size(); j++) {
					//工序id数组
					List<Integer> processList = new ArrayList<Integer>();
					JSONObject partsObject = JSONObject.parseObject(partsArray.getString(j)); 
					//这是部件系数
					JSONArray numberArray = JSONArray.parseArray(partsNumberString);
					int partsId = partsObject.getIntValue("parts_id");
					System.out.println("部件id是"+partsId);
					int parts_number = numberArray.getIntValue(j);
					System.out.println("部件系数是"+parts_number);
					JyParts parts = new JyParts().findFirst("select parts_id,parts_name,img from jy_parts where parts_id = ?",partsId);
					//存入数据库的订单部件数据
					JyAcceptParts acceptParts = new JyAcceptParts();
					acceptParts.setPartsId(partsId);
					acceptParts.setPartsNumber(parts_number*productNumber);
					acceptParts.setPartsName(parts.getPartsName());
					//acceptParts.setPartsNumber(partsObject.getIntValue("parts_number"));
					acceptParts.setProductId(acceptProduct.getProductId());
					acceptParts.setPrincipal(principal);
					acceptParts.setImg(parts.getImg());
					acceptParts.setPlantId(plantId);
					acceptParts.save();
					JSONArray processArray = partsObject.getJSONArray("process");
					for (int k = 0; k < processArray.size(); k++) {
						JSONObject processObject = JSONObject.parseObject(processArray.getString(k));
						//存入数据库的订单工序数据
						JyAcceptProcess acceptProcess = new JyAcceptProcess();
						acceptProcess.setProcessId(processObject.getIntValue("process_id"));
						acceptProcess.setProcessName(processObject.getString("process_name"));
						acceptProcess.setParentOrderId(order.getId());
						acceptProcess.setParentOrderId(order.getId());
						acceptProcess.setParentPartsId(acceptParts.getId());
						acceptProcess.setParentProductId(acceptProduct.getId());
						acceptProcess.setPartsId(acceptParts.getPartsId());
						acceptProcess.setProductId(acceptProduct.getProductId());
						acceptProcess.setPrincipal(principal);
						acceptProcess.setPlantId(plantId);
						acceptProcess.setCreateTime(new Date());
						acceptProcess.save();
						processList.add(acceptProcess.getId());
					}
					acceptParts.setAcceptProcess(processList.toString());
					acceptParts.setParentProductId(acceptProduct.getId());
					acceptParts.setParentOrderId(order.getId());
					acceptParts.update();
					partsList.add(acceptParts.getId());
				}
				acceptProduct.setAcceptParts(partsList.toString());
				acceptProduct.setParentOrderId(order.getId());
				acceptProduct.update();
				productList.add(acceptProduct.getId());
			}
		}
		order.setAcceptProduct(productList.toString());
		order.update();
		renderJson(Ret.ok());
	}
	
	public void acProductList() {
		int plantId = getAccountId();
		Page<JyAcceptProduct> records=os.getAcProductPage(getParaToInt("page"),getParaToInt("limit"),getParaToInt("orderId"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void readPartsColumns() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_process where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyProcess> processList = new JyProcess().find(sql.toString());
		List<JSONObject> objectList = new ArrayList<JSONObject>();
		for (int i = 0; i < processList.size(); i++) {
			JSONObject object = new JSONObject();
			object.put("title", processList.get(i).getProcessName());
			object.put("key","process_id"+processList.get(i).getProcessId());
			objectList.add(object);
		}
		renderJson(Ret.ok("columnsList", objectList));
	}
	
	public void acPartsList() {
		int plantId = getAccountId();
		List<JyAcceptParts> records = os.getAcParts(getParaToInt("productId"),plantId);
		renderJson(Ret.ok("list", records));
	}
	
	public void acProcessList() {
		int plantId = getAccountId();
		List<JyAcceptProcess> records = os.getAcProcess(getParaToInt("partsId"),plantId);
		renderJson(Ret.ok("list", records));
	}
	
	public void saveReport() {
		int plantId = getLoginAccount().getPlantId();
		os.saveReport(HttpKit.readData(getRequest()),plantId);
		renderJson(Ret.ok());
	}
	
	public void getReportPage(){
		int plantId = getAccountId();
		Page<JyReport> records = os.getReportPage(getParaToInt("page"),getParaToInt("limit"),getParaToInt("partsId"),plantId);
		renderJson(Ret.ok("page", records));
	}
	
	public void delect() {
		int id  = getParaToInt("id");
		JyOrder order = new JyOrder().findFirst("select * from jy_order where id = ?",id);
		order.setState(0);
		List<JyAcceptProduct> acceptProducts = new JyAcceptProduct().find("select * from jy_accept_product where parent_order_id = ?",id);
		for (int i = 0; i < acceptProducts.size(); i++) {
			JyAcceptProduct acceptProduct = acceptProducts.get(i);
			acceptProduct.setState(0);
			acceptProduct.update();
		}
		List<JyAcceptParts> acceptParts = new JyAcceptParts().find("select * from jy_accept_parts where parent_order_id = ?",id);
		for (int i = 0; i < acceptParts.size(); i++) {
			JyAcceptParts acceptPart = acceptParts.get(i);
			acceptPart.setState(0);
			acceptPart.update();
		}
		List<JyAcceptProcess> acceptProcesses = new JyAcceptProcess().find("select * from jy_accept_process where parent_order_id = ?",id);
		for (int i = 0; i < acceptProcesses.size(); i++) {
			JyAcceptProcess acceptProcess = acceptProcesses.get(i);
			acceptProcess.setState(0);
			acceptProcess.update();
		}
		order.update();
		renderJson(Ret.ok());
	}
	
	public void query() {
		int plantId = getAccountId();
		String str = HttpKit.readData(getRequest());
		List<JyOrder> records=os.query(str,plantId);
		renderJson(Ret.ok("list", records));
	}
	
	public void getReportProcess() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_process where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyProcess> processList = new JyProcess().find(sql.toString());
		renderJson(Ret.ok("list", processList));
	}
	
	public void getReportQuery() {
		int plantId = getAccountId();
		if(null == getParaToInt("processId")) {
			Page<JyReport> records = os.getReportPage(getParaToInt("page"),getParaToInt("limit"),getParaToInt("partsId"),plantId);
			renderJson(Ret.ok("page", records));
		}else {
			Page<JyReport> records = os.getReportQuery(getParaToInt("page"),getParaToInt("limit"),getParaToInt("partsId"),getParaToInt("processId"),plantId);
			renderJson(Ret.ok("page", records));
		}
	}
}
