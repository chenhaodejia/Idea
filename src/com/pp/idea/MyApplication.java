package com.pp.idea;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import cn.bmob.v3.Bmob;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;

import com.pp.idea.task.PhotupThreadFactory;

public class MyApplication extends Application {

	static final String LOG_TAG = "MeetApplication";

	static final float EXECUTOR_POOL_SIZE_PER_CORE = 1.5f;
	private ExecutorService mMultiThreadExecutor, mSingleThreadExecutor, mDatabaseThreadExecutor;

	private PhotoSelectController mPhotoController;

	private static Context mContext;
	
	public static Context getContext() {
		return mContext;
	}
	
	public PhotoSelectController getPhotoSelectController() {
		return mPhotoController;
	}

	public static MyApplication getApplication(Context context) {
		return (MyApplication) context.getApplicationContext();
	}

	public ExecutorService getMultiThreadExecutorService() {
		if (null == mMultiThreadExecutor || mMultiThreadExecutor.isShutdown()) {
			final int numThreads = Math.round(Runtime.getRuntime().availableProcessors() * EXECUTOR_POOL_SIZE_PER_CORE);
			mMultiThreadExecutor = Executors.newFixedThreadPool(numThreads, new PhotupThreadFactory());

			if (BuildConfig.DEBUG) {
				Log.d(LOG_TAG, "MultiThreadExecutor created with " + numThreads + " threads");
			}
		}
		return mMultiThreadExecutor;
	}

	@SuppressWarnings("deprecation")
	public int getSmallestScreenDimension() {
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return Math.min(display.getHeight(), display.getWidth());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		mPhotoController = new PhotoSelectController(this);

		MyUncaughtExceptionHandler handler = MyUncaughtExceptionHandler.getInstance();
		handler.init(this);
		Thread.setDefaultUncaughtExceptionHandler(handler);
		
		//bmob init SDK
		Bmob.initialize(this, "44688cef2fc2c2ffa95f1cb59c2062ee");
		//init shareSDK
		ShareSDK.initSDK(this);
		//init JPush 
		JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
	}
}
