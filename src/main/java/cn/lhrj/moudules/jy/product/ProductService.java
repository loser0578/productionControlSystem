package cn.lhrj.moudules.jy.product;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.utils.SQLUtils;

public class ProductService {
	
	public static final ProductService me = new ProductService();
	
	public Page<JyProduct> getPage(int page,int limit,int plantId){
		SQLUtils sql = new SQLUtils("from jy_product where state = 1");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		Page<JyProduct> productPage = new JyProduct().paginate(page, limit, "select * ",sql.toString());
		List<JyProduct> productList = productPage.getList();
		for (int i = 0; i < productList.size(); i++) {
			List<String> partsNameList = new ArrayList<String>();
			JyProduct product = productList.get(i);
			String partsString = product.getPartsId();
			JSONArray partsArray = JSONArray.parseArray(partsString);
			for (int j = 0; j < partsArray.size(); j++) {
				SQLUtils sql2 = new SQLUtils("select parts_id,parts_name from jy_parts where 1=1");
				if (-1 == plantId) {
					sql2.whereEquals("parts_id", partsArray.getIntValue(j));
					
				}else {
					sql2.whereEquals("parts_id", partsArray.getIntValue(j));
					sql2.whereEquals("plant_id", plantId);
					
				}
				JyParts parts = new JyParts().findFirst(sql2.toString());
				partsNameList.add(parts.getPartsName());
			}
			product.put("partsNameList", partsNameList);
		}
		return productPage;
	}
}
