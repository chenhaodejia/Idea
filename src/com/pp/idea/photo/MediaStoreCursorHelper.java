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
package com.pp.idea.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;

import com.pp.idea.mode.MediaStoreBucket;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.utils.Utils;

public class MediaStoreCursorHelper {

	public static final String[] PHOTOS_PROJECTION = { Media._ID, Media.BUCKET_ID, Media.PICASA_ID, Media.DATA, Media.DISPLAY_NAME, Media.TITLE, Media.SIZE, Media.BUCKET_DISPLAY_NAME };
	public static final String PHOTOS_ORDER_BY = Images.Media.DATE_ADDED + " desc";

	public static final Uri MEDIA_STORE_CONTENT_URI = Images.Media.EXTERNAL_CONTENT_URI;

	public static PhotoUpload getPhotoUpload(Context context, String path) {
		if (!Utils.isExternalStorageAvilable() || TextUtils.isEmpty(path)) {
			return null;
		} else {
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
		}
		PhotoUpload upload = null;
		//UI线程中 卡
//		Cursor cursor = getCursor(context, path);
//		if (cursor.getCount() != 0) {// 存在
//			upload = photosCursorToSelection(MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, cursor);
//		}
//		cursor.close();
		// 未保存至媒体数据库
		if (upload == null) {
			Uri uri = Uri.parse("file://" + path);
			upload = PhotoUpload.getSelection(uri);
		}
		return upload;
	}
	
	public static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public static Cursor getCursor(Context context, String imagePath) {
		ContentResolver cr = context.getContentResolver();
		String whereClause = ImageColumns.DATA + " = '" + imagePath + "'";

		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns._ID, ImageColumns.DATA }, whereClause, null, null);
		cursor.moveToFirst();
		return cursor;
	}

	public static ArrayList<PhotoUpload> photosCursorToSelectionList(Uri contentUri, Cursor cursor) {
		ArrayList<PhotoUpload> items = new ArrayList<PhotoUpload>(cursor.getCount());
		PhotoUpload item;

		if (cursor.moveToFirst()) {
			do {
				try {
					item = photosCursorToSelection(contentUri, cursor);
					if (null != item) {
						items.add(item);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}

		// Need to reset the List so that oldest is first
		Collections.reverse(items);

		return items;
	}

	public static PhotoUpload photosCursorToSelection(Uri contentUri, Cursor cursor) {
		PhotoUpload item = null;
		try {
			cursor.getString(0);
			File file = new File(cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA)));
			if (file.exists()) {
//				item = PhotoUpload.getSelection(contentUri, cursor.getInt(cursor.getColumnIndexOrThrow(ImageColumns._ID)));
//				String path = cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA));
//				item.setImagePath(path);
				Uri uri = Uri.parse("file://" + file.getAbsolutePath());
				item = PhotoUpload.getSelection(uri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return item;
	}

	public static void photosCursorToBucketPhotoList(Cursor cur, ArrayList<MediaStoreBucket> items) {
		final HashMap<String, MediaStoreBucket> bucketIds = new HashMap<String, MediaStoreBucket>();
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			// int photoIDIndex = cur.getColumnIndexOrThrow(Media._ID);
			// int photoPathIndex = cur.getColumnIndexOrThrow(Media.DATA);
			// int photoNameIndex =
			// cur.getColumnIndexOrThrow(Media.DISPLAY_NAME);
			// int photoTitleIndex = cur.getColumnIndexOrThrow(Media.TITLE);
			// int photoSizeIndex = cur.getColumnIndexOrThrow(Media.SIZE);
			int bucketDisplayNameIndex = cur.getColumnIndexOrThrow(Media.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur.getColumnIndexOrThrow(Media.BUCKET_ID);
			// int picasaIdIndex = cur.getColumnIndexOrThrow(Media.PICASA_ID);

			do {
				PhotoUpload upload = MediaStoreCursorHelper.photosCursorToSelection(MediaStoreCursorHelper.MEDIA_STORE_CONTENT_URI, cur);
				if(upload == null){
					continue;
				}
				//判断文件属性
				File file = new File(upload.getImagePath());
				if(!file.exists() || file.length()< 10 * 1024){//不存在或者小于10k
					continue;
				}
				
				// String _id = cur.getString(photoIDIndex);
				// String name = cur.getString(photoNameIndex);
				// String path = cur.getString(photoPathIndex);
				// String title = cur.getString(photoTitleIndex);
				// String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				// String picasaId = cur.getString(picasaIdIndex);

				MediaStoreBucket bucket = bucketIds.get(bucketId);
				if (bucket == null) {
					bucket = new MediaStoreBucket(bucketId, bucketName);
					items.add(bucket);
					bucket.setImageList(new ArrayList<PhotoUpload>());
					bucketIds.put(bucketId, bucket);
				}
				bucket.setCount(bucket.getCount() + 1);
				if (upload != null) {
					bucket.getImageList().add(upload);
				}

			} while (cur.moveToNext());
		}
	}

	@Deprecated
	public static void photosCursorToBucketList(Cursor cursor, ArrayList<MediaStoreBucket> items) {
		final HashSet<String> bucketIds = new HashSet<String>();

		final int idColumn = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
		final int nameColumn = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);

		if (cursor.moveToFirst()) {
			do {
				try {
					final String bucketId = cursor.getString(idColumn);
					if (bucketIds.add(bucketId)) {
						items.add(new MediaStoreBucket(bucketId, cursor.getString(nameColumn)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cursor.moveToNext());
		}
	}

	public static Cursor openPhotosCursor(Context context, Uri contentUri) {
		return context.getContentResolver().query(contentUri, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);
	}
	
	public static Cursor openCameraPhotosCursor(Context context, Uri contentUri) {
		String name = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getName();
		String selection = Media.BUCKET_DISPLAY_NAME+"= '"+name +"'";
		return context.getContentResolver().query(contentUri, PHOTOS_PROJECTION, selection, null, PHOTOS_ORDER_BY);
	}

}
