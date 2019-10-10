package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseShopGoodsSpecification<M extends BaseShopGoodsSpecification<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Long id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Long getId() {
		return getLong("id");
	}

	public M setGoodsId(java.lang.Long goodsId) {
		set("goods_id", goodsId);
		return (M)this;
	}
	
	public java.lang.Long getGoodsId() {
		return getLong("goods_id");
	}

	public M setSpecificationId(java.lang.Long specificationId) {
		set("specification_id", specificationId);
		return (M)this;
	}
	
	public java.lang.Long getSpecificationId() {
		return getLong("specification_id");
	}

	public M setValue(java.lang.String value) {
		set("value", value);
		return (M)this;
	}
	
	public java.lang.String getValue() {
		return getStr("value");
	}

	public M setPicUrl(java.lang.String picUrl) {
		set("pic_url", picUrl);
		return (M)this;
	}
	
	public java.lang.String getPicUrl() {
		return getStr("pic_url");
	}

}
