package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseShopUserCoupon<M extends BaseShopUserCoupon<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setCouponId(java.lang.Integer couponId) {
		set("coupon_id", couponId);
		return (M)this;
	}
	
	public java.lang.Integer getCouponId() {
		return getInt("coupon_id");
	}

	public M setCouponNumber(java.lang.String couponNumber) {
		set("coupon_number", couponNumber);
		return (M)this;
	}
	
	public java.lang.String getCouponNumber() {
		return getStr("coupon_number");
	}

	public M setUserId(java.lang.Long userId) {
		set("user_id", userId);
		return (M)this;
	}
	
	public java.lang.Long getUserId() {
		return getLong("user_id");
	}

	public M setUsedTime(java.util.Date usedTime) {
		set("used_time", usedTime);
		return (M)this;
	}
	
	public java.util.Date getUsedTime() {
		return get("used_time");
	}

	public M setAddTime(java.util.Date addTime) {
		set("add_time", addTime);
		return (M)this;
	}
	
	public java.util.Date getAddTime() {
		return get("add_time");
	}

	public M setOrderId(java.lang.Integer orderId) {
		set("order_id", orderId);
		return (M)this;
	}
	
	public java.lang.Integer getOrderId() {
		return getInt("order_id");
	}

	/**
	 * 来源key
	 */
	public M setSourceKey(java.lang.String sourceKey) {
		set("source_key", sourceKey);
		return (M)this;
	}
	
	/**
	 * 来源key
	 */
	public java.lang.String getSourceKey() {
		return getStr("source_key");
	}

	/**
	 * 发券人
	 */
	public M setReferrer(java.lang.Integer referrer) {
		set("referrer", referrer);
		return (M)this;
	}
	
	/**
	 * 发券人
	 */
	public java.lang.Integer getReferrer() {
		return getInt("referrer");
	}

	/**
	 * 状态 1. 可用 2. 已用 3. 过期
	 */
	public M setCouponStatus(java.lang.Long couponStatus) {
		set("coupon_status", couponStatus);
		return (M)this;
	}
	
	/**
	 * 状态 1. 可用 2. 已用 3. 过期
	 */
	public java.lang.Long getCouponStatus() {
		return getLong("coupon_status");
	}

}
