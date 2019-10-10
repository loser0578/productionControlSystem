package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseJyOrder<M extends BaseJyOrder<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 订单号
	 */
	public M setOrderId(java.lang.String orderId) {
		set("order_id", orderId);
		return (M)this;
	}
	
	/**
	 * 订单号
	 */
	public java.lang.String getOrderId() {
		return getStr("order_id");
	}

	/**
	 * 生产批号
	 */
	public M setBatchId(java.lang.String batchId) {
		set("batch_id", batchId);
		return (M)this;
	}
	
	/**
	 * 生产批号
	 */
	public java.lang.String getBatchId() {
		return getStr("batch_id");
	}

	/**
	 * 下单日期
	 */
	public M setOrderTime(java.util.Date orderTime) {
		set("order_time", orderTime);
		return (M)this;
	}
	
	/**
	 * 下单日期
	 */
	public java.util.Date getOrderTime() {
		return get("order_time");
	}

	/**
	 * 要求交货日期
	 */
	public M setDeliveryTime(java.util.Date deliveryTime) {
		set("delivery_time", deliveryTime);
		return (M)this;
	}
	
	/**
	 * 要求交货日期
	 */
	public java.util.Date getDeliveryTime() {
		return get("delivery_time");
	}

	/**
	 * 客户
	 */
	public M setClient(java.lang.String client) {
		set("client", client);
		return (M)this;
	}
	
	/**
	 * 客户
	 */
	public java.lang.String getClient() {
		return getStr("client");
	}

	/**
	 * 负责人
	 */
	public M setPrincipal(java.lang.String principal) {
		set("principal", principal);
		return (M)this;
	}
	
	/**
	 * 负责人
	 */
	public java.lang.String getPrincipal() {
		return getStr("principal");
	}

	/**
	 * 优先级 1高级 2中级 3低级
	 */
	public M setLevel(java.lang.Integer level) {
		set("level", level);
		return (M)this;
	}
	
	/**
	 * 优先级 1高级 2中级 3低级
	 */
	public java.lang.Integer getLevel() {
		return getInt("level");
	}

	/**
	 * 订单的产品
	 */
	public M setAcceptProduct(java.lang.String acceptProduct) {
		set("accept_product", acceptProduct);
		return (M)this;
	}
	
	/**
	 * 订单的产品
	 */
	public java.lang.String getAcceptProduct() {
		return getStr("accept_product");
	}

	/**
	 * 订单状态 0未生产 1生产中 2已完工
	 */
	public M setStatus(java.lang.Integer Status) {
		set("_status", Status);
		return (M)this;
	}
	
	/**
	 * 订单状态 0未生产 1生产中 2已完工
	 */
	public java.lang.Integer getStatus() {
		return getInt("_status");
	}

	/**
	 * 0 删除 1 显示
	 */
	public M setState(java.lang.Integer state) {
		set("state", state);
		return (M)this;
	}
	
	/**
	 * 0 删除 1 显示
	 */
	public java.lang.Integer getState() {
		return getInt("state");
	}

	/**
	 * 完成时间
	 */
	public M setSuccessTime(java.util.Date successTime) {
		set("success_time", successTime);
		return (M)this;
	}
	
	/**
	 * 完成时间
	 */
	public java.util.Date getSuccessTime() {
		return get("success_time");
	}

	/**
	 * 是否是库存
	 */
	public M setIsShipment(java.lang.Integer isShipment) {
		set("is_shipment", isShipment);
		return (M)this;
	}
	
	/**
	 * 是否是库存
	 */
	public java.lang.Integer getIsShipment() {
		return getInt("is_shipment");
	}

	/**
	 * 是否已出货
	 */
	public M setIsOutshop(java.lang.Integer isOutshop) {
		set("is_outshop", isOutshop);
		return (M)this;
	}
	
	/**
	 * 是否已出货
	 */
	public java.lang.Integer getIsOutshop() {
		return getInt("is_outshop");
	}

	/**
	 * 出库日期
	 */
	public M setOutTime(java.util.Date outTime) {
		set("out_time", outTime);
		return (M)this;
	}
	
	/**
	 * 出库日期
	 */
	public java.util.Date getOutTime() {
		return get("out_time");
	}

	/**
	 * 0不开发票 1开发票
	 */
	public M setIsInvoice(java.lang.Integer isInvoice) {
		set("is_invoice", isInvoice);
		return (M)this;
	}
	
	/**
	 * 0不开发票 1开发票
	 */
	public java.lang.Integer getIsInvoice() {
		return getInt("is_invoice");
	}

	public M setPlantId(java.lang.Integer plantId) {
		set("plant_id", plantId);
		return (M)this;
	}
	
	public java.lang.Integer getPlantId() {
		return getInt("plant_id");
	}

}
