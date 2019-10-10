package cn.lhrj.moudules.jy.product;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.template.stat.ast.Return;

import cn.lhrj.common.model.JyClient;
import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyPrincipal;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.model.JyRepertory;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;

public class ProductController extends BaseProjectController{
	
	ProductService ps = ProductService.me;
	
	public void index() {
		render("/page/jy/product.html");
	}
	
	public void list() {
		int plantId = getAccountId();
		Page<JyProduct> productPage = ps.getPage(getParaToInt("page"),getParaToInt("limit"),plantId);
		renderJson(Ret.ok("page", productPage));
	}
	
	public void productList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_product where state = 1");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyProduct> productList = new JyProduct().find(sql.toString());
		for (int i = 0; i < productList.size(); i++) {
			String partsIdString = productList.get(i).getPartsId();
			//拥有部件的列表
			JSONArray partsArray = JSONArray.parseArray(partsIdString);
			//部件list
			List<JyParts> partsList = new ArrayList<JyParts>();
			for (int j = 0; j < partsArray.size(); j++) {
				//单个部件的数据
				JyParts parts = new JyParts().findFirst("select * from jy_parts where state = 1 and parts_id = ?",partsArray.get(j));
				//部件拥有的工序的id
				String processString = parts.getProcessId();
				JSONArray processArray = JSONArray.parseArray(processString);
				//工序list
				List<JyProcess> processList = new ArrayList<JyProcess>();
				for (int k = 0; k < processArray.size(); k++) {
					//部件拥有的工序
					JyProcess process = new JyProcess().findFirst("select * from jy_process where state = 1 and process_id = ?",processArray.get(k));
					processList.add(process);
				}
				parts.put("processList", processList);
				partsList.add(parts);
			}
			productList.get(i).put("partsList", partsList);
		}
		System.out.println("productList"+productList);
		renderJson(Ret.ok("list", productList));
	}
	
	public void clientList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_client where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyClient> clients = new JyClient().find(sql.toString());
		renderJson(Ret.ok("list", clients));
	}
	
	public void principalList() {
		int plantId = getAccountId();
		SQLUtils sql = new SQLUtils("select * from jy_principal where state = 1 ");
		if (-1 == plantId) {
			
		}else {
			sql.whereEquals("plant_id", plantId);
		}
		List<JyPrincipal> principals = new JyPrincipal().find(sql.toString());
		renderJson(Ret.ok("list", principals));
	}
	
	public void delect() {
		int productid = getParaToInt("id");
		JyProduct product = new JyProduct().findFirst("select * from jy_product where product_id = ?",productid);
		product.setState(0);
		boolean result = product.update();
		JyRepertory repertory = new JyRepertory().findFirst("select * from jy_repertory where state = 1 and product_id = ?",product.getProductId());
		repertory.setState(0);
		repertory.update();
		renderJson(Ret.ok("state", result));
	}
	
	public void watchDesc() {
		int plantId = getLoginAccount().getPlantId();
		int productid = getParaToInt("id");
		JyProduct product = new JyProduct().findFirst("select * from jy_product where product_id = ? and state = 1 and plant_id = ?",productid,plantId);
		String partsString = product.getPartsId();
		JSONArray partsArray = JSONArray.parseArray(partsString);
		//返回前端的部件列表
		List<JyParts> parstList = new ArrayList<JyParts>();
		for (int i = 0; i < partsArray.size(); i++) {
			int partsId = partsArray.getIntValue(i);
			JyParts parts = new JyParts().findFirst("select * from jy_parts where parts_id = ? and state = 1 and plant_id = ?",partsId,plantId);
			String	processString = parts.getProcessId();
			JSONArray processArray = JSONArray.parseArray(processString);
			for (int j = 0; j < processArray.size(); j++) {
				int processId = processArray.getIntValue(j);
				List<JyProcess> processList = new JyProcess().find("select * from jy_process where process_id = ? and state = 1 and plant_id = ?",processId,plantId);
				parts.put("processList", processList);
			}
			parstList.add(parts);
		}
		renderJson(Ret.ok("data", parstList));
	}
	
	public void save() {
		int plantId = getLoginAccount().getPlantId();
		String productString= HttpKit.readData(getRequest());
		JSONObject productObject = JSONObject.parseObject(productString);
		//存入数据库的对象
		JyProduct product = new JyProduct();
		product.setProductName(productObject.getString("product_name"));
		//product.setPartsId(productObject.getString("parts_id"));
		product.setSort(productObject.getIntValue("sort"));
		product.setImg(productObject.getString("img"));
		product.setPlantId(plantId);
		product.setState(1);
		JSONArray partsList = productObject.getJSONArray("partsList");
		//存放数量的list
		List<Integer> numberObjectList = new ArrayList<Integer>();
		//存放id的list
		List<Integer> idObjectList = new ArrayList<Integer>();
		for (int i = 0; i < partsList.size(); i++) {
			JSONObject partsObject = JSONObject.parseObject(partsList.getString(i));
			if(1 == partsObject.getIntValue("status")) {
				numberObjectList.add(partsObject.getIntValue("parts_number"));
				idObjectList.add(partsObject.getIntValue("parts_id"));
			}
		}
		product.setPartsNumber(numberObjectList.toString());
		product.setPartsId(idObjectList.toString());
		product.save();
		JyRepertory repertory = new JyRepertory();
		repertory.setProductId(product.getProductId());
		repertory.setProductName(product.getProductName());
		repertory.setPlantId(plantId);
		repertory.save();
		renderJson(Ret.ok());
	}
	
	public void update() {
		String productString= HttpKit.readData(getRequest());
		JSONObject productObject = JSONObject.parseObject(productString);
		//存入数据库的对象
		int productId = productObject.getIntValue("product_id");
		JyProduct product = new JyProduct().findFirst("select * from jy_product where product_id = ?",productId);
		product.setProductName(productObject.getString("product_name"));
		product.setPartsId(productObject.getString("parts_id"));
		product.setSort(productObject.getIntValue("sort"));
		product.setImg(productObject.getString("img"));
		product.setState(1);
		JSONArray partsList = productObject.getJSONArray("partsList");
		//存放数量的list
		List<Integer> numberObjectList = new ArrayList<Integer>();
		//存放id的list
		List<Integer> idObjectList = new ArrayList<Integer>();
		for (int i = 0; i < partsList.size(); i++) {
			JSONObject partsObject = JSONObject.parseObject(partsList.getString(i));
			if(1 == partsObject.getIntValue("status")) {
				numberObjectList.add(partsObject.getIntValue("parts_number"));
				idObjectList.add(partsObject.getIntValue("parts_id"));
			}
		}
		product.setPartsNumber(numberObjectList.toString());
		product.setPartsId(idObjectList.toString());
		product.update();
		JyRepertory repertory = new JyRepertory().findFirst("select * from jy_repertory where state = 1 and product_id = ?",product.getProductId());
		repertory.setProductName(product.getProductName());
		repertory.update();
		renderJson(Ret.ok());
	}
}
