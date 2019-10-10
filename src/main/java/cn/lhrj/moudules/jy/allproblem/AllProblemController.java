package cn.lhrj.moudules.jy.allproblem;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyAcceptProcess;
import cn.lhrj.common.model.JyAcceptProduct;
import cn.lhrj.common.model.JyOrder;
import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class AllProblemController extends BaseProjectController{
	
	public void index() {
		render("/page/jy/allproblem.html");
	}
	
	public void problemList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_order where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyOrder> orders = new JyOrder().find(sql.toString());
		//去重之后的负责人名字数组
		List<String> principalList = new ArrayList<String>();
		for (int i = 0; i < orders.size(); i++) {
			String principal = orders.get(i).getPrincipal();
			if(!(principalList.contains(principal))) {
				principalList.add(principal);
			}
		}
		List<JyProduct> jyProductList = new ArrayList<JyProduct>();
		for (int i = 0; i < principalList.size(); i++) {
			String principal = principalList.get(i);
			//所有生产过的产品
			List<JyAcceptProduct> allAcceptProducts = new JyAcceptProduct().find("select * from jy_accept_product where state = 1 and principal = ?",principal);
			//未去重产品id
			List<Integer> acceptProductIdContant = new ArrayList<Integer>();
			for (int j = 0; j < allAcceptProducts.size(); j++) {
				acceptProductIdContant.add(allAcceptProducts.get(j).getProductId());
			}
			//去重之后的产品id数组
			List<Integer> productIdList = new ArrayList<Integer>();
			for (int j = 0; j < acceptProductIdContant.size(); j++) {
				int productId = acceptProductIdContant.get(j);
				if(!(productIdList.contains(productId))) {
					productIdList.add(productId);
				}
			}
			for (int j = 0; j < productIdList.size(); j++) {
				int productId = productIdList.get(j);
				JyProduct product = new JyProduct().findFirst("select product_id,product_name,img,parts_id from jy_product where product_id = ?",productId);
				product.put("principal", principal);
				//这里开始计算部件
				List<JSONObject> partsList = new ArrayList<JSONObject>();
				String partsString = product.getPartsId();
				JSONArray partsIdArray = JSONArray.parseArray(partsString);
				for (int k = 0; k < partsIdArray.size(); k++) {
					int partsId = partsIdArray.getIntValue(k);
					JyParts parts = new JyParts().findFirst("select * from jy_parts where parts_id = ?",partsId);
					JSONObject object = new JSONObject();
					object.put("parts_name", parts.getPartsName());
					//所有工序的id
					List<Integer> processIdList = new ArrayList<Integer>();
					//先遍历所有工序
					List<JyProcess> processList = new JyProcess().find("select * from jy_process where state = 1");
					for (int l = 0; l < processList.size(); l++) {
						processIdList.add(processList.get(l).getProcessId());
					}
					//全部工序故障总数
					int addScrapNumber = 0;
					//查找这个部件的工序
					for (int l = 0; l < processIdList.size(); l++) {
						int processId = processIdList.get(l);
						List<JyAcceptProcess> acceptProcessList = new JyAcceptProcess().find("select * from jy_accept_process where process_id = ? and parts_id = ? and product_id = ? and state = 1 and principal = ?",processId,partsId,productId,principal);
						if(acceptProcessList.size()==0) {
							object.put(""+processId, 0);
						}else {
							int processAddNumber = 0;
							for (int m = 0; m < acceptProcessList.size(); m++) {
								processAddNumber += acceptProcessList.get(m).getScrapNumber();
								addScrapNumber += processAddNumber;
							}
							object.put(""+processId, processAddNumber);
						}
					}
					object.put("addScrapNumber", addScrapNumber);
					partsList.add(object);
				}
				product.put("partsList", partsList);
				jyProductList.add(product);
			}
		}
		System.out.println(jyProductList);
		renderJson(Ret.ok("list", jyProductList));
	}
	
	public void getAllProblemColumns() {
		int plantId = getLoginAccount().getPlantId();
		List<JSONObject> list = new ArrayList<JSONObject>();
		JSONObject object = new JSONObject();
		object.put("title", "部件名称");
		object.put("key", "parts_name");
		list.add(object);
		JSONObject object2 = new JSONObject();
		object2.put("title", "总数统计");
		object2.put("key", "addScrapNumber");
		list.add(object2);
		List<JyProcess> processList = new JyProcess().find("select * from jy_process where state = 1 and plant_id = ? order by sort ASC",plantId);
		for (int i = 0; i < processList.size(); i++) {
			JSONObject objectProcess = new JSONObject();
			objectProcess.put("title", processList.get(i).getProcessName()+"(质量问题数量)");
			objectProcess.put("key",processList.get(i).getProcessId());
			list.add(objectProcess);
		}
		renderJson(Ret.ok("list", list));
	}
}
