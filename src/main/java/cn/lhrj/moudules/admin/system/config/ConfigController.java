/**
 * 
 */
package cn.lhrj.moudules.admin.system.config;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;

import cn.lhrj.common.model.SysConfig;
import cn.lhrj.common.utils.SQLUtils;
import cn.lhrj.component.base.BaseProjectController;


/**
 * @author 行动家!
 *
 *     乐活软件
 */
public class ConfigController extends BaseProjectController{
	
	ConfigService srv= ConfigService.me;
	
	
	public void list() {
		SysConfig model = getModel(SysConfig.class,"",true);
		int operType = getParaToInt("oper_type", 1);
		SQLUtils sql = new SQLUtils("  from sys_config t " + 
				"left join sys_config t2 on t.type = t2.id where 1=1  ");
		if (model._getAttrValues().length != 0) {
			sql.setAlias("t");
			// 查询条件
			sql.whereLike("rule_type", model.getStr("rule_type"));
		}	
		if (operType == 1) {
			sql.append(" and t.type != 0 ");
		} else {
			sql.append(" and t.type = 0 ");
		}
		// 排序
	    sql.append(" order by t.id ");
		Page<SysConfig> page = SysConfig.dao.paginate(getParaToInt("page"),getParaToInt("limit"), "select t.*,t2.name as typeName", //
				sql.toString().toString());
		renderJson(Ret.ok("page",page ));
		// 下拉框
	}
	public void create() {
		SysConfig model = getModel(SysConfig.class,"",true);
		//model.setCreateId(getLoginAccount().getId());
		model.setCreateTime(getNow());
		model.save();
		ConfigCache.init();
		renderJson(model);
	}
	public void update() {
		SysConfig model = getModel(SysConfig.class,"",true);
		model.setUpdateTime(getNow());
		//model.setUpdateId(getLoginAccount().getId());
		model.update();
		ConfigCache.init();
		renderJson(model);
	}
	public void gettype() {
		renderJson(Ret.ok("list",srv.getMenu()));
	}
	
	public void delete() {
		SysConfig model = getModel(SysConfig.class,"",true);
		model.deleteById(model.getId());

		// 更新缓存
		ConfigCache.update();

		renderJson(Ret.ok());
	}
}
