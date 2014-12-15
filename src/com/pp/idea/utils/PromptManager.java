package com.pp.idea.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.pp.idea.R;

/**
 * 提示信息的管理
 */

public class PromptManager {
	// 滚动条
	private static ProgressDialog dialog;
	
	// 提示框
	// Toast
	
	/**
	 * 显示item的dialog ，无title
	 * @param context
	 * @param items
	 * @param listener
	 */
	public static void showItemDialog(Context context, String[] items, OnClickListener listener){
		showItemDialog(context, null, items, listener);
	}
	
	public static void showItemDialog(Context context, String title,String[] items, OnClickListener listener){
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setItems(items, listener)
		.create();
		if(!TextUtils.isEmpty(title)){
			dialog.setTitle(title);
		}
		dialog.show();
	}

	public static void showProgressDialog(Context context, String msg) {
		dialog = new ProgressDialog(context);
		// dialog.setIcon(R.drawable.icon);
		dialog.setTitle(R.string.app_name);

		if (TextUtils.isEmpty(msg)) {
			dialog.setMessage("努力加载中,请稍等...");
		} else {
			dialog.setMessage(msg);
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	public static void setOnCancelListener(OnCancelListener cancelListener) {
		if (cancelListener != null) {
			dialog.setOnCancelListener(cancelListener);
		}
	}

	public static void closeProgressDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
		if(alertDialog != null && alertDialog.isShowing()){
			alertDialog.dismiss();
			alertDialog = null;
		}
	}

	/**
	 * 当判断当前手机没有网络时使用
	 * 
	 * @param context
	 */
	public static void showNoNetWork(final Context context) {
		showToast(context, "网络不可用");
		// AlertDialog.Builder builder = new Builder(context);
		// builder.setIcon(R.drawable.icon)//
		// .setTitle(R.string.app_name)//
		// .setMessage("当前无网络").setPositiveButton("设置", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // 跳转到系统的网络设置界面
		// Intent intent=new Intent();
		// intent.setClassName("com.android.settings",
		// "com.android.settings.WirelessSettings");
		// context.startActivity(intent);
		//
		// }
		// }).setNegativeButton("知道了", null).show();
	}

	/**
	 * 退出系统
	 * 
	 * @param context
	 */
	public static void showExitSystem(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setIcon(R.drawable.ic_launcher)//
				.setTitle(R.string.app_name)//
				.setMessage("是否退出应用").setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						android.os.Process.killProcess(android.os.Process.myPid());
						// 多个Activity——懒人听书：没有彻底退出应用
						// 将所有用到的Activity都存起来，获取全部，干掉
						// BaseActivity——onCreated——放到容器中
						// 默认的activity放在栈底
					}
				})//
				.setNegativeButton("取消", null)//
				.show();

	}

	public static void showWarnDialog(Context context, String msg, OnClickListener sureListener) {
		showWarnDialog(context, msg, "确定", "取消", sureListener, null, null);
	}

	public static void showWarnDialog(Context context, String msg, OnClickListener sureListener, OnClickListener cancelListener) {
		showWarnDialog(context, msg, "确定", "取消", sureListener, cancelListener, null);
	}

	public static void showWarnDialog(Context context, String msg, String sureNote, String cancleNote, OnClickListener sureListener, OnClickListener cancelListener, OnCancelListener dismissListener) {
		AlertDialog.Builder builder = new Builder(context);
		if (dismissListener != null) {
			builder.setOnCancelListener(dismissListener);
		}
		alertDialog = builder.setTitle(R.string.app_name).setMessage(msg).setPositiveButton(sureNote, sureListener).setNegativeButton(cancleNote, cancelListener).show();
	}

	public static void showNoteDialog(Context context, String msg, String note, OnClickListener noteListener) {
		AlertDialog.Builder builder = new Builder(context);

		builder.setTitle(R.string.app_name).setMessage(msg).setPositiveButton(note, noteListener).show();
	}
	
	public static void showNoteViewDialog(Context context,String title,View view,String sureNote,DialogInterface.OnClickListener sureListener,String cancelNote,DialogInterface.OnClickListener cancelListener){
		AlertDialog.Builder builder = new Builder(context);
		if(!TextUtils.isEmpty(title)){
			builder.setTitle(title);
		}
		if(!TextUtils.isEmpty(sureNote)){
			builder.setPositiveButton(sureNote, sureListener);
		}
		if(!TextUtils.isEmpty(cancelNote)){
			builder.setNegativeButton(cancelNote, cancelListener);
		}
		alertDialog = builder.setView(view)
		.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		}).show();
	}
	
	public static void showNoteViewDialog(Context context,String title,View view){
		showNoteViewDialog(context, title, view, null, null, null, null);
	}

	public static void showToast(Context context, String msg) {
		MyToast.showToast(context, msg, 2);
	}

	public static void showToast(Context context, int msgResId) {
		MyToast.showToast(context, msgResId, 2);
	}
	
	public static void showToast(Context context, int msgResId,int duration) {
		Toast.makeText(context, msgResId, duration*1000).show();;
	}

	// 当测试阶段时true
	private static final boolean isShow = true;
	private static AlertDialog alertDialog;

	/**
	 * 测试用 在正式投入市场：删
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showToastTest(Context context, String msg) {
		if (isShow) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 显示错误提示框
	 * 
	 * @param context
	 * @param msg
	 */
	public static void showErrorDialog(Context context, String msg) {
		new AlertDialog.Builder(context)//
				.setIcon(R.drawable.ic_launcher)//
				.setTitle(R.string.app_name)//
				.setMessage(msg)//
				.setNegativeButton("确定", null)//
				.show();
	}

}
