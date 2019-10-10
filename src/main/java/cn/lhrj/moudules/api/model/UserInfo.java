package cn.lhrj.moudules.api.model;

import java.io.Serializable;

public class UserInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	//
    private String avatarUrl;
    //
    private String city;
    //
    private Integer gender;
    //
    private String nickName;
    //
    private String province;

}
