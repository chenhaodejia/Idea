/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pp.idea.view;

import java.lang.ref.WeakReference;
import java.util.concurrent.Future;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.pp.idea.BuildConfig;
import com.pp.idea.MyApplication;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.task.PhotupThreadRunnable;
import com.pp.idea.utils.BitmapLruCache;

public class PhotupImageView extends ImageView {
	
	private Drawable defalutDrawable = new ColorDrawable(Color.argb(255, 255, 255, 255));

    public static interface OnPhotoLoadListener {

        void onPhotoLoadFinished(Bitmap bitmap);
    }

//    static final class FaceDetectionRunnable extends PhotupThreadRunnable {
//
//        private final PhotoUpload mUpload;
//        private final CacheableBitmapWrapper mBitmapWrapper;
//
//        public FaceDetectionRunnable(PhotoUpload upload, CacheableBitmapWrapper bitmap) {
//            mUpload = upload;
//            mBitmapWrapper = bitmap;
//        }
//
//        public void runImpl() {
//            if (mBitmapWrapper.hasValidBitmap()) {
//                mUpload.detectPhotoTags(mBitmapWrapper.getBitmap());
//            }
//            mBitmapWrapper.setBeingUsed(false);
//        }
//    }

//    static final class PhotoFilterRunnable extends PhotupThreadRunnable {
//
//        private final WeakReference<PhotupImageView> mImageView;
//        private final PhotoUpload mUpload;
//        private final boolean mFullSize;
//        private final BitmapLruCache mCache;
//        private final OnPhotoLoadListener mListener;
//
//        public PhotoFilterRunnable(PhotupImageView imageView, PhotoUpload upload,
//                BitmapLruCache cache,
//                final boolean fullSize, final OnPhotoLoadListener listener) {
//            mImageView = new WeakReference<PhotupImageView>(imageView);
//            mUpload = upload;
//            mFullSize = fullSize;
//            mCache = cache;
//            mListener = listener;
//        }
//
//        public void runImpl() {
//            final PhotupImageView imageView = mImageView.get();
//            if (null == imageView) {
//                return;
//            }
//
//            final Context context = imageView.getContext();
//            final Bitmap filteredBitmap;
//
//            final String key = mFullSize ? mUpload.getDisplayImageKey()
//                    : mUpload.getThumbnailImageKey();
//            CacheableBitmapWrapper wrapper = mCache.get(key);
//
//            if (null == wrapper || !wrapper.hasValidBitmap()) {
//                Bitmap bitmap = mFullSize ? mUpload.getDisplayImage(context)
//                        : mUpload.getThumbnailImage(context);
//                wrapper = new CacheableBitmapWrapper(key, bitmap);
//                wrapper.setBeingUsed(true);
//                mCache.put(wrapper);
//            } else {
//                wrapper.setBeingUsed(true);
//            }
//
//            // Don't process if we've been interrupted
//            if (!isInterrupted()) {
//                filteredBitmap = mUpload.processBitmap(wrapper.getBitmap(), mFullSize, false);
//            } else {
//                filteredBitmap = null;
//            }
//
//            // Make sure we release the original bitmap
//            wrapper.setBeingUsed(false);
//
//            // If we haven't been interrupted, update the view
//            if (!isInterrupted()) {
//
//                imageView.post(new Runnable() {
//                    public void run() {
//                        imageView.setImageBitmap(filteredBitmap);
//
//                        if (null != mListener) {
//                            mListener.onPhotoLoadFinished(filteredBitmap);
//                        }
//                    }
//                });
//            }
//        }
//    }

    static final class PhotoLoadRunnable extends PhotupThreadRunnable {

        private final WeakReference<PhotupImageView> mImageView;
        private final PhotoUpload mUpload;
        private final boolean mFullSize;
        private final BitmapLruCache mCache;
        private final OnPhotoLoadListener mListener;

        public PhotoLoadRunnable(PhotupImageView imageView, PhotoUpload upload,
                BitmapLruCache cache,
                final boolean fullSize, final OnPhotoLoadListener listener) {
            mImageView = new WeakReference<PhotupImageView>(imageView);
            mUpload = upload;
            mFullSize = fullSize;
            mCache = cache;
            mListener = listener;
        }

        public void runImpl() {
            final PhotupImageView imageView = mImageView.get();
            if (null == imageView) {
                return;
            }

            final Context context = imageView.getContext();
            
            final Bitmap bitmap = mFullSize ? mUpload.getDisplayImage(context)
                    : mUpload.getThumbnailImage(context);

            if (null != bitmap) {
                final String key = mFullSize ? mUpload.getDisplayImageKey()
                        : mUpload.getThumbnailImageKey();

                mCache.putBitmap(key, bitmap);
                // If we're interrupted, just update the cache and return
                if (isInterrupted()) {
                    return;
                }
            } 

            // If we're still running, update the Views
            if (null != bitmap) {
                imageView.post(new Runnable() {
                    public void run() {
                        imageView.setImageBitmap(bitmap);

                        if (null != mListener) {
                            mListener.onPhotoLoadFinished(bitmap);
                        }
                    }
                });
            }
        }
    }

    ;

    static class RequestFaceDetectionPassRunnable implements Runnable {

        private final PhotupImageView mImageView;
        private final PhotoUpload mSelection;

        public RequestFaceDetectionPassRunnable(PhotupImageView imageView, PhotoUpload selection) {
            mImageView = imageView;
            mSelection = selection;
        }

        public void run() {
            mImageView.requestFaceDetection(mSelection);
        }
    }

    ;

    static final int FACE_DETECTION_DELAY = 800;
    ;

    private boolean mFadeInDrawables = false;
    private Drawable mFadeFromDrawable;
    private int mFadeDuration;

    private Runnable mRequestFaceDetectionRunnable;

    private Future<?> mCurrentRunnable;
	private int defaultResId = -1;

    public PhotupImageView(Context context) {
        super(context);
    }

    public PhotupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void cancelRequest() {
        if (null != mCurrentRunnable) {
            mCurrentRunnable.cancel(true);
            mCurrentRunnable = null;
        }
    }

    public void clearFaceDetection() {
        if (null != mRequestFaceDetectionRunnable) {
            removeCallbacks(mRequestFaceDetectionRunnable);
            mRequestFaceDetectionRunnable = null;
        }
    }

    public Bitmap getCurrentBitmap() {
        Drawable d = getDrawable();
        if (d instanceof BitmapDrawable) {
            return ((BitmapDrawable) d).getBitmap();
        }

        return null;
    }

    public void postFaceDetection(PhotoUpload selection) {
//        if (null == mRequestFaceDetectionRunnable && selection.requiresFaceDetectPass()) {
//            mRequestFaceDetectionRunnable = new RequestFaceDetectionPassRunnable(this, selection);
//            postDelayed(mRequestFaceDetectionRunnable, FACE_DETECTION_DELAY);
//        }
    }

    public void recycleBitmap() {
        Bitmap currentBitmap = getCurrentBitmap();
        if (null != currentBitmap) {
            setImageDrawable(null);
            currentBitmap.recycle();
        }
    }

    @SuppressWarnings("unused")
	public void requestFullSize(final PhotoUpload upload, final boolean honourFilter,
            final boolean clearDrawableOnLoad, final OnPhotoLoadListener listener) {
    	if(upload == null){
        	if(defaultResId!=-1){
        		setImageResource(defaultResId);
        	}else{
        		setImageDrawable(null);
        	}
    		return ;
    	}
    	
        resetForRequest(clearDrawableOnLoad);

        if (upload.requiresProcessing(true) && honourFilter) {
            requestFiltered(upload, true, listener);
        } else {
            // Show thumbnail if it's in the cache
        	BitmapLruCache cache = BitmapLruCache.getInstance(getContext());
            Bitmap cached = cache.getBitmap(upload.getThumbnailImageKey());
			if (cache != null) {
				if (BuildConfig.DEBUG) {
					Log.d("requestFullSize", "Got Cached Thumbnail");
				}
				setImageBitmap(cached);
			} else {
				setImageDrawable(null);
			}

            requestImage(upload, true, listener);
        }
    }

    public void requestFullSize(final PhotoUpload upload, final boolean honourFilter,
            final OnPhotoLoadListener listener) {
        requestFullSize(upload, honourFilter, true, listener);
    }

    public void requestThumbnail(final PhotoUpload upload, final boolean honourFilter) {
        resetForRequest(true);
        if(upload == null){
        	if(defaultResId!=-1){
        		setImageResource(defaultResId);
        	}else{
        		setImageDrawable(null);
        	}
        	return ;
        }

        if (upload.requiresProcessing(false) && honourFilter) {
            requestFiltered(upload, false, null);
        } else {
            requestImage(upload, false, null);
        }
    }

    public void setFadeInDrawables(boolean fadeIn) {
        mFadeInDrawables = fadeIn;

        if (fadeIn && null == mFadeFromDrawable) {
            mFadeFromDrawable = new ColorDrawable(Color.TRANSPARENT);
            mFadeDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (mFadeInDrawables && null != drawable) {
            TransitionDrawable newDrawable = new TransitionDrawable(
                    new Drawable[]{mFadeFromDrawable, drawable});
            super.setImageDrawable(newDrawable);
            newDrawable.startTransition(mFadeDuration);
        } else {
            super.setImageDrawable(drawable);
        }
    }

    private void requestFaceDetection(final PhotoUpload upload) {
//        CacheableBitmapWrapper wrapper = getCachedBitmapWrapper();
//        if (null != wrapper && wrapper.hasValidBitmap()) {
//            wrapper.setBeingUsed(true);
//
//            PhotupApplication app = PhotupApplication.getApplication(getContext());
//            app.getMultiThreadExecutorService().submit(new FaceDetectionRunnable(upload, wrapper));
//        }
    }

    private void requestFiltered(final PhotoUpload upload, boolean fullSize,
            final OnPhotoLoadListener listener) {
//        PhotupApplication app = PhotupApplication.getApplication(getContext());
//        mCurrentRunnable = app.getPhotoFilterThreadExecutorService().submit(
//                new PhotoFilterRunnable(this, upload, app.getImageCache(), fullSize, listener));
    }
    
    public void setDefaultImage(int resId){
    	this.defaultResId = resId;
    }

    private void requestImage(final PhotoUpload upload, final boolean fullSize,
            final OnPhotoLoadListener listener) {
        final String key = fullSize ? upload.getDisplayImageKey() : upload.getThumbnailImageKey();
        BitmapLruCache cache = BitmapLruCache.getInstance(getContext());
        Bitmap cached = cache.getBitmap(key);

        if (null != cached) {
            setImageBitmap(cached);
            if (null != listener) {
                listener.onPhotoLoadFinished(cached);
            }
        } else {
        	if(defaultResId != -1&&!fullSize){
        		setImageResource(defaultResId);
        	}
        	MyApplication app = MyApplication.getApplication(getContext());
            mCurrentRunnable = app.getMultiThreadExecutorService().submit(
                    new PhotoLoadRunnable(this, upload, cache, fullSize, listener));
        }
    }

    private void resetForRequest(final boolean clearDrawable) {
        cancelRequest();

        // Clear currently display bitmap
        if (clearDrawable) {
            setImageDrawable(defalutDrawable);
        }
    }

}
