package com.pp.idea.photo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pp.idea.R;
import com.pp.idea.mode.MediaStoreBucket;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.view.photoview.PhotoView;

public class SelectPhotoAdapter extends BaseAdapter {

	private Context context;
	private List<MediaStoreBucket> list;
	private LayoutInflater mInflater;

	public SelectPhotoAdapter(Context context, List<MediaStoreBucket> list) {
		this.list = list;
		this.context = context;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.il_select_photo_item, null);
		}

		ViewHolder holder = getHolder(convertView);
		MediaStoreBucket bucket = list.get(position);

		holder.build(bucket);
		return convertView;
	}

	private ViewHolder getHolder(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder(view);
			view.setTag(holder);
		}
		return holder;
	}

	private class ViewHolder {

		private TextView tv_name;
		private PhotoView iv_photo;
		private TextView tv_desc;

		public ViewHolder(View view) {
			iv_photo = (PhotoView) view.findViewById(R.id.iv_photo);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			tv_desc = (TextView) view.findViewById(R.id.tv_desc);
		}

		public void build(MediaStoreBucket bucket) {
			tv_name.setText(bucket.getmBucketName());
			if(bucket.getCount()>0){
				tv_desc.setVisibility(View.VISIBLE);
				tv_desc.setText("（" + bucket.getCount() + "）");
			}else{
				tv_desc.setVisibility(View.GONE);
			}
			if (bucket.getImageList() != null && bucket.getImageList().size() > 0) {
				PhotoUpload photoUpload = bucket.getImageList().get(0);
				if (photoUpload != null) {
					iv_photo.setFadeInDrawables(false);
					iv_photo.requestThumbnail(photoUpload, false);
				}
			} else {
				if(bucket.getDefaultIcon() != null){
					iv_photo.setImageBitmap(bucket.getDefaultIcon());
				}else{
					iv_photo.setImageResource(R.drawable.default_iv_loading);
				}
			}
		}

	}

}
