package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseJyProduct<M extends BaseJyProduct<M>> extends Model<M> implements IBean {

	/**
	 * 产品id
	 */
	public M setProductId(java.lang.Integer productId) {
		set("product_id", productId);
		return (M)this;
	}
	
	/**
	 * 产品id
	 */
	public java.lang.Integer getProductId() {
		return getInt("product_id");
	}

	/**
	 * 产品名
	 */
	public M setProductName(java.lang.String productName) {
		set("product_name", productName);
		return (M)this;
	}
	
	/**
	 * 产品名
	 */
	public java.lang.String getProductName() {
		return getStr("product_name");
	}

	public M setImg(java.lang.String img) {
		set("img", img);
		return (M)this;
	}
	
	public java.lang.String getImg() {
		return getStr("img");
	}

	/**
	 * 部件id
	 */
	public M setPartsId(java.lang.String partsId) {
		set("parts_id", partsId);
		return (M)this;
	}
	
	/**
	 * 部件id
	 */
	public java.lang.String getPartsId() {
		return getStr("parts_id");
	}
	
	/**
	 * 部件id
	 */
	public M setPartsNumber(java.lang.String partsNumber) {
		set("parts_number", partsNumber);
		return (M)this;
	}
	
	/**
	 * 部件id
	 */
	public java.lang.String getPartsNumber() {
		return getStr("parts_number");
	}

	/**
	 * 1显示 0隐藏
	 */
	public M setState(java.lang.Integer state) {
		set("state", state);
		return (M)this;
	}
	
	/**
	 * 1显示 0隐藏
	 */
	public java.lang.Integer getState() {
		return getInt("state");
	}

	public M setSort(java.lang.Integer sort) {
		set("sort", sort);
		return (M)this;
	}
	
	public java.lang.Integer getSort() {
		return getInt("sort");
	}

	/**
	 * 所需原材料
	 */
	public M setMaterials(java.lang.String materials) {
		set("materials", materials);
		return (M)this;
	}
	
	/**
	 * 所需原材料
	 */
	public java.lang.String getMaterials() {
		return getStr("materials");
	}

	public M setPlantId(java.lang.Integer plantId) {
		set("plant_id", plantId);
		return (M)this;
	}
	
	public java.lang.Integer getPlantId() {
		return getInt("plant_id");
	}

}
