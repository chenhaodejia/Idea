package com.pp.idea.mode;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

import com.pp.idea.Configure;

public class User extends BmobUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7584930218L;

	private String avatar;
	private String source;
	private int status;
	private String otherName;
	private String openId;
	private BmobRelation ideas;

	public User() {
		this.setTableName(Configure.TABLE_USER);
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public BmobRelation getIdeas() {
		return ideas;
	}

	public void setIdeas(BmobRelation ideas) {
		this.ideas = ideas;
	}

}
