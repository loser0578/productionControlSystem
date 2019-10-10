package cn.lhrj.moudules.admin;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import cn.lhrj.common.model.Session;
import cn.lhrj.common.model.SysMenu;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.utils.Constants.MenuType;

/**
 * @author 行动家!
 *
 *     乐活软件
 */
public class AdminService {

	public static final AdminService me = new AdminService();
	private SysUser accountDao=new SysUser().dao();
	// 存放登录用户的 cacheName
	public static final String loginAccountCacheName = "loginAccount";
	// "jfinalId" 仅用于 cookie 名称，其它地方如 cache 中全部用的 "sessionId" 来做 key
	public static final String sessionIdName = "token";
	private final static String allAccountsCacheName = "allAccounts";
    
	//更新缓存
	public static void  initLoginAccount() {
		
		CacheKit.removeAll(loginAccountCacheName);
	}
	
	/**
	 * 登录成功返回 sessionId 与 loginAccount，否则返回一个 msg
	 * @throws Exception 
	 */
	public Ret login(String userName, String password) throws Exception {
		/*userName = userName.toLowerCase().trim();*/
		password = HashKit.md5(password.trim());
		SysUser loginAccount = new SysUser().findFirst("select * from sys_user where username = ? limit 1", userName);
		System.out.println("xxxxxxxxx"+userName);
		if (loginAccount == null) {
			return Ret.fail("msg", "用户名不正确");
		}
		// 未通过密码验证
		if (loginAccount.getPassword().equals(password) == false) {
			return Ret.fail("msg", "密码不正确");
		}	
		// 如果用户勾选保持登录，暂定过期时间为 3 年，否则为 120 分钟，单位为秒
		long liveSeconds =   120 * 60;
		// 传递给控制层的 cookie
		int maxAgeInSeconds = (int)liveSeconds;
		// expireAt 用于设置 session 的过期时间点，需要转换成毫秒
		long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
		// 保存登录 session 到数据库
		Session session = new Session();
		String sessionId = StrKit.getRandomUUID();
		session.setId(sessionId);
		session.setAccountId(loginAccount.getId());
		session.setExpireAt(expireAt);
		if ( ! session.save()) {
			return Ret.fail("msg", "保存 session 到数据库失败，请联系管理员");
		}
		loginAccount.removeSensitiveInfo();                                 // 移除 password 与 salt 属性值
		loginAccount.put("sessionId", sessionId);                          // 保存一份 sessionId 到 loginAccount 备用
        // 移除 password 与 salt 属性值
		String token=StrKit.getRandomUUID();
		loginAccount.put("token", token);                          // 保存一份 sessionId 到 loginAccount 备用
		CacheKit.put(loginAccountCacheName, sessionId, loginAccount);
		return Ret.ok(sessionIdName, sessionId)
				.set(loginAccountCacheName, loginAccount)
				.set("maxAgeInSeconds", maxAgeInSeconds);  			
	}

	
	
	
	/**
	 * 创建登录日志
	 */
 	private void createLoginLog(Long accountId, String loginIp) {
		Record loginLog = new Record().set("accountId", accountId).set("ip", loginIp).set("loginAt", new Date());
		Db.save("sys_log", loginLog);
	}

	
	public SysUser getLoginAccountWithSessionId(String sessionId) {
		return CacheKit.get(loginAccountCacheName, sessionId);
	}
	/**
	 * 通过 sessionId 获取登录用户信息
	 * sessoin表结构：session(id, accountId, expireAt)
	 *
	 * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取
	 * 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
	 *     如果没过期则先放缓存一份，然后再返回
	 */
	public SysUser loginWithSessionId(String sessionId, String loginIp) {

		Session session = Session.dao.findById(sessionId);
		if (session == null) {      // session 不存在
			return null;
		}
		if (session.isExpired()) {  // session 已过期
			session.delete();		// 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
			return null;
		}

		SysUser loginAccount = accountDao.findFirst("select * from sys_user where id=?",session.getAccountId());
		// 找到 loginAccount 并且 是正常状态 才允许登录
		if (loginAccount != null ) {
			loginAccount.removeSensitiveInfo();                                 // 移除 password 与 salt 属性值
			loginAccount.put("sessionId", sessionId);                          // 保存一份 sessionId 到 loginAccount 备用
			CacheKit.put(loginAccountCacheName, sessionId, loginAccount);

			createLoginLog(loginAccount.getId(), loginIp);
			return loginAccount;
		}
		return null;
	}

	//需要搞清楚在哪个地方去更新缓存
	
	
	
	
    /**
     * 优先从缓存中获取 account 对象，可获取到被 block 的 account
     */
    public SysUser getById(String accountId) {
        // 优先从缓存中取，未命中缓存则从数据库取
    	String key=("getByIdcountId_"+accountId);
    	SysUser account = CacheKit.get(allAccountsCacheName, key);
        if (account == null) {
            // 考虑到可能需要 join 状态不合法的用户，先放开 status 的判断
            // account = dao.findFirst("select * from account where id=? and status=? limit 1", accountId, Account.STATUS_OK);
            account = accountDao.findFirst("select * from sys_user where loginid=? and `_status`=1 order by iid desc limit 1", accountId);
            if (account != null) {
                account.removeSensitiveInfo();
                CacheKit.put(allAccountsCacheName, key, account);
            }
        }
        return account;
    }
    
    public static void init() {
		CacheKit.removeAll(allAccountsCacheName);
	}
    
    
    
    /**
     * 优先从缓存中获取 account 对象，可获取到被 block 的 account
     */
    public SysUser getByIId(String accountId) {
        // 优先从缓存中取，未命中缓存则从数据库取
    	String key=("useraccountId_"+accountId);
    	SysUser account = CacheKit.get(allAccountsCacheName, key);
        if (account == null) {
            // 考虑到可能需要 join 状态不合法的用户，先放开 status 的判断
            // account = dao.findFirst("select * from account where id=? and status=? limit 1", accountId, Account.STATUS_OK);
            account = accountDao.findFirst("select * from sys_user where id=? limit 1", accountId);
            if (account != null) {
                account.removeSensitiveInfo();
                CacheKit.put(allAccountsCacheName, key, account);
            }
        }
        return account;
    }

	/**
	 * 退出登录
	 */
	public void logout(String sessionId) {
		if (sessionId != null) {
			CacheKit.remove(loginAccountCacheName, sessionId);
			Session.dao.deleteById(sessionId);
		}
	}


	
	//查询到这个用户的所有的menuid
	public  List<Record> queryAllMenuId(Long uid) {
		List<Record> records=Db.find("select distinct rm.menu_id from sys_user_role ur LEFT JOIN sys_role_menu rm on ur.role_id = rm.role_id where ur.user_id = ?",uid);
		return records;
	}

	 /**
	    * 获取用户菜单列表
	 * @return 
    */
	public List<SysMenu> getUserMenuList(Long userId) {
		//系统管理员，拥有最高权限
		if(userId == 1){
			return getAllMenuList(null);
		}
		
		//用户菜单列表
		List<Record> menuIdList = queryAllMenuId(userId);
		return getAllMenuList(menuIdList);	
	}
	
	/**
	 * 获取所有菜单列表
	 */
	private List<SysMenu> getAllMenuList(List<Record> menuIdList){
		//查询根菜单列表
		List<SysMenu> menuList = queryListParentId(0L, menuIdList);
		//递归获取子菜单
		getMenuTreeList(menuList, menuIdList);
		
		return menuList;
	}
	/**
	 * 递归
	 */
	private List<SysMenu> getMenuTreeList(List<SysMenu> menuList, List<Record> menuIdList){
		List<SysMenu> subMenuList = new ArrayList<SysMenu>();
		
		for(SysMenu entity : menuList){
			if(entity.getType() == MenuType.CATALOG.getValue()){//目录
				entity.put("list",(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList)));
			}
			subMenuList.add(entity);
		}
		
		return subMenuList;
	}
	@SuppressWarnings("unlikely-arg-type")
	public List<SysMenu> queryListParentId(Long parentId, List<Record> menuIdList) {
		List<SysMenu> menuList = SysMenu.dao.queryListParentId(parentId);
		if(menuIdList == null){
			return menuList;
		}
		List<SysMenu> userMenuList = new ArrayList<>();
		for(SysMenu menu : menuList){

			for (int i = 0; i < menuIdList.size(); i++) {
				if(menuIdList.get(i).getLong("menu_id").equals(menu.getMenuId())){
					userMenuList.add(menu);
				}
				
			}
			
		}
		
		System.out.println("userMenuList"+userMenuList);
		return userMenuList;
	}
}
