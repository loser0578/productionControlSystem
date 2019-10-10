package cn.lhrj.moudules.sys.menu;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.validate.Validator;

import cn.lhrj.common.utils.Constants.MenuType;
/**
 * @author 行动家!
 *
 *     乐活软件
 */
public class SysMenuValidator extends Validator{


	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);
		validateRequired("name", "msg", "菜单名称不能为空");
		validateRequired("parent_id", "msg", "上级菜单不能为空");
		validateRequired("type", "msg", "类型不能为空");
		validateRequired("url", "msg", "url不能为空");
		
		int type = c.getParaToInt("type");
		 if (type == MenuType.MENU.getValue()) {
	            if (StrKit.isBlank(c.getPara("url"))) {
	                	addError("msg", "菜单URL不能为空");
	            }
	        }
        //上级菜单类型
        int parentType = MenuType.CATALOG.getValue();
        Long parent_id = c.getParaToLong("parent_id");
        if (c.getParaToInt("type") != 0) {
        	Record parentMenu = new SysMenuService().queryObject(parent_id);
            parentType = parentMenu.getInt("type");
        }
      //目录、菜单
        if (type == MenuType.CATALOG.getValue() || type == MenuType.MENU.getValue()) {
            if (parentType != MenuType.CATALOG.getValue()) {
                addError("msg", "上级菜单只能为目录类型");
            }
		}
      //按钮
        if (type == MenuType.BUTTON.getValue()) {
        	if (parentType != MenuType.MENU.getValue()) {
        		addError("msg","上级菜单只能为菜单类型");
            }
		}
	}

	@Override
	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.renderJson();	
	}
}
