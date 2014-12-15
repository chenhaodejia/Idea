package com.pp.idea.mode;

import com.pp.idea.Configure;

import cn.bmob.v3.BmobObject;

public class Message extends BmobObject {

	private String content;
	private String extra_content;

	public Message() {
		this.setTableName(Configure.TABLE_MESSAGE);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtra_content() {
		return extra_content;
	}

	public void setExtra_content(String extra_content) {
		this.extra_content = extra_content;
	}

}
