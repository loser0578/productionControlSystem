package cn.lhrj.moudules.jy.production;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyAcceptProcess;
import cn.lhrj.common.model.JyAcceptProduct;
import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class ProductionController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/production.html");
	}
	
	public void productList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_accept_product where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		//所有生产过的产品
		List<JyAcceptProduct> allAcceptProducts = new JyAcceptProduct().find(sql.toString());
		//未去重产品id
		List<Integer> acceptProductIdContant = new ArrayList<Integer>();
		for (int i = 0; i < allAcceptProducts.size(); i++) {
			acceptProductIdContant.add(allAcceptProducts.get(i).getProductId());
		}
		//去重之后的产品id数组
		List<Integer> productIdList = new ArrayList<Integer>();
		for (int i = 0; i < acceptProductIdContant.size(); i++) {
			int productId = acceptProductIdContant.get(i);
			if(!(productIdList.contains(productId))) {
				productIdList.add(productId);
			}
		}
		List<JyProduct> productList = new ArrayList<JyProduct>();
		for (int i = 0; i < productIdList.size(); i++) {
			int productId = productIdList.get(i);
			SQLUtils sql2 = new SQLUtils("select product_id,img,product_name,parts_id from jy_product where 1=1 ");
			if (-1 == plantId) {
				sql2.whereEquals("product_id", productId);
			}else {
				sql2.whereEquals("product_id", productId);
				sql2.whereEquals("plant_id", plantId);
			}
			JyProduct product = new JyProduct().findFirst(sql2.toString());
			//单个产品生产数量总和
			int productAddNumber = 0;
			SQLUtils sql3 = new SQLUtils("select * from jy_accept_product where state = 1 ");
			if (-1 == plantId) {
				sql3.whereEquals("product_id", productId);
			}else {
				sql3.whereEquals("product_id", productId);
				sql3.whereEquals("plant_id", plantId);
			}
			List<JyAcceptProduct> acceptProductList = new JyAcceptProduct().find(sql3.toString());
			for (int j = 0; j < acceptProductList.size(); j++) {
				productAddNumber += acceptProductList.get(j).getProductNumber();
			}
			product.put("productAddNumber", productAddNumber);
			//这里开始添加子集合
			List<JSONObject> partsList = new ArrayList<JSONObject>();
			String partsString = product.getPartsId();
			JSONArray partsIdArray = JSONArray.parseArray(partsString);
			for (int j = 0; j < partsIdArray.size(); j++) {
				int partsId = partsIdArray.getIntValue(j);
				SQLUtils sql4 = new SQLUtils("select * from jy_parts where 1=1 ");
				if (-1 == plantId) {
					sql4.whereEquals("parts_id", partsId);
				}else {
					sql4.whereEquals("parts_id", partsId);
					sql4.whereEquals("plant_id", plantId);
				}
				JyParts parts = new JyParts().findFirst(sql4.toString());
				JSONObject object = new JSONObject();
				object.put("parts_name", parts.getPartsName());
				//所有工序的id
				List<Integer> processIdList = new ArrayList<Integer>();
				//先遍历所有工序
				SQLUtils sql5 = new SQLUtils("select * from jy_process where state = 1 ");
				if (-1 == plantId) {
					
				}else {
					sql5.whereEquals("plant_id", plantId);
				}
				List<JyProcess> processList = new JyProcess().find(sql5.toString());
				for (int k = 0; k < processList.size(); k++) {
					processIdList.add(processList.get(k).getProcessId());
				}
				//查找这个部件的工序
				for (int k = 0; k < processIdList.size(); k++) {
					int processId = processIdList.get(k);
					SQLUtils sql6 = new SQLUtils("select * from jy_accept_process where 1=1 and state = 1 ");
					if (-1 == plantId) {
						sql6.whereEquals("process_id", processId);
						sql6.whereEquals("parts_id", partsId);
					}else {
						sql6.whereEquals("process_id", processId);
						sql6.whereEquals("parts_id", partsId);
						sql6.whereEquals("plant_id", plantId);
					}
					List<JyAcceptProcess> acceptProcessList = new JyAcceptProcess().find(sql6.toString());
					if(acceptProcessList.size()==0) {
						object.put(""+processId, "无需生产");
					}else {
						int processAddNumber = 0;
						for (int l = 0; l < acceptProcessList.size(); l++) {
							processAddNumber += acceptProcessList.get(l).getRequireNumber();
						}
						object.put(""+processId, processAddNumber);
					}
				}
				partsList.add(object);
			}
			product.put("partsList", partsList);
			productList.add(product);
		}
		renderJson(Ret.ok("list", productList));
	}
	
	public void getProcessColumns() {
		int plantId = getAccountId();
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject object = new JSONObject();
		object.put("title", "部件名称");
		object.put("key", "parts_name");
		list.add(object);
		SQLUtils sql = new SQLUtils("select * from jy_process where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		List<JyProcess> processList = new JyProcess().find(sql.toString());
		for (int i = 0; i < processList.size(); i++) {
			JSONObject objectProcess = new JSONObject();
			objectProcess.put("title", processList.get(i).getProcessName()+"(生产总数)");
			objectProcess.put("key",processList.get(i).getProcessId());
			list.add(objectProcess);
		}
		renderJson(Ret.ok("list", list));
	}
}
