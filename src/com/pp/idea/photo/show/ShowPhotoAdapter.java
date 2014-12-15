package com.pp.idea.photo.show;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView.ScaleType;

import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.view.PhotupImageView.OnPhotoLoadListener;
import com.pp.idea.view.photoview.PhotoView;
import com.pp.idea.view.photoview.PhotoViewAttacher;

public class ShowPhotoAdapter extends PagerAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<PhotoUpload> data;
	private PhotoSelectController mPhotoController;

	public ShowPhotoAdapter(Context context, List<PhotoUpload> data,PhotoSelectController mPhotoController) {
		this.context = context;
		mInflater = LayoutInflater.from(context);
		this.data = data;
		this.mPhotoController = mPhotoController;
	}
	
	public List<PhotoUpload> getData(){
		return data;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = showContentView(container, position);
		container.addView(view);
		return view;
	}

	private View showContentView(ViewGroup container, final int position) {
		final PhotoUpload upload = data.get(position);
		View view = mInflater.inflate(R.layout.il_show_photo_item, null);
		PhotoView iv_content_image = (PhotoView) view.findViewById(R.id.iv_content_image);
		EditText et_content_desc = (EditText) view.findViewById(R.id.et_content_desc);
		
		et_content_desc.setTag("content_desc_"+position);
		if(TextUtils.isEmpty(upload.getDesc())){
			et_content_desc.setText("");
		}else{
			et_content_desc.setText(upload.getDesc());
		}
		
		PhotoViewAttacher mAttacher = iv_content_image.getAttacher();
//		mAttacher.setScaleType(ScaleType.FIT_XY);
		mAttacher.setEdit(true);
		iv_content_image.setImageDrawable(new ColorDrawable(Color.argb(255, 255, 255, 255)));
		if (upload != null) {
			iv_content_image.requestFullSize(upload, false, new OnPhotoLoadListener() {

				@Override
				public void onPhotoLoadFinished(Bitmap bitmap) {
//					Utils.stopLoading(iv_loading);
				}
			});
		}
		et_content_desc.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				upload.setDesc(s.toString());
			}
		});
		return view;
	}

}
