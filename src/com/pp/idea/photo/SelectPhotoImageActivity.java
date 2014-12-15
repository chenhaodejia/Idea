package com.pp.idea.photo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.Configure;
import com.pp.idea.GlobalParams;
import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.event.SelectPhotoEvent;
import com.pp.idea.mode.IdeaPic;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.photo.show.ShowPhotoActivity;
import com.pp.idea.publish.PublishIdeaActivity;
import com.pp.idea.utils.PromptManager;

import de.greenrobot.event.EventBus;

public class SelectPhotoImageActivity extends BaseActivity {

	public static final String EXTRA_TITLE = "extra_title";
	
	@ViewInject(R.id.gv_photos)
	private GridView gv_photos;
	@ViewInject(R.id.photo_look)
	private RelativeLayout photo_look;
	@ViewInject(R.id.photo_sure)
	private RelativeLayout photo_sure;
	@ViewInject(R.id.tv_photo_sure)
	private TextView tv_photo_sure;
	@ViewInject(R.id.title_text)
	private TextView title_text;

	private List<PhotoUpload> photos;
	private PhotoSelectController mPhotoController;
	private String extra_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		photos = GlobalParams.photoList;
		Intent intent = getIntent();
		extra_title = intent.getStringExtra(EXTRA_TITLE);
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(SelectPhotoEvent event){
		if(mPhotoController.getSelected().size() > 0){
			showOperator(true);
		}else{
			showOperator(false);
		}
		SelectPhotoImageadapter adapter = (SelectPhotoImageadapter) gv_photos.getAdapter();
		adapter.notifyDataSetChanged();
	}
	
	private void showOperator(boolean flag) {
		if(flag){
			tv_photo_sure.setText("确定("+(mPhotoController.getSelected().size()+mPhotoController.getNotSeleteList().size())+")");
		}else{
			if(mPhotoController.getNotSeleteList().size()>0){
				tv_photo_sure.setText("确定("+mPhotoController.getNotSeleteList().size()+")");
			}else{
				tv_photo_sure.setText("确定");
			}
		}
		photo_look.setSelected(flag);
		photo_sure.setSelected(flag);		
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_select_photo_image);
	}

	@Override
	protected void init() {
		super.init();
		mPhotoController = PhotoSelectController.getFromContext(this);
		if(mPhotoController.getSelected().size() > 0){
			showOperator(true);
		}else{
			showOperator(false);
		}
		title_text.setText(extra_title);
	}

	@Override
	protected void loadData() {
		super.loadData();
		gv_photos.setAdapter(new SelectPhotoImageadapter(this, photos));
	}

	@OnClick(R.id.title_back)
	public void clickBack(View v) {
		super.onBackPressed();
	}

	@OnClick(R.id.title_cancel)
	public void clickCancel(View v) {
		Intent data = new Intent();
		data.putExtra("isCancel", true);
		setResult(Activity.RESULT_OK, data);
		super.onBackPressed();
	}
	
	@OnClick(R.id.photo_look)
	public void clickPhotoLook(View v){
		if(!photo_look.isSelected()){
			return ;
		}
		
		Intent intent = new Intent(this, ShowPhotoActivity.class);
		startActivityForResult(intent, PublishIdeaActivity.SELECT_IMAGE);
	}
	
	@OnClick(R.id.photo_sure)
	public void clickPhotoSure(View v){
		if(!photo_sure.isSelected()){
			return ;
		}
		int count = mPhotoController.getSelected().size()+mPhotoController.getNotSeleteList().size();
		if(count > Configure.MAX_ADD_PHOTO){
			PromptManager.showToast(this, getResources().getString(R.string.toast_over_max_photo,Configure.MAX_ADD_PHOTO+""));
			return ;
		}
		Intent data = new Intent();
		setResult(Activity.RESULT_OK,data);
		onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case PublishIdeaActivity.SELECT_IMAGE:
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
