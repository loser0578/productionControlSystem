package cn.lhrj.moudules.jy.parts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.jfinal.json.FastJson;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyParts;
import cn.lhrj.common.model.JyProcess;
import cn.lhrj.common.model.JyProduct;
import cn.lhrj.common.utils.SQLUtils;

public class PartsService {

	public static final PartsService me = new PartsService();
	
	public Page<JyParts> getPage(int page,int limit,int plantId){
		SQLUtils sql = new SQLUtils("from jy_parts where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		Page<JyParts> partsPageList = new JyParts().paginate(page, limit, "select * ",sql.toString());
		List<JyParts> partsList = partsPageList.getList();
		for (int i = 0; i < partsList.size(); i++) {
			List<String> processNameList = new ArrayList<String>();
			JyParts parts= partsList.get(i);
			String processString = parts.getProcessId();
			JSONArray processArray = JSONArray.parseArray(processString);
			for (int j = 0; j < processArray.size(); j++) {
				JyProcess process = new JyProcess().findFirst("select process_id,process_name from jy_process where process_id = ?",processArray.get(j));
				processNameList.add(process.getProcessName());
			}
			parts.put("processNameList", processNameList);
		}
		return partsPageList;
	}
	
	public List<JyParts> getPartsByProductId(int id){
		JyProduct product = new JyProduct().findFirst("select * from jy_product where product_id = ? order by sort ASC",id);
		String partsIdString = product.getPartsId();
		JSONArray partsArray = JSONArray.parseArray(partsIdString);
		List<JyParts> partsList = new ArrayList<JyParts>();
		for (int i = 0; i < partsArray.size(); i++) {
			JyParts parts = new JyParts().findFirst("select parts_id,parts_name,process_id from jy_parts where parts_id = ?",partsArray.get(i));
			parts.put("parts_number", 0);
			parts.put("predict_start", null);
			parts.put("predict_end", null);
			//拿到工序list
			String processString = parts.getProcessId();
			JSONArray processArray = JSONArray.parseArray(processString);
			List<JyProcess> processList = new ArrayList<JyProcess>();
			for (int j = 0; j < processArray.size(); j++) {
				JyProcess process = new JyProcess().findFirst("select process_id,process_name from jy_process where process_id = ?",processArray.get(j));
				processList.add(process);
			}
			parts.put("process",processList);
			partsList.add(parts);
		}
		return partsList;
	}
	
	public void save(String string,int plantId) {
		System.out.println("xxxxxxxxxxx"+string);
		JyParts parts=FastJson.getJson().parse(string, JyParts.class);
		Date date = new Date();
		parts.setCreateTime(date);
		parts.setUpdateTime(date);
		parts.setPlantId(plantId);
		parts.setState(1);
		parts.save();
	}
	
	public void update(String string) {
		JyParts parts=FastJson.getJson().parse(string, JyParts.class);
		Date date = new Date();
		parts.setUpdateTime(date);
		parts.setState(1);
		parts.update();
	}
}
