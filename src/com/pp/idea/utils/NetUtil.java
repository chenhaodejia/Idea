package com.pp.idea.utils;

import org.apache.http.HttpHost;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.Uri;

public class NetUtil {
	private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

	/**
	 * 获取当前手机端的联网方式 一旦检测到当前用户使用的是wap方式，设置ip和端口
	 */
	public static boolean checkNet(Context context) {
		boolean wifiConnectivity = isWIFIConnectivity(context);
		boolean mobileConnectivity = isMobileConnectivity(context);

		if (wifiConnectivity == false && mobileConnectivity == false) {
			// 需要配置网络
			return false;
		}

		// mobile(APN)
		if (mobileConnectivity) {
			// wap方式时端口和ip
			// 不能写死：IP是10.0.0.172 端口是80
			// 一部分手机：IP是010.000.000.172 端口是80
			// 读取当亲的代理信息
//			readApn(context);
		}
		return true;
	}

	/**
	 * 读取ip和端口信息
	 * 
	 * @param context
	 */
	private static HttpHost readApn(Context context) {
		//4.0+ 不可用
//		ContentResolver resolver = context.getContentResolver();
//		Cursor query = resolver.query(PREFERRED_APN_URI, null, null, null, null);// 获取手机Apn：获取当前处于激活状态的APN信息
//		if (query != null && query.moveToFirst()) {
//			query.getString(query.getColumnIndex("proxy"));
//			query.getInt(query.getColumnIndex("port"));
//		}
		String proxy=Proxy.getDefaultHost();
		int port = Proxy.getDefaultPort() == -1 ? 80 : Proxy.getDefaultPort();
		HttpHost httpHost = new HttpHost(proxy, port, "http");
		return httpHost;
	}

	/**
	 * WIFI是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (info != null)
			return info.isConnected();
		return false;
	}

	/**
	 * 手机APN是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (info != null)
			return info.isConnected();
		return false;
	}

}
