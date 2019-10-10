package cn.lhrj.moudules.jy.problem;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyAcceptProcess;
import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.model.JyPrincipal;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class ProblemController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/problem.html");
	}
	
	public void selectList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_principal where state = 1 ");
		SQLUtils sql2 = new SQLUtils("select * from jy_product where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
			sql2.whereEquals("plant_id", plantId);
		}
		List<JyPrincipal> principalList = new JyPrincipal().find(sql.toString());
		List<JyProduct> productList = new JyProduct().find(sql2.toString());
		//用来装子集
		List<JSONObject> objectProductList = new ArrayList<JSONObject>();
		for (int i = 0; i < productList.size(); i++) {
			JyProduct product = productList.get(i);
			JSONObject object = new JSONObject();
			object.put("value", product.getProductId());
			object.put("label", product.getProductName());
			objectProductList.add(object);
		}
		//父级(负责人)
		List<JSONObject> objectList = new ArrayList<JSONObject>();
		for (int i = 0; i < principalList.size(); i++) {
			JyPrincipal principal = principalList.get(i);
			JSONObject object = new JSONObject();
			object.put("value", principal.getPrincipalName());
			object.put("label", principal.getPrincipalName());
			object.put("children", objectProductList);
			objectList.add(object);
		}
		renderJson(Ret.ok("selectList", objectList));
	}
	
	public void  getAllProblem() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_accept_process where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		//正常个数
		int AllNumber = 0;
		//质量问题个数
		int problemNumber = 0;
		List<JyAcceptProcess> processList = new JyAcceptProcess().find(sql.toString());
		for (int i = 0; i < processList.size(); i++) {
			JyAcceptProcess process = processList.get(i);
			AllNumber += process.getRequireNumber();
			problemNumber += process.getScrapNumber();
		}
		List<JSONObject> objectList = new ArrayList<JSONObject>();
		if(0 == AllNumber && 0 == problemNumber) {
			JSONObject object = new JSONObject();
			object.put("value", 100);
			object.put("name", "正常率");
			objectList.add(object);
			JSONObject object2 = new JSONObject();
			object2.put("value", 0);
			object2.put("name", "质量问题率");
			objectList.add(object2);
		}else {
			JSONObject object = new JSONObject();
			object.put("value", AllNumber);
			object.put("name", "正常率");
			objectList.add(object);
			JSONObject object2 = new JSONObject();
			object2.put("value", problemNumber);
			object2.put("name", "质量问题率");
			objectList.add(object2);
		}
		System.out.println(objectList);
		renderJson(Ret.ok("list", objectList));
	}
	
	public void getProblem() {
		try {
			String productString= HttpKit.readData(getRequest());
			JSONObject object = JSONObject.parseObject(productString);
			System.out.println(productString);
			JSONArray queryArray= object.getJSONArray("queryList");
			String principal = queryArray.getString(0);
			int productId = queryArray.getIntValue(1);
			String year = object.getString("year") == null ? "2019" :object.getString("year");
			
			int plantId = getAccountId();
			SQLUtils sql = new SQLUtils("select * from jy_accept_process where state = 1 ");
			if (-1 == plantId) {
				
			}else {
				sql.whereEquals("plant_id", plantId);
			}
			sql.whereEquals("principal", principal);
			sql.whereEquals("product_id", productId);
			sql.whereEquals("year(create_time)", year);
			List<JyAcceptProcess> acceptProcesses = new JyAcceptProcess().find(sql.toString());
			//返回的数组
			List<JSONObject> objectList = new ArrayList<JSONObject>();
			if(0 == acceptProcesses.size()) {
				JSONObject objectSuccess = new JSONObject();
				objectSuccess.put("value", 100);
				objectSuccess.put("name", "正常率");
				objectList.add(objectSuccess);
				JSONObject objectError = new JSONObject();
				objectError.put("value", 0);
				objectError.put("name", "质量问题率");
				objectList.add(objectError);
			}else {
				//正常个数
				int AllNumber = 0;
				//质量问题个数
				int problemNumber = 0;
				for (int i = 0; i < acceptProcesses.size(); i++) {
					JyAcceptProcess process = acceptProcesses.get(i);
					AllNumber += process.getRequireNumber();
					problemNumber += process.getScrapNumber();
				}
				if(0 == AllNumber && 0 == problemNumber) {
					JSONObject objectSuccess = new JSONObject();
					objectSuccess.put("value", 100);
					objectSuccess.put("name", "正常率");
					objectList.add(objectSuccess);
					JSONObject objectError = new JSONObject();
					objectError.put("value", 0);
					objectError.put("name", "质量问题率");
					objectList.add(objectError);
				}else {
					JSONObject objectSuccess = new JSONObject();
					objectSuccess.put("value", AllNumber);
					objectSuccess.put("name", "正常率");
					objectList.add(objectSuccess);
					JSONObject objectError = new JSONObject();
					objectError.put("value", problemNumber);
					objectError.put("name", "质量问题率");
					objectList.add(objectError);
				}
			}
			renderJson(Ret.ok("list", objectList));
		} catch (Exception e) {
			e.printStackTrace();
			getAllProblem();
		}
	}
}
