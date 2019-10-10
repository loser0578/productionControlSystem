package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseShopTopicCategory<M extends BaseShopTopicCategory<M>> extends Model<M> implements IBean {

	/**
	 * 主键
	 */
	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	/**
	 * 主键
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 活动类别主题
	 */
	public M setTitle(java.lang.String title) {
		set("title", title);
		return (M)this;
	}
	
	/**
	 * 活动类别主题
	 */
	public java.lang.String getTitle() {
		return getStr("title");
	}

	/**
	 * 活动类别图片链接
	 */
	public M setPicUrl(java.lang.String picUrl) {
		set("pic_url", picUrl);
		return (M)this;
	}
	
	/**
	 * 活动类别图片链接
	 */
	public java.lang.String getPicUrl() {
		return getStr("pic_url");
	}

}
