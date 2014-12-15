package com.pp.idea.publish;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.pp.idea.GlobalParams;
import com.pp.idea.R;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.view.PhotoItemLayout;
import com.pp.idea.view.PhotupImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<PhotoUpload> data;
	private LayoutInflater mInflater;

	public ImageAdapter(Context context, List<PhotoUpload> data) {
		this.mContext = context;
		this.data = data;
		mInflater = LayoutInflater.from(mContext);
	}

	public List<PhotoUpload> getData() {
		return data;
	}

	@Override
	public int getCount() {
		return data.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.il_image, null);
		}
		PhotoItemLayout iv_pic = (PhotoItemLayout) convertView.findViewById(R.id.iv_pic);
		PhotupImageView iv = iv_pic.getImageView();
		TextView tv = iv_pic.getContentText();
		initParams(iv);
		if (position == getCount() - 1) {
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(R.drawable.hnx);
			tv.setVisibility(View.GONE);
			return convertView;
		} else {
			tv.setVisibility(View.VISIBLE);
			iv.setScaleType(ScaleType.CENTER_CROP);
		}

		setItemData(position, iv, tv);

		return convertView;
	}

	/**
	 * 设置每个Item的数据填充
	 * 
	 * @param position
	 * @param iv
	 * @param tv
	 */
	private void setItemData(int position, PhotupImageView iv, TextView tv) {
		iv.setDefaultImage(R.drawable.default_iv_loading);
		tv.setVisibility(View.VISIBLE);

		PhotoUpload upload = data.get(position);
		iv.setFadeInDrawables(false);
		iv.requestThumbnail(upload, false);
		if (TextUtils.isEmpty(upload.getDesc())) {
			tv.setText("点击输入描述");
		} else {
			tv.setText(upload.getDesc());
		}
	}

	private void initParams(View iv_pic) {
		int count = 4;
		LayoutParams params = iv_pic.getLayoutParams();
		float dimen_10 = mContext.getResources().getDimension(R.dimen.dimen_10);
		float dimen_3 = mContext.getResources().getDimension(R.dimen.dimen_3);
		int width = (int) Math.floor((GlobalParams.screenWidth - (dimen_10 * 2) - (dimen_3 * (count - 1))) / count);
		params.width = width;
		params.height = width;
	}

}
