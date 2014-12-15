package com.pp.idea.mode;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images.Thumbnails;
import android.text.TextUtils;

import com.pp.idea.BuildConfig;
import com.pp.idea.MyApplication;
import com.pp.idea.R;
import com.pp.idea.utils.BitmapLruCache;
import com.pp.idea.utils.LogUtil;
import com.pp.idea.utils.Utils;

public class PhotoUpload implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1990020001921992L;

	private static final HashMap<Uri, PhotoUpload> SELECTION_CACHE = new HashMap<Uri, PhotoUpload>();

	public static final int STATE_SELECTED = 1;
	public static final int STATE_NONE = 0;

	private int mUserRotation;
	private Uri mFullUri;
	private String mFullUriString;
	private String mImagePath;
	private String desc;

	private int mState;

	static final int MINI_THUMBNAIL_SIZE = 300;
	static final int MICRO_THUMBNAIL_SIZE = 96;

	private boolean isOOMError;
	
	private PhotoUpload(Uri uri) {
		mFullUri = uri;
		mFullUriString = uri.toString();
		reset();
	}

	public void setImagePath(String path) {
		this.mImagePath = path;
	}

	public String getImagePath() {
		return mImagePath;
	}

	public String getmFullUriString() {
		return mFullUriString;
	}

	public void setmFullUriString(String mFullUriString) {
		this.mFullUriString = mFullUriString;
	}

	public static PhotoUpload getSelection(Uri uri) {
		// Check whether we've already got a Selection cached
		PhotoUpload item = SELECTION_CACHE.get(uri);

		if (null == item) {
			item = new PhotoUpload(uri);
			SELECTION_CACHE.put(uri, item);
			if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
				String path = uri.getPath();
				item.setImagePath(path);
			}
		}

		return item;
	}

	public static PhotoUpload getSelection(Uri baseUri, long id) {
		return getSelection(Uri.withAppendedPath(baseUri, String.valueOf(id)));
	}

	public Bitmap getDisplayImage(Context context) {
		try {
			final int size = MyApplication.getApplication(context).getSmallestScreenDimension();
			Bitmap bitmap = Utils.decodeImage(context.getContentResolver(), getOriginalPhotoUri(), size);
			bitmap = Utils.rotate(bitmap, getExifRotation(context));
			if (null == bitmap) {
				bitmap = Utils.decodeImage(mImagePath, size);
			}
			isOOMError = false;
			return bitmap;
		} catch (OutOfMemoryError error){
			BitmapLruCache.getInstance(context).clearData();
			LogUtil.info("大图片内存溢出了");
			if(isOOMError){
				return null;
			}else{
				isOOMError = true;
				return getDisplayImage(context);
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Bitmap getThumbnailImage(Context context) {
		if (ContentResolver.SCHEME_CONTENT.equals(getOriginalPhotoUri().getScheme())) {
			return getThumbnailImageFromMediaStore(context);
		}

		final Resources res = context.getResources();
		int size = res.getBoolean(R.bool.load_mini_thumbnails) ? MINI_THUMBNAIL_SIZE : MICRO_THUMBNAIL_SIZE;
		if (size == MINI_THUMBNAIL_SIZE && res.getBoolean(R.bool.sample_mini_thumbnails)) {
			size /= 2;
		}

		try {
			Bitmap bitmap = Utils.decodeImage(context.getContentResolver(), getOriginalPhotoUri(), size);
			bitmap = Utils.rotate(bitmap, getExifRotation(context));
			if (bitmap == null) {
				bitmap = Utils.decodeImage(mImagePath, size);
			}
			isOOMError = false;
			return bitmap;
		} catch (OutOfMemoryError error){
			BitmapLruCache.getInstance(context).clearData();
			LogUtil.info("小图片内存溢出了");
			if(isOOMError){
				return null;
			}else{
				isOOMError = true;
				return getThumbnailImage(context);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Bitmap getThumbnailImageFromMediaStore(Context context) {
		Resources res = context.getResources();

		final int kind = res.getBoolean(R.bool.load_mini_thumbnails) ? Thumbnails.MINI_KIND : Thumbnails.MICRO_KIND;

		BitmapFactory.Options opts = null;
		if (kind == Thumbnails.MINI_KIND && res.getBoolean(R.bool.sample_mini_thumbnails)) {
			opts = new BitmapFactory.Options();
			opts.inSampleSize = 2;
		}

		try {
			final long id = Long.parseLong(getOriginalPhotoUri().getLastPathSegment());

			Bitmap bitmap = Thumbnails.getThumbnail(context.getContentResolver(), id, kind, opts);
			bitmap = Utils.rotate(bitmap, getExifRotation(context));
			return bitmap;
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public int getExifRotation(Context context) {
		return Utils.getOrientationFromContentUri(context.getContentResolver(), getOriginalPhotoUri());
	}

	private void reset() {
		mState = STATE_NONE;

	}

	public String getDisplayImageKey() {
		return "dsply_" + getOriginalPhotoUri();
	}

	public String getThumbnailImageKey() {
		return "thumb_" + getOriginalPhotoUri();
	}

	public Uri getOriginalPhotoUri() {
		if (null == mFullUri && !TextUtils.isEmpty(mFullUriString)) {
			mFullUri = Uri.parse(mFullUriString);
		}
		return mFullUri;
	}

	public boolean requiresProcessing(final boolean fullSize) {
		return getUserRotation() != 0 || beenFiltered() || (fullSize && beenCropped());
	}

	private boolean beenCropped() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean beenFiltered() {
		return true;
	}

	public int getUserRotation() {
		return mUserRotation % 360;
	}

	public String getCaption() {
		return "pphdsny";
	}

	public void setUploadState(int state) {
		if (mState != state) {
			mState = state;
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
