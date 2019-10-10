package cn.lhrj.moudules.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Ret;
import com.lhrj.common.interceptor.LoginSessionInterceptor;

import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.ImageCode;
import cn.lhrj.common.utils.StrUtils;
import cn.lhrj.component.base.BaseProjectController;

@Clear(LoginSessionInterceptor.class)
public class AdminController extends BaseProjectController{

	AdminService srv=AdminService.me;
		public void index() {
			render("login.html");
	}
		
	public void initCache() {
		AdminService.initLoginAccount();
		renderJson(1);
	}	
		
		
	/**
	 * 登录
	 * @throws Exception 
	 */
	@Before(AdminValidator.class)
	@Clear
	public void login() throws Exception {
		String imageCode = getSessionAttr(ImageCode.class.getName());
		String checkCode = this.getPara("captcha");

		if (StrUtils.isEmpty(imageCode) || !imageCode.equalsIgnoreCase(checkCode)) {
			setAttr("msg", "验证码错误！");
			renderJson(Ret.fail());
			return;
		}
		System.out.println("para:"+getPara("username"));
		Ret ret = srv.login(getPara("username"), getPara("password"));
		if (ret.isOk()) {
			String sessionId = ret.getStr(AdminService.sessionIdName);
			int maxAgeInSeconds = ret.getInt("maxAgeInSeconds");
			setCookie(AdminService.sessionIdName, sessionId, maxAgeInSeconds, true);
			ret.set("returnUrl", getPara("returnUrl", "/"));    // 如果 returnUrl 存在则跳过去，否则跳去首页
		}
		renderJson(ret);
	}

	public void logout() {
		String sessionId = getCookie(AdminService.sessionIdName);
		srv.logout(sessionId);
		redirect("/login");
	}
	
	public void ResValidata() {
		String loginid=getPara("loginid");
		List<SysUser> sysUser=new SysUser().find("select iid from sys_user where loginid=?",loginid);
		if (sysUser.size()>0) {		
			renderJson(Ret.fail());
		}else {
			renderJson(Ret.ok());
		}
	}

	
	
	public void image_code() {
		try {
			new ImageCode().doGet(getRequest(), getResponse());
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		renderNull();
	}

}
