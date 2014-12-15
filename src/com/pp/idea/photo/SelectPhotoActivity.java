package com.pp.idea.photo;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.pp.idea.GlobalParams;
import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.mode.MediaStoreBucket;
import com.pp.idea.publish.PublishIdeaActivity;
import com.pp.idea.task.MediaStoreBucketsAsyncTask1;
import com.pp.idea.task.MediaStoreBucketsAsyncTask1.MediaStoreBucketsResultListener;
import com.pp.idea.utils.Utils;

public class SelectPhotoActivity extends BaseActivity implements MediaStoreBucketsResultListener {
	
	@ViewInject(R.id.lv_photo)
	private ListView lv_photo;
	@ViewInject(R.id.iv_loading)
	private ImageView iv_loading;

	private PhotoSelectController mPhotoController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_select_photo);
	}

	@Override
	protected void init() {
		super.init();
		mPhotoController = PhotoSelectController.getFromContext(this);
		mPhotoController.clearSelected();
	}

	@Override
	protected void loadData() {
		super.loadData();
		Utils.startLoading(iv_loading);
		MediaStoreBucketsAsyncTask1.execute(this, MediaStoreBucketsAsyncTask1.STATUS_LOAD_ALL_PHOTO, this);

	}

	@OnClick(R.id.title_back)
	public void clickBack(View v) {
		super.onBackPressed();
	}

	@Override
	public void onBucketsLoaded(List<MediaStoreBucket> buckets) {
		lv_photo.setAdapter(new SelectPhotoAdapter(this, buckets));
		Utils.stopLoading(iv_loading);
	}
	
	@OnItemClick(R.id.lv_photo)
	public void clickPhotoItem(AdapterView<?> parent, View view, int position, long id){
		SelectPhotoAdapter adapter = (SelectPhotoAdapter) lv_photo.getAdapter();
		MediaStoreBucket item = (MediaStoreBucket) adapter.getItem(position);
		
		Intent intent = new Intent();
		intent.setClass(this, SelectPhotoImageActivity.class);
		intent.putExtra(SelectPhotoImageActivity.EXTRA_TITLE, item.getmBucketName());
		GlobalParams.photoList = item.getImageList();
		startActivityForResult(intent,PublishIdeaActivity.SELECT_IMAGE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case PublishIdeaActivity.SELECT_IMAGE:
				boolean isCancel = data.getBooleanExtra("isCancel", false);
				if(isCancel){
					this.finish();
					return ;
				}
				data.putExtra("isAddSuccess", true);
				setResult(Activity.RESULT_OK, data);
				this.finish();
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
