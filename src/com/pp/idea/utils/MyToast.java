package com.pp.idea.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * 自定义toast，可以立刻将提示信息更新替换
 * @author wangpeng
 *
 */
public class MyToast {

	private static Toast mToast;
	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
		}
	};

	/**
	 * 
	 * @param mContext
	 * @param text
	 * @param duration 显示时长，单位：秒
	 */
	public static void showToast(Context mContext, String text, int duration) {

		mHandler.removeCallbacks(r);
		if (mToast != null)
			mToast.setText(text);
		else
			mToast = Toast.makeText(mContext, text, duration);
		mHandler.postDelayed(r, duration*1000);

		mToast.show();
	}

	public static void showToast(Context mContext, int resId, int duration) {
		showToast(mContext, mContext.getResources().getString(resId), duration);
	}

}
