package com.pp.idea.mode;

import com.pp.idea.Configure;

import cn.bmob.v3.BmobObject;

public class Collect extends BmobObject {

	public Collect() {
		this.setTableName(Configure.TABLE_COLLECT);
	}
}
