package cn.lhrj.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSysRole<M extends BaseSysRole<M>> extends Model<M> implements IBean {

	public M setRoleId(java.lang.Long roleId) {
		set("role_id", roleId);
		return (M)this;
	}
	
	public java.lang.Long getRoleId() {
		return getLong("role_id");
	}

	/**
	 * 角色名称
	 */
	public M setRoleName(java.lang.String roleName) {
		set("role_name", roleName);
		return (M)this;
	}
	
	/**
	 * 角色名称
	 */
	public java.lang.String getRoleName() {
		return getStr("role_name");
	}

	/**
	 * 备注
	 */
	public M setRemark(java.lang.String remark) {
		set("remark", remark);
		return (M)this;
	}
	
	/**
	 * 备注
	 */
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	/**
	 * 创建者ID
	 */
	public M setCreateUserId(java.lang.Long createUserId) {
		set("create_user_id", createUserId);
		return (M)this;
	}
	
	/**
	 * 创建者ID
	 */
	public java.lang.Long getCreateUserId() {
		return getLong("create_user_id");
	}

	/**
	 * 创建时间
	 */
	public M setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
		return (M)this;
	}
	
	/**
	 * 创建时间
	 */
	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	/**
	 * 部门ID
	 */
	public M setDeptId(java.lang.Long deptId) {
		set("dept_id", deptId);
		return (M)this;
	}
	
	/**
	 * 部门ID
	 */
	public java.lang.Long getDeptId() {
		return getLong("dept_id");
	}

	public M setRoleKey(java.lang.String roleKey) {
		set("role_key", roleKey);
		return (M)this;
	}
	
	public java.lang.String getRoleKey() {
		return getStr("role_key");
	}

}
