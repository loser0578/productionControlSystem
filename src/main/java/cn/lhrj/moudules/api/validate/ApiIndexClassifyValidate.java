package cn.lhrj.moudules.api.validate;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class ApiIndexClassifyValidate extends Validator{

	protected void validate(Controller c) {

		setShortCircuit(true);
		validateRequired("page", "msg", "page不能为空");
		validateRequired("limit", "msg", "limit不能为空");

	}
	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.renderJson();
	}
   

}
