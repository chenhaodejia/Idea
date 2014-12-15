package com.pp.idea;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.pp.idea.utils.LogUtil;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private Context context;

	private MyUncaughtExceptionHandler() {
	}

	private static MyUncaughtExceptionHandler handler = new MyUncaughtExceptionHandler();

	public static MyUncaughtExceptionHandler getInstance() {
		return handler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		LogUtil.info("恭喜你，中奖了...");
		try {
			// 对捕获到的异常进行处理...
			// 1.获取当前程序的版本号，版本的id
			String versioninfo = getVersionInfo();
			// 2.获取手机的硬件信息.
			String mobileInfo = getMobileInfo();
			// 3.把错误的堆栈信息 获取出来
			String errorinfo = getErrorInfo(ex);
			LogUtil.info(errorinfo);
			// 4.把所有的信息 还有信息对应的时间 提交到服务器
			// 发送到umeng服务器
//			MobclickAgent.reportError(context, " versioninfo:" + versioninfo + "\n" + " mobileInfo:" + "\n" + " error:" + errorinfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 杀掉出异常的应用
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 获取手机的版本信息 　* @return
	 */
	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	/**
	 * 获取手机的硬件信息
	 * 
	 * @return
	 */
	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 ** 获取错误的信息
	 * 
	 * @param arg1
	 * @return 　　
	 */
	private String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		String error = writer.toString();
		return error;
	}

}
