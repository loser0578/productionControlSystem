package cn.lhrj.moudules.api.util;

import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import cn.lhrj.common.model.ShopUser;
import cn.lhrj.common.model.TbToken;
import cn.lhrj.component.base.BaseProjectController;



public class ApiBaseAction extends BaseProjectController{
	
	private final static Log log = Log.getLog(ApiBaseAction.class);
	public static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";
    public static final String LOGIN_TOKEN_KEY = "X-shop-Token";
    /**
     * @param requestCode
     * @param msg
     * @param data
     * @return Map<String,Object>
     * @throws
     * @Description:构建统一格式返回对象
     * @date 2016年9月2日
     * @author zhuliyun
     */
    public static Ret toResponsFail(int requestCode, String msg) {
    	log.info("error"+msg);
        return Ret.fail("msg",msg).set("code", requestCode);
    }

    public static Ret toResponsSuccess(Object data) {
    
    	log.info("data"+data);
        return Ret.ok("data",data).set("code", 1);
    }
    public static Ret toResponsSuccesswithcode(int code,Object data) {
        
    	log.info("data"+data);
        return Ret.ok("data",data).set("code", code);
    }  
    public  ShopUser getApiLoginAccount() {
   		//从header中获取token
        String token = getHeader(LOGIN_TOKEN_KEY);
        //如果header中不存在token，则从参数中获取token
        if (StrKit.isBlank(token)) {
            token =getRequest().getParameter(LOGIN_TOKEN_KEY);
        }
    	TbToken tbToken=new TbToken().findFirst("select * from tb_token where token=?",token);
		ShopUser user=new ShopUser().findById(tbToken.getUserId());       
        return user;
	}
}
