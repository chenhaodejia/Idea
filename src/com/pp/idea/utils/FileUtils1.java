package com.pp.idea.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class FileUtils1 {

	private static final int MAX_SIZE = 512000;// 压缩在500k内
	private static final int MAX_SCALE = 1600;// 2448 * 3264

	private static final String TAG = "FileUtils";

	public static Bitmap getBitmap(String path) {

		File f = new File(path);
		int degree = readPictureDegree(f.getAbsolutePath());
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		int maxSize;
		if (opts.outHeight > opts.outWidth) {
			maxSize = opts.outHeight;
		} else {
			maxSize = opts.outWidth;
		}
		if (maxSize > MAX_SCALE) {
			opts.inSampleSize = Math.round((maxSize * 1.0f) / MAX_SCALE);
		} else {
			opts.inSampleSize = 1;
		}
		opts.inJustDecodeBounds = false;

		try {
			Log.i(TAG, "file.length=" + f.length());
			resizeBmp = BitmapFactory.decodeFile(f.getPath(), opts);
		} catch (Exception err) {
			err.printStackTrace();
			Log.i(TAG, "BitmapFactory.decodeFile(f.getPath(),opts) has err!!!!!!");
		}
		return rotaingImageView(degree, resizeBmp);
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 只压缩图片质量
	 * 
	 * @param path
	 * @param quality
	 * @return
	 */
	public static InputStream compressImage(String path, int quality) {
		ByteArrayInputStream isBm = null;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			bitmap = rotaingImageView(readPictureDegree(path), bitmap);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			isBm = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			bitmap = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isBm;
	}

	/**
	 * 
	 * @param bitmap
	 * @param maxSize
	 *            最大值 单位是KB
	 * @return
	 */
	public static InputStream compressImage(Bitmap bitmap, int maxSize) {
		if (bitmap == null) {
			return null;
		}
		ByteArrayInputStream isBm = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > maxSize) {
			baos.reset();
			options -= 5;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		isBm = new ByteArrayInputStream(baos.toByteArray());

		// close io
		try {
			baos.close();
			// isBm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isBm;
	}

	/**
	 * 
	 * @param bitmap
	 * @param quality
	 *            图片质量
	 * @return
	 */
	public static InputStream compressImageByQuality(Bitmap bitmap, int quality) {
		if (bitmap == null) {
			return null;
		}
		ByteArrayInputStream isBm = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
		isBm = new ByteArrayInputStream(baos.toByteArray());

		// close io
		try {
			baos.close();
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return isBm;
	}

	public static void cleanFiles(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			File[] fs = file.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}
		}
	}

	/**
	 * 
	 * @param srcPath
	 *            源文件地址
	 * @param descParentPath
	 *            目标文件目录地址
	 * @param descPath
	 *            目标文件名称（非具体地址）
	 * @return 目标文件的地址
	 */
	public static String copyFile(String srcPath, String descParentPath, String descPath) {
		if (!new File(srcPath).exists()) {
			return "";
		}
		descPath = descParentPath + descPath;
		File descParentFile = new File(descParentPath);
		File descFile = new File(descPath);
		if (!descParentFile.exists()) {
			descParentFile.mkdirs();
		}
		if (!descFile.isFile() || !descFile.exists()) {
			try {
				descFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(srcPath);
			fo = new FileOutputStream(descPath);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				fi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return descPath;
	}

	public static String getCopyName(String srcPath) {
		int firstHalfLength = srcPath.length() / 2;
		String localName = String.valueOf(srcPath.substring(0, firstHalfLength).hashCode());
		localName += String.valueOf(srcPath.substring(firstHalfLength).hashCode());
		return localName;
	}

	/**
	 * 获取本地映射到空间的地址，默认返回本地，本地不存在，返回映射空间地址
	 * 
	 * @param path
	 * @return
	 */
	public static String getPhotoCopyPath(String path) {
//		if (TextUtils.isEmpty(path)) {
//			return "";
//		}
		String resultPath = path;
//		// 使用copy目录下的图片资源
//		String copyPath = Configure.COPY_PHOTO_PATH + FileUtils1.getCopyName(path);
//		// 本地不存在，再加载copy过去的图片（本地会有缩略图，快些）
//		if (!new File(path).exists()) {
//			resultPath = copyPath;
//		}
		return resultPath;
	}

	private void compressUtil() {

		// 压缩的尺码
		// if (f.length() < MAX_SIZE) { // 500k 不压缩
		// opts.inSampleSize = 1;
		// } else if (f.length() < MAX_SIZE * 2) { // 500-1M
		// opts.inSampleSize = 2;
		// } else if (f.length() < MAX_SIZE * 4) { // 1M-1.5M
		// opts.inSampleSize = 4;
		// } else if (f.length() < MAX_SIZE * 8) {// 2-4M
		// opts.inSampleSize = 6;
		// } else if (f.length() < MAX_SIZE * 10) {// 5M
		// opts.inSampleSize = 8;
		// } else {
		// opts.inSampleSize = 12;
		// }
	}

	/**
	 * 删除一个文件
	 * 
	 * @param filePath
	 *            要删除的文件路径名
	 * @return true if this file was deleted, false otherwise
	 */
	public static boolean deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				return file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建一个文件，创建成功返回true
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean createFile(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}

				return file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public static void writeImage(Bitmap bitmap, String destPath, int quality) {
		try {
			FileUtils1.deleteFile(destPath);
			if (FileUtils1.createFile(destPath)) {
				FileOutputStream out = new FileOutputStream(destPath);
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
					out.flush();
					out.close();
					out = null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
