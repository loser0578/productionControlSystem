package cn.lhrj.moudules.api.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.hutool.core.util.ArrayUtil;
import cn.lhrj.common.model.TbToken;
import cn.lhrj.modules.api.annotation.HttpEnum;
import cn.lhrj.modules.api.annotation.IgnoreAuth;
import cn.lhrj.modules.api.annotation.NotNullValidate;
import cn.lhrj.moudules.api.constant.ApiConstant;
import cn.lhrj.moudules.api.service.TokenService;
import cn.lhrj.moudules.api.util.ApiBaseAction;

public class ApiInterceptor implements Interceptor{
	@Inject
	private TokenService tokenService;
	public static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";
    public static final String LOGIN_TOKEN_KEY = "X-shop-Token";
    private static Log log=Log.getLog(ApiInterceptor.class);
	
    @Override
	public void intercept(Invocation inv) {
		Controller controller = inv.getController();
		
		//支持跨域请求
		controller.getResponse().setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		controller.getResponse().setHeader("Access-Control-Max-Age", "3600");
		controller.getResponse().setHeader("Access-Control-Allow-Credentials", "true");
		controller.getResponse().setHeader("Access-Control-Allow-Headers", "x-requested-with,X-Nideshop-Token,X-URL-PATH");
		controller.getResponse().setHeader("Access-Control-Allow-Origin", controller.getHeader("Origin"));		
       
		
        //TODO 权限功能后台拦截待实现
        //Permissions permissions=invocation.getMethod().getAnnotation(Permissions.class);
        NotNullValidate[] validates = inv.getMethod().getAnnotationsByType(NotNullValidate.class);
        if (ArrayUtil.isNotEmpty(validates)) {
            if (HttpEnum.PARA.equals(validates[0].type())) {
                for (NotNullValidate validate : validates) {
                    if (controller.getPara(validate.value()) == null) {
                    	log.info(controller.getRequest().getRequestURI().toString()+validate.message().toString());
                        controller.renderJson(ApiBaseAction.toResponsFail(500, validate.message()));
                        return;
                    }
                }
            } else if (HttpEnum.JSON.equals(validates[0].type())) {
                JSONObject jsonObject = JSON.parseObject(controller.getRawData());
                for (NotNullValidate validate : validates) {
                    if (!jsonObject.containsKey(validate.value())||jsonObject.get(validate.value())==null) {
                    	log.info(controller.getRequest().getRequestURI().toString()+validate.message().toString());
                        controller.renderJson(ApiBaseAction.toResponsFail(500, validate.message()));
                        return;
                    }
                }
            }
        }
		
        IgnoreAuth annotation = inv.getMethod().getAnnotation(IgnoreAuth.class);
        //如果没有有@IgnoreAuth注解，则验证token
        if (annotation ==null ) {   		
    		//从header中获取token
            String token = controller.getHeader(LOGIN_TOKEN_KEY);
            //如果header中不存在token，则从参数中获取token
            if (StrKit.isBlank(token)) {
                token = controller.getRequest().getParameter(LOGIN_TOKEN_KEY);
            }
            
            //token为空
    		if (StrKit.isBlank(token)) {
    			controller.renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_LOGIN_VALID_ERROR, "请先登录"));
    			return;
    		}
    		
            //查询token信息		
    		TbToken tbToken=tokenService.queryByToken(token);
    		if (tbToken == null || tbToken.getExpireTime().getTime()<System.currentTimeMillis()) {
    			controller.renderJson(ApiBaseAction.toResponsFail(ApiConstant.CODE_TOKEN_VALID_ERROR, "token失效，请重新登录"));
    			return;
    		}
    		controller.setAttr(LOGIN_USER_KEY, tbToken.getUserId());
		}
		inv.invoke();	
	}

}
