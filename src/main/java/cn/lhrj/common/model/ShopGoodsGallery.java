package cn.lhrj.common.model;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cn.lhrj.common.model.base.BaseShopGoodsGallery;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class ShopGoodsGallery extends BaseShopGoodsGallery<ShopGoodsGallery> {
	public static final ShopGoodsGallery dao = new ShopGoodsGallery().dao();

	
	
	//这里处理二进制的图片地址转换
	public List<ShopGoodsGallery> findAll(String sql) {
		List<ShopGoodsGallery> list=new ShopGoodsGallery().find(sql);
		for (ShopGoodsGallery shopGoodsGallery : list) {
			if (shopGoodsGallery.getImgUrl() !=null && shopGoodsGallery.getImgUrl().length !=0 ) {
				try {
					String 	imgUrl = new String(shopGoodsGallery.getImgUrl(),"UTF-8");
					shopGoodsGallery.put("imgUrl", imgUrl);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}
}
