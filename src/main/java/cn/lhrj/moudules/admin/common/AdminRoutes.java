package cn.lhrj.moudules.admin.common;


/**
 * @author 行动家!
 *
 *     乐活软件
 */
import com.jfinal.config.Routes;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.lhrj.common.interceptor.LoginSessionInterceptor;

import cn.lhrj.moudules.admin.AdminController;
import cn.lhrj.moudules.index.IndexController;
import cn.lhrj.moudules.jy.allproblem.AllProblemController;
import cn.lhrj.moudules.jy.client.ClientController;
import cn.lhrj.moudules.jy.company.CompanyController;
import cn.lhrj.moudules.jy.inprincipal.InPrincipalController;
import cn.lhrj.moudules.jy.order.OrderController;
import cn.lhrj.moudules.jy.outshop.OutShopController;
import cn.lhrj.moudules.jy.parts.PartsController;
import cn.lhrj.moudules.jy.plant.PlantController;
import cn.lhrj.moudules.jy.principal.PrincipalController;
import cn.lhrj.moudules.jy.problem.ProblemController;
import cn.lhrj.moudules.jy.process.ProcessController;
import cn.lhrj.moudules.jy.product.ProductController;
import cn.lhrj.moudules.jy.production.ProductionController;
import cn.lhrj.moudules.jy.repertory.RepertoryController;
import cn.lhrj.moudules.jy.staff.StaffController;
import cn.lhrj.moudules.jy.upload.UploadController;
import cn.lhrj.moudules.main.MainController;
import cn.lhrj.moudules.shop.blessing.BlessingController;
import cn.lhrj.moudules.shop.category.CategoryController;
import cn.lhrj.moudules.shop.categoryclassfy.CategoryClassfyController;
import cn.lhrj.moudules.shop.coupon.CouponController;
import cn.lhrj.moudules.shop.denomination.DenominationController;
import cn.lhrj.moudules.shop.goods.GoodsController;
import cn.lhrj.moudules.shop.goodsgallery.GoodsGalleryController;
import cn.lhrj.moudules.shop.goodshistory.GoodsHistoryController;
import cn.lhrj.moudules.shop.goodsspecification.GoodsSpecificationController;
import cn.lhrj.moudules.shop.order.OrderReciverController;
import cn.lhrj.moudules.shop.ordergoods.OrderGoodsController;
import cn.lhrj.moudules.shop.ordergoods.OrderTrackController;
import cn.lhrj.moudules.shop.product.ShopProductController;
import cn.lhrj.moudules.shop.shipping.ShippingController;
import cn.lhrj.moudules.shop.specification.SpecificationController;
import cn.lhrj.moudules.shop.user.ShopUserController;
import cn.lhrj.moudules.shop.usercoupon.UserCouponController;
import cn.lhrj.moudules.sys.dept.SysDeptController;
import cn.lhrj.moudules.sys.icon.SysIconController;
import cn.lhrj.moudules.sys.menu.SysMenuController;
import cn.lhrj.moudules.sys.role.SysRoleController;
import cn.lhrj.moudules.upload.SysOssController;


public class AdminRoutes extends Routes{

	@Override
	public void config() {
		addInterceptor(new LoginSessionInterceptor());
		addInterceptor(new SessionInViewInterceptor());
		// TODO Auto-generated method stub
		add("/login", AdminController.class,"");
		add("/index", IndexController.class,"");
		add("/main", MainController.class,"");
		//系统的菜单管理
		add("sys/menu", SysMenuController.class,"");
		//系统的商品规格
		add("shop/specification",SpecificationController.class,"");
		//商品分类
		add("shop/category",CategoryController.class,"");
		//图片上传
		add("sys/oss",SysOssController.class,"");
		//所有商品
		add("shop/goods",GoodsController.class,"");
		//商品详情图
		add("shop/goodsgallery",GoodsGalleryController.class,"");
		//商品规格
		add("shop/goodsspecification",GoodsSpecificationController.class,"");
		//祝福管理
		add("shop/blessing",BlessingController.class,"");
		//icon 管理
		add("sys/icon",SysIconController.class,"");

		//order 订单管理
		add("shop/order",OrderController.class,"");
		//order 接收订单管理
		add("shop/orderReciver",OrderReciverController.class,"");
		//shipp
		add("shop/shipping",ShippingController.class,"");
		//打印
		add("shop/ordergoods",OrderGoodsController.class,"");
		//物流
		add("shop/orderTrack",OrderTrackController.class,"");
		//商品回收站	
		add("shop/goodshistory",GoodsHistoryController.class,"");
		//商品分类的栏目
		add("shop/categoryclassify",CategoryClassfyController.class,"");
		//会员管理
		add("shop/shopuser",ShopUserController.class,"");
		//优惠券管理
		add("shop/coupon",CouponController.class,"");
		//会员优惠券管理
		add("shop/usercoupon",UserCouponController.class,"");
		//管理员管理
		add("sys/user",cn.lhrj.moudules.sys.user.SysUserController.class,"");
		//产品管理
		add("shop/product",ShopProductController.class,"");
		//系统角色
		add("sys/role",SysRoleController.class,"");
		//系统部门
		add("sys/dept",SysDeptController.class,"");
		//支付成功回掉
	    add("sys/notify",SysDeptController.class,"");
		//面额管理
	    add("shop/denomination",DenominationController.class,"");
	    
	    //工序管理
	    add("jy/process",ProcessController.class,"");
	    //部件管理
	    add("jy/parts",PartsController.class,"");
	    //产品管理
	    add("jy/product",ProductController.class,"");
	    //客户名称
	    add("jy/client",ClientController.class,"");
	    //负责人名称
	    add("jy/inprincipal",InPrincipalController.class,"");
	    //订单管理
	    add("jy/order",OrderController.class,"");
	    //负责人准时率
	    add("jy/principal",PrincipalController.class,"");
	    //公司准时率
	    add("jy/company",CompanyController.class,"");
	    //质量问题率
	    add("jy/problem",ProblemController.class,"");
	    //生产数据统计
	    add("jy/production",ProductionController.class,"");
	    //订单出货
	    add("jy/outshop",OutShopController.class,"");
	    //库存
	    add("jy/repertory",RepertoryController.class,"");
	    //图片上传
	    add("jy/update",UploadController.class,"");
	    //生产问题数据统计
	    add("jy/allproblem",AllProblemController.class,"");
	    //公司列表
	    add("jy/plant",PlantController.class,"");
	    //该厂员工列表
	    add("jy/staff",StaffController.class,"");
	}
}
