package cn.lhrj.moudules.admin;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
/**
 * @author 行动家!
 *
 *     乐活软件
 */
public class AdminValidator extends Validator{

	@Override
	protected void validate(Controller c) {
		setShortCircuit(true);

		validateRequired("username", "userNameMsg", "账号不能为空");
		validateRequired("password", "passwordMsg", "密码不能为空");
		
	}

	@Override
	protected void handleError(Controller c) {
		c.renderJson();	
	}

}
