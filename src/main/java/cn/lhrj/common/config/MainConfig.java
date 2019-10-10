package cn.lhrj.common.config;


import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;
import com.lhrj.common.interceptor.ExceptionIntoLogInterceptor;

import cn.lhrj.common.engine.NowDirective;
import cn.lhrj.common.model.Session;
import cn.lhrj.common.model.ShopAddress;
import cn.lhrj.common.model.ShopAttribute;
import cn.lhrj.common.model.ShopAttributeCategory;
import cn.lhrj.common.model.ShopBlessing;
import cn.lhrj.common.model.ShopBrand;
import cn.lhrj.common.model.ShopCategory;
import cn.lhrj.common.model.ShopCategoryclassfy;
import cn.lhrj.common.model.ShopCoupon;
import cn.lhrj.common.model.ShopCouponGoods;
import cn.lhrj.common.model.ShopGoods;
import cn.lhrj.common.model.ShopGoodsAttribute;
import cn.lhrj.common.model.ShopGoodsGallery;
import cn.lhrj.common.model.ShopGoodsSpecification;
import cn.lhrj.common.model.ShopIndexImg;
import cn.lhrj.common.model.ShopOrder;
import cn.lhrj.common.model.ShopOrderGoods;
import cn.lhrj.common.model.ShopOrderLog;
import cn.lhrj.common.model.ShopOrderReciver;
import cn.lhrj.common.model.ShopProduct;
import cn.lhrj.common.model.ShopRechargeLog;
import cn.lhrj.common.model.ShopShipping;
import cn.lhrj.common.model.ShopSpecification;
import cn.lhrj.common.model.ShopTopicCategory;
import cn.lhrj.common.model.ShopUser;
import cn.lhrj.common.model.ShopUserCoupon;
import cn.lhrj.common.model.SysArea;
import cn.lhrj.common.model.SysConfig;
import cn.lhrj.common.model.SysDept;
import cn.lhrj.common.model.SysMenu;
import cn.lhrj.common.model.SysMessage;
import cn.lhrj.common.model.SysRemain;
import cn.lhrj.common.model.SysRole;
import cn.lhrj.common.model.SysRoleMenu;
import cn.lhrj.common.model.SysUser;
import cn.lhrj.common.model.SysUserRole;
import cn.lhrj.common.model.TaskRunLog;
import cn.lhrj.common.model.TbToken;
import cn.lhrj.common.model.UploadCounter;
import cn.lhrj.common.model._MappingKit;
import cn.lhrj.moudules.admin.common.AdminRoutes;
import cn.lhrj.moudules.admin.common.ApiRoutes;
import cn.lhrj.moudules.admin.common.LoginHandler;
import cn.lhrj.moudules.admin.system.config.ConfigCache;
import cn.lhrj.moudules.api.controller.ApiIndexController;

public class MainConfig extends JFinalConfig {
	
	protected Log log=Log.getLog(ApiIndexController.class);

	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		//读取数据库配置文件
		PropKit.use("config.properties");
		//设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		//设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath("upload/temp/");
		//设置上传最大限制尺寸

		me.setMaxPostSize(1024*1024*100);
		//设置默认下载文件路径 renderFile使用
		me.setBaseDownloadPath("download");
		//设置默认视图类型
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		//设置404渲染视图
		//me.setError404View("404.html");
        me.setInjectDependency(true);
		//设置启用依赖注入
		me.setInjectDependency(true);
		
		
	}
	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		me.add(new AdminRoutes());
		me.add(new ApiRoutes());
	}
	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		//配置数据库连接池插件
		DruidPlugin dbPlugin=new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		//orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp=new ActiveRecordPlugin(dbPlugin);
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new MysqlDialect());
		dbPlugin.setDriverClass("com.mysql.jdbc.Driver");
		arp.addSqlTemplate("/sql/_all_sqls.sql");
		/********在此添加数据库 表-Model 映射*********/
		//如果使用了JFinal Model 生成器 生成了BaseModel 把下面注释解开即可
		_MappingKit.mapping(arp);
		me.add(new EhCachePlugin());
		//添加到插件列表中
		me.add(dbPlugin);
		me.add(arp);
		me.add(new Cron4jPlugin(PropKit.use("config.properties"),"cron4j"));
	}
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
        //添加控制层全局拦截器
        //interceptors.addGlobalActionInterceptor(new GlobalActionInterceptor());
		me.add(new ExceptionIntoLogInterceptor());

	}
	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {
		me.add(new LoginHandler());
//		RoutesHandler handler=new RoutesHandler();
//		handler.addRoute("index.html","index");
//		handler.addRoute("login.html","login");
//		me.add(handler);
	}
	
	/**
	 * 配置模板引擎 
	 */
	@Override
	public void configEngine(Engine me) {
		//这里只有选择JFinal TPL的时候才用
		//配置共享函数模板
		//me.addSharedFunction("/view/common/layout.html")
		me.addDirective("now", NowDirective.class);
	}
	public void afterJFinalStart() {
		ConfigCache.init();
		System.out.println("##################################");
		System.out.println("############系统启动完成##########");
		System.out.println("##################################");
        // 配置微信 API 相关参数
        WxaConfig wc = new WxaConfig();
        wc.setAppId(PropKit.get("appId"));
        wc.setAppSecret(PropKit.get("appSecret"));
        WxaConfigKit.setWxaConfig(wc);
	}
	
   public static void main(String[] args) {
        UndertowServer.start(MainConfig.class);
   }
}