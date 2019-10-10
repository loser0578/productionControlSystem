package cn.lhrj.component.base;

import java.util.Date;

import com.jfinal.core.Controller;
import com.jfinal.log.Log;

import cn.lhrj.common.model.SysRole;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.model.SysUserRole;
import cn.lhrj.common.utils.DateUtils;
import cn.lhrj.common.utils.IpUtils;
import cn.lhrj.moudules.admin.AdminService;


/**
 * @author 行动家!
 *
 *     乐活软件
 */
public class BaseProjectController extends Controller{

	protected static final Log log = Log.getLog(BaseProjectController.class);
	/**
	 * 获取当前时间，保存创建时间使用
	 * 
	 * 2015年3月25日 下午3:48:02 flyfox 330627517@qq.com
	 * 
	 * @return
	 */
	protected String getNow() {
		return DateUtils.getNow(DateUtils.DEFAULT_REGEX_YYYY_MM_DD_HH_MIN_SS);
	}
	protected Date getNowDate() {
		return DateUtils.getNowDate();
	}
	
	
	public SysUser getLoginAccount() {
		String sessionId = getCookie(AdminService.sessionIdName);
		SysUser loginAccount = AdminService.me.getLoginAccountWithSessionId(sessionId);
		if (loginAccount == null) {
			String loginIp = IpUtils.getClientIP(getRequest());
			loginAccount = AdminService.me.loginWithSessionId(sessionId, loginIp);
		}
		if (loginAccount == null) {
			loginAccount = getAttr(AdminService.loginAccountCacheName);
			if (loginAccount != null) {
				throw new IllegalStateException("当前用户状态不允许登录，status = " + loginAccount.getStatus());
			}
		}
		
		return loginAccount;
	}
	
	public String getRoleKey() {
		String sessionId = getCookie(AdminService.sessionIdName);
		SysUser loginAccount = AdminService.me.getLoginAccountWithSessionId(sessionId);
		if (loginAccount == null) {
			String loginIp = IpUtils.getClientIP(getRequest());
			loginAccount = AdminService.me.loginWithSessionId(sessionId, loginIp);
		}
		if (loginAccount == null) {
			loginAccount = getAttr(AdminService.loginAccountCacheName);
			if (loginAccount != null) {
				throw new IllegalStateException("当前用户状态不允许登录，status = " + loginAccount.getStatus());
			}
		}
		SysUserRole role = new SysUserRole().findFirst("select * from sys_user_role where user_id = ?",loginAccount.getId());
		SysRole sysRole = new SysRole().findFirst("select * from sys_role where role_id = ?",role.getRoleId());
		String roleKey = sysRole.getRoleKey();
		return roleKey;
	}
	
	//判断要拼接的sql
	public int getAccountId() {
		String roleKey = getRoleKey();
		if("admin".equals(roleKey)) {
			return -1;
		}else {
			return getLoginAccount().getPlantId();
		}
	}
	
	public boolean isLogin() {
		return getLoginAccount() != null;
	}
	public boolean notLogin() {
		return !isLogin();
	}

}
