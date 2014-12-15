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
package com.pp.idea.mode;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class MediaStoreBucket {

	private String mBucketId;
	private String mBucketName;
	private int count;
	private List<PhotoUpload> imageList;
	private Bitmap defaultIcon;

	public MediaStoreBucket(String id, String name) {
		mBucketId = id;
		mBucketName = name;
	}

	public String getmBucketId() {
		return mBucketId;
	}

	public void setmBucketId(String mBucketId) {
		this.mBucketId = mBucketId;
	}

	public String getmBucketName() {
		return mBucketName;
	}

	public void setmBucketName(String mBucketName) {
		this.mBucketName = mBucketName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<PhotoUpload> getImageList() {
		return imageList;
	}

	public void setImageList(List<PhotoUpload> imageList) {
		this.imageList = imageList;
	}

	public static MediaStoreBucket getAllPhotosBucket(Context context) {
		return new MediaStoreBucket(null, "All Photos");
	}

	public Bitmap getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(Bitmap defaultIcon) {
		this.defaultIcon = defaultIcon;
	}

	@Override
	public String toString() {
		return mBucketName;
	}

}
