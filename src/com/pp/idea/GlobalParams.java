package com.pp.idea;

import java.util.List;

import android.app.Activity;

import cn.bmob.v3.BmobUser;

import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.mode.User;
import com.pp.idea.utils.Utils;

public class GlobalParams {

	public static Activity context;
	//屏幕长宽
	public static int screenWidth;
	public static int screenHeight;
	
	public static List<PhotoUpload> photoList;
	
	public static User userInfo;


	public void init(Activity activity) {
		if(userInfo != null){
			return ;
		}
		userInfo = BmobUser.getCurrentUser(activity, User.class);
		int width = Utils.getScreenWidth(activity);
		int height = Utils.getScreenHeight(activity);
		if (width > height) {
			screenHeight = width;
			screenWidth = height;
		} else {
			screenHeight = height;
			screenWidth = width;
		}

		context = activity;
	}
}
