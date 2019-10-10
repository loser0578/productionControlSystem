package cn.lhrj.moudules.api.model;

import com.jfinal.kit.Ret;
import com.jfinal.log.Log;

import cn.lhrj.component.base.BaseProjectController;



public class ApiBaseAction extends BaseProjectController{
	
	private final static Log log = Log.getLog(ApiBaseAction.class);
	
	
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
 
        return Ret.fail("msg",msg).set("code", requestCode);
    }

    public static Ret toResponsSuccess(Object data) {
    
        log.info("response:" + data);
        return Ret.ok("data",data).set("code", 1);
    }	
}
