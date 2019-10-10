package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseYjParts<M extends BaseYjParts<M>> extends Model<M> implements IBean {

	public M setPartsId(java.lang.Integer partsId) {
		set("parts_id", partsId);
		return (M)this;
	}
	
	public java.lang.Integer getPartsId() {
		return getInt("parts_id");
	}

	/**
	 * 部件名
	 */
	public M setPartsName(java.lang.String partsName) {
		set("parts_name", partsName);
		return (M)this;
	}
	
	/**
	 * 部件名
	 */
	public java.lang.String getPartsName() {
		return getStr("parts_name");
	}

	public M setImg(java.lang.String img) {
		set("img", img);
		return (M)this;
	}
	
	public java.lang.String getImg() {
		return getStr("img");
	}

	/**
	 * 工序id
	 */
	public M setProcessId(java.lang.String processId) {
		set("process_id", processId);
		return (M)this;
	}
	
	/**
	 * 工序id
	 */
	public java.lang.String getProcessId() {
		return getStr("process_id");
	}

	public M setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
		return (M)this;
	}
	
	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public M setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
		return (M)this;
	}
	
	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	/**
	 * 0隐藏  1显示
	 */
	public M setState(java.lang.Integer state) {
		set("state", state);
		return (M)this;
	}
	
	/**
	 * 0隐藏  1显示
	 */
	public java.lang.Integer getState() {
		return getInt("state");
	}

}
