package com.pp.idea.net.base;

import java.io.File;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.pp.idea.MyApplication;
import com.pp.idea.utils.BitmapLruCache;
import com.pp.idea.utils.CacheUtils;

/**
 * Created by WangPeng on 2013.12.03.
 */
public class RequestManager<T> {
	public static RequestQueue mRequestQueue = newRequestQueue();
	// 取运行内存阈值的1/3作为图片缓存
	private static final int MEM_CACHE_SIZE = 1024 * 1024 * ((ActivityManager) MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() / 3;

	// private static ImageLoader mImageLoader = new ImageLoader(mRequestQueue,
	// new BitmapLruCache(
	// MEM_CACHE_SIZE));

	private static ImageLoader mImageLoader = new ImageLoader(mRequestQueue, BitmapLruCache.getInstance(MyApplication.getContext()));

	private static DiskBasedCache mDiskCache = (DiskBasedCache) mRequestQueue.getCache();

	private RequestManager() {
	}

	private static Cache openCache() {
		return new DiskBasedCache(CacheUtils.getExternalCacheDir(MyApplication.getContext()), 100 * 1024 * 1024);
	}

	private static RequestQueue newRequestQueue() {
		RequestQueue requestQueue = new RequestQueue(openCache(), new BasicNetwork(new HurlStack()));
		requestQueue.start();
		return requestQueue;
	}

	public static void addRequest(Request request, Object tag) {
		if (tag != null) {
			request.setTag(tag);
		}
		mRequestQueue.add(request);
	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

	public static File getCachedImageFile(String url) {
		return mDiskCache.getFileForKey(url);
	}

	public static void clearDiskCache() {
		mDiskCache.clear();
	}

	public static ImageLoader.ImageContainer loadImage(String requestUrl, ImageLoader.ImageListener imageListener) {
		return loadImage(requestUrl, imageListener, 0, 0);
	}

	public static ImageLoader.ImageContainer loadImage(String requestUrl, ImageLoader.ImageListener imageListener, int maxWidth, int maxHeight) {
		ImageContainer imageContainer;
		try {
			imageContainer = mImageLoader.get(requestUrl, imageListener, maxWidth, maxHeight);
		} catch (Exception e) {
			imageContainer = mImageLoader.get("", imageListener);
			e.printStackTrace();
		}
		return imageContainer;
	}

	public static ImageLoader.ImageListener getImageListener(final ImageView view, final Drawable defaultImageDrawable, final Drawable errorImageDrawable) {
		return new ImageLoader.ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (errorImageDrawable != null) {
					view.setImageDrawable(errorImageDrawable);
				}
				BitmapLruCache.getInstance(MyApplication.getContext()).clearData();
			}

			@Override
			public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
				if (response.getBitmap() != null) {
					if (!isImmediate && defaultImageDrawable != null) {
//						TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[] { defaultImageDrawable,
//								new BitmapDrawable(MeetApplication.getContext().getResources(), response.getBitmap()) });
//						transitionDrawable.setCrossFadeEnabled(true);
//						view.setImageDrawable(transitionDrawable);
//						transitionDrawable.startTransition(100);
						view.setImageBitmap(response.getBitmap());
					} else {
						view.setImageBitmap(response.getBitmap());
					}
				} else if (defaultImageDrawable != null) {
					view.setImageDrawable(defaultImageDrawable);
				}
			}
		};
	}
}
