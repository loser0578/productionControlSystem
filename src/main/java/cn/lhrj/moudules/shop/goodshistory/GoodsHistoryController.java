package cn.lhrj.moudules.shop.goodshistory;

import cn.lhrj.component.base.BaseProjectController;

public class GoodsHistoryController extends BaseProjectController{
	
	GoodsHistoryService srv=GoodsHistoryService.me;
	
	public void index() {
		render("/page/shop/goodshistory.html");
	}
	

}
