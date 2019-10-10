package cn.lhrj.moudules.shop.usercoupon;

public class PublishModel {
	public Integer getCouponId() {
		return couponId;
	}
	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}
	public String getGoodsIds() {
		return goodsIds;
	}
	public void setGoodsIds(String goodsIds) {
		this.goodsIds = goodsIds;
	}
	public boolean isSendSms() {
		return sendSms;
	}
	public void setSendSms(boolean sendSms) {
		this.sendSms = sendSms;
	}
	public Integer getSendType() {
		return sendType;
	}
	public void setSendType(Integer sendType) {
		this.sendType = sendType;
	}
	public String getUserIds() {
		return userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	private Integer couponId;
	private String goodsIds;
	private boolean sendSms;
	private Integer sendType;
	private String userIds;
}
