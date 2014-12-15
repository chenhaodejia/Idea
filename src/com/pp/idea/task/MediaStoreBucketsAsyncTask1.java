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
package com.pp.idea.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.pp.idea.mode.MediaStoreBucket;
import com.pp.idea.photo.MediaStoreCursorHelper;

public class MediaStoreBucketsAsyncTask1 extends AsyncTask<Void, Void, List<MediaStoreBucket>> {
	
	public static final int STATUS_LOAD_ALL_PHOTO = 1;
	public static final int STATUS_LOAD_CAMERA_PHOTO = 2;

    public static interface MediaStoreBucketsResultListener {

        public void onBucketsLoaded(List<MediaStoreBucket> buckets);
    }

	private static int loadStatus = STATUS_LOAD_ALL_PHOTO;

    private final WeakReference<Context> mContext;
    private final WeakReference<MediaStoreBucketsResultListener> mListener;

    /**
     * 
     * @param context
     * @param status 
     * 		{@link MediaStoreBucketsAsyncTask1.STATUS_LOAD_ALL_PHOTO} 加载全部图片
     * 		{@link MediaStoreBucketsAsyncTask1.STATUS_LOAD_CAMERA_PHOTO} 加载相册图片
     * @param listener
     */
    public static void execute(Context context, int status,MediaStoreBucketsResultListener listener) {
    	loadStatus  = status;
        new MediaStoreBucketsAsyncTask1(context, listener).execute();
    }

    private MediaStoreBucketsAsyncTask1(Context context, MediaStoreBucketsResultListener listener) {
        mContext = new WeakReference<Context>(context);
        mListener = new WeakReference<MediaStoreBucketsResultListener>(listener);
    }

    @Override
    protected List<MediaStoreBucket> doInBackground(Void... params) {
        ArrayList<MediaStoreBucket> result = null;
        Context context = mContext.get();

        if (null != context) {
            // Add 'All Photos' item
            result = new ArrayList<MediaStoreBucket>();
//            result.add(MediaStoreBucket.getAllPhotosBucket(context));

            Cursor cursor = null;
            switch (loadStatus) {
			case STATUS_LOAD_ALL_PHOTO:
				cursor = MediaStoreCursorHelper.openPhotosCursor(context, MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI);
				break;
			case STATUS_LOAD_CAMERA_PHOTO:
				cursor = MediaStoreCursorHelper.openCameraPhotosCursor(context, MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI);
				break;

			default:
				break;
			}

            if (null != cursor) {
                MediaStoreCursorHelper.photosCursorToBucketPhotoList(cursor, result);
                cursor.close();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(List<MediaStoreBucket> result) {
        super.onPostExecute(result);

        MediaStoreBucketsResultListener listener = mListener.get();
        if (null != listener) {
            listener.onBucketsLoaded(result);
        }
    }

}
