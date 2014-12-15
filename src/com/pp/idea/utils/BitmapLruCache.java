package com.pp.idea.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.pp.idea.MyApplication;

public class BitmapLruCache implements ImageCache{
	private BitmapLruCache() {
	}

    // Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
    // Compression settings when writing images to disk cache
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG;
    private static final int DEFAULT_COMPRESS_QUALITY = 70;
    
	private static BitmapLruCache instance = new BitmapLruCache();
	private static LruCache<String, Bitmap> mMemoryCache;
	private static ImageCacheParams cacheParams;

	/**
	 * 第一次的时候可以设置缓存的参数，在之前调用{@link setCacheParams}
	 * @param context
	 * @return
	 */
	public static BitmapLruCache getInstance(Context context) {
		
		if (mMemoryCache == null) {
			if(cacheParams==null){
				cacheParams = new ImageCacheParams();
				cacheParams.setMemCacheSizePercent(context, 0.1f);
			}
			mMemoryCache = new LruCache<String, Bitmap>(cacheParams.memCacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap value) {
					LogUtil.info("bitmap size:"+getBitmapSize(value)+",key:"+key);
					return getBitmapSize(value);
				}
				
				@Override
				protected void entryRemoved(boolean evicted, String key,
						Bitmap oldValue, Bitmap newValue) {
					// TODO Auto-generated method stub
					if (evicted && oldValue !=null && !oldValue.isRecycled()) {  
						BitmapLruCache.getInstance(MyApplication.getContext()).clearData();
						clearGC();
                    } 
					
					LogUtil.info("超出缓存大小,oldValue:"+oldValue+",newValue:"+newValue);
//					if(newValue == null && oldValue != null){
//					}
					super.entryRemoved(evicted, key, oldValue, newValue);
				}
				
				@Override
				protected Bitmap create(String key) {
					// TODO Auto-generated method stub
					return super.create(key);
				}
			};
		}
		return instance;
	}

	public Bitmap remove(String key) {
		return mMemoryCache.remove(key);
	}

	@Override
	public Bitmap getBitmap(String key) {
		Bitmap bitmap = null;
		if (mMemoryCache != null) {
			bitmap = mMemoryCache.get(key);
			if(bitmap == null){
				mMemoryCache.remove(key);
			}
		}
		return bitmap;
	}

	@Override
	public void putBitmap(String key, Bitmap bitmap) {
		if(key==null || bitmap==null || mMemoryCache == null){
			return ;
		}
		
		if (mMemoryCache != null) {
			mMemoryCache.put(key, bitmap);
			clearGC();
		}
	}

	public void clear() {
		if (mMemoryCache != null) {
			clearData();
			mMemoryCache = null;
		}
	}
	
	public void clearData(){
		try {
			mMemoryCache.evictAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setCacheParams(ImageCacheParams cacheParams) {
		BitmapLruCache.cacheParams = cacheParams;
	}

	/**
     * Get the size in bytes of a bitmap.
     * @param bitmap
     * @return size in bytes
     */
    @TargetApi(12)
    public static int getBitmapSize(Bitmap bitmap) {
        if (Utils.hasHoneycombMR1()) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
	
	/**
     * A holder class that contains cache parameters.
     */
    public static class ImageCacheParams {
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
        public int compressQuality = DEFAULT_COMPRESS_QUALITY;

        /**
         * Sets the memory cache size based on a percentage of the device memory class.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the device memory
         * class. Throws {@link IllegalArgumentException} if percent is < 0.05 or > .8.
         *
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param context Context to use to fetch memory class
         * @param percent Percent of memory class to use to size memory cache
         */
        public void setMemCacheSizePercent(Context context, float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.05 and 0.8 (inclusive)");
            }
            
//            memCacheSize = Math.round(percent * MemUtils.getAvailRam(context));
            memCacheSize = (int) (Runtime.getRuntime().maxMemory())/8;

			LogUtil.info("max bitmap size:"+memCacheSize);
        }

        private static int getMemoryClass(Context context) {
            return ((ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE)).getMemoryClass();
        }
    }
    
    public static void clearGC(){
    	new Thread(){
    		public void run() {
    			Runtime.getRuntime().gc();
    		};
    	}.start();
    }
}
