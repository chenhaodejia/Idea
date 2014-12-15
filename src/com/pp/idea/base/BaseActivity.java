package com.pp.idea.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import cn.jpush.android.api.JPushInterface;

import com.lidroid.xutils.ViewUtils;
import com.pp.idea.GlobalParams;
import com.pp.idea.R;

public class BaseActivity extends Activity implements NetErrorListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		new GlobalParams().init(this);
		setLayout();
		ViewUtils.inject(this);
		init();
		loadData();
	}
	
	/**
	 * 设置布局文件
	 */
	protected void setLayout() {
	}

	/**
	 * 做一些初始化工作
	 */
	protected void init() {
	}

	/**
	 * 加载数据
	 */
	protected void loadData() {
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	/**
	 * 无网络情况下调用
	 */
	@Override
	public void netErrorListener() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void startActivityForResult(Intent intent, int requestCode) { 
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.slide_in_right, R.anim.scale_small);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.scale_small);
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.scale_big, R.anim.slide_out_right);
	}
}
