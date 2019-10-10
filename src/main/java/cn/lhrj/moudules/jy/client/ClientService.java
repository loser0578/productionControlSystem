package cn.lhrj.moudules.jy.client;

import com.jfinal.json.FastJson;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.JyClient;
import cn.lhrj.common.utils.SQLUtils;

public class ClientService {
	
	public static final ClientService me = new ClientService();
	
	public Page<JyClient> getPage(int page,int limit,int plantId) {
		SQLUtils sql = new SQLUtils("from jy_client where state = 1 ");
		if (-1 == plantId) {
			sql.append(" order by sort ASC");
		}else {
			sql.whereEquals("plant_id", plantId);
			sql.append(" order by sort ASC");
		}
		Page<JyClient> clientPageList = new JyClient().paginate(page, limit, "select * ",sql.toString());
		return clientPageList;
	}
	
	public void save(String string,int plantId) {
		JyClient client=FastJson.getJson().parse(string, JyClient.class);
		client.setState(1);
		client.setPlantId(plantId);
		client.save();
	}
	
	public void update(String string,int plantId) {
		JyClient client=FastJson.getJson().parse(string, JyClient.class);
		client.setState(1);
		client.setPlantId(plantId);
		client.update();
	}
}
