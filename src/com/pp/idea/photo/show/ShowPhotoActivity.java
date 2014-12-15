package com.pp.idea.photo.show;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.Configure;
import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.event.SelectPhotoEvent;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.view.JazzyViewPager;
import com.pp.idea.view.JazzyViewPager.TransitionEffect;

import de.greenrobot.event.EventBus;

public class ShowPhotoActivity extends BaseActivity {

	@ViewInject(R.id.vp_photo)
	private JazzyViewPager mPager;
	@ViewInject(R.id.photo_sure)
	private RelativeLayout photo_sure;
	@ViewInject(R.id.tv_photo_sure)
	private TextView tv_photo_sure;
	@ViewInject(R.id.title_select)
	private View title_select;
	
	private PhotoSelectController mPhotoController;
	private ArrayList<PhotoUpload> selectedList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		photo_sure.setSelected(flag);		
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_show_photo);
	}

	@Override
	protected void init() {
		super.init();
		mPhotoController = PhotoSelectController.getFromContext(this);
		initData();
		setupJazzyPager(TransitionEffect.Stack);
		if(mPhotoController.getSelected().size() > 0){
			showOperator(true);
		}else{
			showOperator(false);
		}
		title_select.setSelected(true);
	}
	
	private void initData() {
		selectedList = new ArrayList<PhotoUpload>();
		for (int i = 0; i < mPhotoController.getSelected().size(); i++) {
			selectedList.add(mPhotoController.getSelected().get(i));
		}
	}

	protected void setupJazzyPager(TransitionEffect effect) {
		mPager.setTransitionEffect(effect);
		mPager.setPageMargin(0);

		mPager.setAdapter(new ShowPhotoAdapter(this, selectedList,mPhotoController));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if(mPhotoController.getSelected().contains(selectedList.get(position))){
					title_select.setSelected(true);
				}else{
					title_select.setSelected(false);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}


	@Override
	protected void loadData() {
		super.loadData();
	}

	@OnClick(R.id.title_back)
	public void clickBack(View v) {
		super.onBackPressed();
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
	
	@OnClick(R.id.title_select)
	public void clickTitleSelect(View v){
		ShowPhotoAdapter adapter = (ShowPhotoAdapter) mPager.getAdapter();
		List<PhotoUpload> data = adapter.getData();
		PhotoUpload photoUpload = data.get(mPager.getCurrentItem());
		if(title_select.isSelected()){
			mPhotoController.getSelected().remove(selectedList.get(mPager.getCurrentItem()));
			title_select.setSelected(false);
			photoUpload.setDesc("");
		}else{
			mPhotoController.getSelected().add(selectedList.get(mPager.getCurrentItem()));
			title_select.setSelected(true);
			EditText et_content_desc = (EditText) mPager.findViewWithTag("content_desc_"+mPager.getCurrentItem());
			if(et_content_desc != null){
				photoUpload.setDesc(et_content_desc.getText().toString());
			}
		}
		EventBus.getDefault().post(new SelectPhotoEvent());
	}
}
