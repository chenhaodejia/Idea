package com.pp.idea.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.pp.idea.BuildConfig;
import com.pp.idea.Configure;
import com.pp.idea.MyApplication;

public class Utils {

	/**
	 * 程序是否显示在前台
	 * 需要全新
	 * uses-permission android:name ="android.permission.GET_TASKS"
	 * @param context
	 * @return
	 */
	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}

	/**
	 * 得到umeng测试设备的info
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}

			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			}

			json.put("device_id", device_id);

			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 得到数据库操作对象
	 * 
	 * @return
	 */
	public static DbUtils getDBInstance() {
		return DbUtils.create(MyApplication.getContext(), Configure.DB_NAME, Configure.DB_VERSION, new DbUpgradeListener() {

			@Override
			public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
			}
		});
	}

	/**
	 * 开始加载动画
	 * 
	 * @param iv
	 */
	public static void startLoading(ImageView iv) {
		if (iv == null || iv.getVisibility() == View.VISIBLE) {
			return;
		}
		startImageAnimation(iv);
	}
	
	public static void startImageAnimation(ImageView iv){
		AnimationDrawable drawable = (AnimationDrawable) iv.getDrawable();
		drawable.start();
		iv.setVisibility(View.VISIBLE);	
	}

	/**
	 * 停止加载动画
	 * 
	 * @param iv
	 */
	public static void stopLoading(ImageView iv) {
		if (iv == null || iv.getVisibility() == View.GONE) {
			return;
		}
		stopImageAnimation(iv);
	}
	
	public static void stopImageAnimation(ImageView iv){
		AnimationDrawable drawable = (AnimationDrawable) iv.getDrawable();
		drawable.stop();
		iv.setVisibility(View.GONE);
	}

	/**
	 * long 转换为 String 格式默认 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param createDate
	 * @return
	 */
	public static String formatTime(long date) {
		return formatTime(date, "yyyy-MM-dd HH:mm:ss");
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatTime(long date, String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		String desDate = formatter.format(new Date(date));
		return desDate;
	}

	/**
	 * String 转换为 long
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long paserTime(String date) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date parseDate = formatter.parse(date);
			long time = parseDate.getTime();
			return time;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getApplicationName(Context context, String packageName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(packageName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
		return applicationName;
	}
	
	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppVersion(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	/**
	 * 异步任务执行
	 * 
	 * @param task
	 * @param params
	 */
	public static <Params, Progress, Result> void executeAsyncTask(AsyncTask<Params, Progress, Result> task, Params... params) {
		if (Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}

	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

	/**
	 * 扫描图库，添加缩略图
	 * 
	 * @param context
	 * @param file
	 * @param listener
	 */
	public static void scanMediaJpegFile(final Context context, final String filePath, final OnScanCompletedListener listener) {
		MediaScannerConnection.scanFile(context, new String[] { filePath }, new String[] { "image/jpg" }, listener);
	}

	/**
	 * SD卡是否可用
	 * 
	 * @return
	 */
	public static boolean isExternalStorageAvilable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public static int getGridItemWidth(Activity context,int margin, int spacing, int numColumns) {
		int screenWidth = getScreenWidth(context);
		int itemWidth = (screenWidth - (margin * 2) - (spacing * (numColumns - 1))) / numColumns;
		return itemWidth;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * SDK是否大于12
	 * 
	 * @return
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static int getOrientationFromContentUri(ContentResolver cr, Uri contentUri) {
		int returnValue = 0;

		if (ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())) {
			// can post image
			String[] proj = { MediaStore.Images.Media.ORIENTATION };
			Cursor cursor = cr.query(contentUri, proj, null, null, null);

			if (null != cursor) {
				if (cursor.moveToFirst()) {
					returnValue = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
				}
				cursor.close();
			}
		} else if (ContentResolver.SCHEME_FILE.equals(contentUri.getScheme())) {
			returnValue = MediaUtils.getExifOrientation(contentUri.getPath());
		}

		return returnValue;
	}

	public static Bitmap rotate(Bitmap original, final int angle) {
		if ((angle % 360) == 0) {
			return original;
		}

		final boolean dimensionsChanged = angle == 90 || angle == 270;
		final int oldWidth = original.getWidth();
		final int oldHeight = original.getHeight();
		final int newWidth = dimensionsChanged ? oldHeight : oldWidth;
		final int newHeight = dimensionsChanged ? oldWidth : oldHeight;

		Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, original.getConfig());
		Canvas canvas = new Canvas(bitmap);

		Matrix matrix = new Matrix();
		matrix.preTranslate((newWidth - oldWidth) / 2f, (newHeight - oldHeight) / 2f);
		matrix.postRotate(angle, bitmap.getWidth() / 2f, bitmap.getHeight() / 2);
		canvas.drawBitmap(original, matrix, null);

		original.recycle();

		return bitmap;
	}

	public static Bitmap decodeImage(String path, int MAX_DIM) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeFile(path, o);
		} catch (SecurityException se) {
			se.printStackTrace();
			return null;
		}
		final int origWidth = o.outWidth;
		final int origHeight = o.outHeight;

		// Holds returned bitmap
		Bitmap bitmap = null;

		o.inJustDecodeBounds = false;
		o.inScaled = false;
		o.inPurgeable = true;
		o.inInputShareable = true;
		o.inDither = true;
		o.inPreferredConfig = Bitmap.Config.RGB_565;

		if (origWidth > MAX_DIM || origHeight > MAX_DIM) {
			int k = 1;
			int tmpHeight = origHeight, tmpWidth = origWidth;
			while ((tmpWidth / 2) >= MAX_DIM || (tmpHeight / 2) >= MAX_DIM) {
				tmpWidth /= 2;
				tmpHeight /= 2;
				k *= 2;
			}
			o.inSampleSize = k;

			bitmap = BitmapFactory.decodeFile(path, o);

		} else {
			bitmap = BitmapFactory.decodeFile(path, o);
		}

		return bitmap;
	}

	public static Bitmap decodeImage(final ContentResolver resolver, final Uri uri, final int MAX_DIM) throws FileNotFoundException {

		// Get original dimensions
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(resolver.openInputStream(uri), null, o);
		} catch (SecurityException se) {
			se.printStackTrace();
			return null;
		}

		final int origWidth = o.outWidth;
		final int origHeight = o.outHeight;

		// Holds returned bitmap
		Bitmap bitmap;

		o.inJustDecodeBounds = false;
		o.inScaled = false;
		o.inPurgeable = true;
		o.inInputShareable = true;
		o.inDither = true;
		o.inPreferredConfig = Bitmap.Config.RGB_565;

		InputStream is = resolver.openInputStream(uri);
		if (origWidth > MAX_DIM || origHeight > MAX_DIM) {
			int k = 1;
			int tmpHeight = origHeight, tmpWidth = origWidth;
			while ((tmpWidth / 2) >= MAX_DIM || (tmpHeight / 2) >= MAX_DIM) {
				tmpWidth /= 2;
				tmpHeight /= 2;
				k *= 2;
			}
			o.inSampleSize = k;

			// bitmap =
			// BitmapFactory.decodeFile("/storage/sdcard0/imgs/big image/IMG_2032.JPG",
			// o);
			bitmap = BitmapFactory.decodeStream(is, null, o);

		} else {
			// bitmap =
			// BitmapFactory.decodeFile("/storage/sdcard0/imgs/big image/IMG_2032.JPG",
			// o);
			bitmap = BitmapFactory.decodeStream(is, null, o);
		}
		try {
			is.close();
			is = null;
		} catch (IOException e) {
			e.printStackTrace();
			is = null;
		}

		if (null != bitmap) {
			if (BuildConfig.DEBUG) {
				Log.d("Utils", "Resized bitmap to: " + bitmap.getWidth() + "x" + bitmap.getHeight());
			}
		}

		return bitmap;
	}

	/**
	 * 相机是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasCamera(Context context) {
		PackageManager pm = context.getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	}

	/**
	 * Get the screen height.
	 * 
	 * @param context
	 * @return the screen height
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenHeight(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.y;
		}
		return display.getHeight();
	}

	/**
	 * Get the screen width.
	 * 
	 * @param context
	 * @return the screen width
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity context) {

		Display display = context.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

	/**
	 * 验证邮箱的有效性
	 * @param content
	 * @return
	 */
	public static boolean regexMail(String content) {
		String mailRegex = "^([a-z0-9A-Z]+[_-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return content.matches(mailRegex);
	}
	
	/**
	 * 获取状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        return statusBarHeight;
    }
	
	/**
	 * 释放Bitmap内存
	 * 
	 * @param bitmap
	 */
	public static void recycleBitmap(Bitmap bitmap){
		try {
			if(bitmap != null && !bitmap.isRecycled()){
				//	回收并置Bitmap为null
				bitmap.recycle();
				bitmap = null;
			}
		} catch (Exception e) {
			LogUtil.info("回收图片Bitmap时发生异常");
			e.printStackTrace();
		}
		System.gc();
	}
}
