package com.pp.idea.mode;

import java.util.List;

import cn.bmob.v3.BmobObject;

import com.pp.idea.Configure;

public class Idea extends BmobObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 123456789008L;
	
	private String title;
	private String content;
	private int liked;
	private User user;
	private int unliked;
	private int status;
	private int lookNum;
	private String extra_content;
	private List<IdeaExtraContent> extraContentList;

	public Idea() {
		this.setTableName(Configure.TABLE_IDEA);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLiked() {
		return liked;
	}

	public void setLiked(int liked) {
		this.liked = liked;
	}

	public int getUnliked() {
		return unliked;
	}

	public void setUnliked(int unliked) {
		this.unliked = unliked;
	}

	public String getExtra_content() {
		return extra_content;
	}

	public void setExtra_content(String extra_content) {
		this.extra_content = extra_content;
	}

	public List<IdeaExtraContent> getExtraContentList() {
		return extraContentList;
	}

	public void setExtraContentList(List<IdeaExtraContent> extraContentList) {
		this.extraContentList = extraContentList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getLookNum() {
		return lookNum;
	}

	public void setLookNum(int lookNum) {
		this.lookNum = lookNum;
	}

}
