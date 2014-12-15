package com.pp.idea;


public interface Configure {

	String DB_NAME = "pp_idea";

	// 目前的数据库版本
	int DB_VERSION = 1;
	
	//TODO 请求基础URL
	String BASE_URL = "";
	
	//home fragment
	int HOME_FIND_IDEA = 1;
	int HOME_DISCUSS_IDEA = 2;
	int HOME_PUBLISH_IDEA = 3;
	int HOME_COLLECT_IDEA = 4;
	int HOME_SETTING = 5;
	int HOME_TUIJIAN = 6;

	//bmob 数据库名称
	String TABLE_IDEA = "Idea";
	String TABLE_MESSAGE = "rl_message";
	String TABLE_LIKE = "rl_like";
	String TABLE_USER = "_User";
	String TABLE_REPORT_IDEA = "rl_report_idea";
	String TABLE_REPORT_MESSAGE = "rl_report_message";
	String TABLE_COLLECT = "rl_collect";

	//最多添加照片数
	int MAX_ADD_PHOTO = 9;
	
	//small image
	String PHOTO_SMALL = "!small";

}
