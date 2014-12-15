package com.pp.idea.net;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import android.graphics.Bitmap;

import com.pp.idea.UpyunConfigure;
import com.pp.idea.utils.EncryptUtils;
import com.pp.idea.utils.FileUtils1;
import com.pp.idea.utils.LogUtil;
import com.pp.idea.utils.Utils;
import com.upyun.api.Uploader;
import com.upyun.api.utils.UpYunUtils;

public class UpyunApi {

	private static String DEFAULT_DOMAIN = UpyunConfigure.DEFAULT_DOMAIN;
	private static String API_KEY = UpyunConfigure.API_KEY; // 表单api验证密钥
	private static String BUCKET = UpyunConfigure.BUCKET;
	private static final long EXPIRATION = System.currentTimeMillis() / 1000 + 100000 * 5 * 10; // 过期时间，必须大于当前时间

	/**
	 * 往upyun上，传图片
	 * 
	 * @param iamgePath
	 *            图片的本地地址
	 * @return 成功返回地址，失败返回null
	 */
	public String uploadImage(String imagePath) {
		String retValue = null;
		try {
			// 设置服务器上保存文件的目录和文件名，如果服务器上同目录下已经有同名文件会被自动覆盖的。
			String formatTime = Utils.formatTime(System.currentTimeMillis());
			String year = formatTime.substring(0, 4);
			String month = formatTime.substring(5, 7);
			String day = formatTime.substring(8, 10);
			String name = EncryptUtils.encodeMD5("android" + UUID.randomUUID() + imagePath);

			String SAVE_KEY = File.separator + year + File.separator + month + File.separator + day + File.separator + name + ".jpg";

			// 取得base64编码后的policy
			String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);

			// 根据表单api签名密钥对policy进行签名
			// 通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
			String signature = UpYunUtils.signature(policy + "&" + API_KEY);

			// 对上传图片进行压缩
			Bitmap bitmap = FileUtils1.getBitmap(imagePath);
			InputStream is = FileUtils1.compressImageByQuality(bitmap, 90);
			retValue = Uploader.upload(policy, signature, BUCKET, imagePath, is);
			// retValue = Uploader.upload(policy, signature, BUCKET, imagePath);
			retValue = DEFAULT_DOMAIN + retValue;
			// 关闭流
			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != bitmap && !bitmap.isRecycled()) {
					LogUtil.info("回收～");
					bitmap.recycle();
					bitmap = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retValue;
	}
}
