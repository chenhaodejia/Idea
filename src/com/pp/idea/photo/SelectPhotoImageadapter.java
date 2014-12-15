package com.pp.idea.photo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;

import com.pp.idea.MyApplication;
import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.view.PhotoItemLayout;
import com.pp.idea.view.PhotupImageView;

public class SelectPhotoImageadapter extends BaseAdapter {

	private Context context;
	private List<PhotoUpload> list;
	private LayoutInflater mInflater;
	private PhotoSelectController mController;

	public SelectPhotoImageadapter(Context context, List<PhotoUpload> list) {
		this.context = context;
		this.list = list;
		mInflater = LayoutInflater.from(context);
		mController = MyApplication.getApplication(context).getPhotoSelectController();
	}

	@Override
	public int getCount() {
		return list == null?0:list.size();
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
			convertView = mInflater.inflate(R.layout.il_item_grid_photo_user, null);
		}

		ViewHolder holder = getHolder(convertView);
		holder.build(list.get(position));

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
		private PhotoItemLayout layout;
		private PhotupImageView iv;

		public ViewHolder(View view) {
			layout = (PhotoItemLayout) view;
			iv = layout.getImageView();
			
			iv.setDefaultImage(R.drawable.default_iv_loading);
		}

		public void build(PhotoUpload upload) {
			if (null != upload) {
				iv.setFadeInDrawables(false);
				iv.requestThumbnail(upload, false);
				layout.setPhotoSelection(upload);

				if (null != mController) {
					layout.setMEnable(!mController.isNotSelected(upload));
					// layout.setClickable(!mController.isNotSelected(upload));
					((Checkable) layout).setChecked(mController.isSelected(upload));
				}
			}
		}
	}

}
