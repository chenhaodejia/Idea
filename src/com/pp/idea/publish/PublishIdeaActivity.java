package com.pp.idea.publish;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.pp.idea.GlobalParams;
import com.pp.idea.PhotoSelectController;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.mode.Idea;
import com.pp.idea.mode.IdeaExtraContent;
import com.pp.idea.mode.PhotoUpload;
import com.pp.idea.net.IdeaApi;
import com.pp.idea.net.UpyunApi;
import com.pp.idea.photo.SelectPhotoActivity;
import com.pp.idea.utils.EncryptUtils;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.utils.Utils;

public class PublishIdeaActivity extends BaseActivity {

	public static final int SELECT_IMAGE = 1;
	
	@ViewInject(R.id.gv_image)
	private GridView gv_image;
	@ViewInject(R.id.et_title)
	private EditText et_title;
	@ViewInject(R.id.et_content)
	private EditText et_content;

	private PhotoSelectController mPhotoController;

	private ImageAdapter mAdapter;

	private Idea idea;

	private IdeaApi ideaApi;

	private UpyunApi upyunApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_publish_idea);
	}

	@Override
	protected void init() {
		super.init();
		mPhotoController = PhotoSelectController.getFromContext(this);
		//进来创建个空的idea
		idea = new Idea();
		ideaApi = new IdeaApi(this);
		upyunApi = new UpyunApi();
		idea.setExtraContentList(new ArrayList<IdeaExtraContent>());
	}

	@Override
	protected void loadData() {
		super.loadData();
		mAdapter = new ImageAdapter(this, new ArrayList<PhotoUpload>());
		gv_image.setAdapter(mAdapter);
	}

	@OnClick(R.id.title_back)
	public void clickBack(View v){
		super.onBackPressed();
	}
	
	public void clickAddPhoto(){
		Intent intent = new Intent(this, SelectPhotoActivity.class);
		startActivityForResult(intent, SELECT_IMAGE);
	}
	
	@OnClick(R.id.title_publish_idea)
	public void clickPublish(View v){
		String title = et_title.getText().toString();
		String content = et_content.getText().toString();
		if(TextUtils.isEmpty(title)){
			PromptManager.showToast(this, "请输入标题～");
			return ;
		}
		if(TextUtils.isEmpty(content)){
			PromptManager.showToast(this, "请输入点内容吧～");
			return ;
		}
		startUpload(title,content);
	}
	
	private void startUpload(String title, String content) {
		idea.setTitle(title);
		idea.setContent(content);
		idea.setUser(GlobalParams.userInfo);
		idea.setLiked(0);
		idea.setUnliked(0);
		idea.setStatus(0);
		idea.setLookNum(0);
		Utils.executeAsyncTask(new AsyncTask<Void, Void, Void>(){
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				PromptManager.showProgressDialog(PublishIdeaActivity.this, "正在发布，请稍候...");
			}

			@Override
			protected Void doInBackground(Void... params) {
				for (int i = 0; i < idea.getExtraContentList().size(); i++) {
					IdeaExtraContent ideaExtraContent = idea.getExtraContentList().get(i);
					String uploadImage = upyunApi.uploadImage(ideaExtraContent.getLocalImage());
					if(!TextUtils.isEmpty(uploadImage)){
						ideaExtraContent.setImage(uploadImage);
					}
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				ideaApi.addNewIdea(idea, new SaveListener() {
					
					@Override
					public void onSuccess() {
						addDataToUser(idea);
					}
					
					private void addDataToUser(Idea idea) {
						BmobRelation ideas = GlobalParams.userInfo.getIdeas();
						ideas.add(idea);
//						GlobalParams.userInfo.setIdeas(ideas);
						GlobalParams.userInfo.update(PublishIdeaActivity.this,new UpdateListener() {
							
							@Override
							public void onSuccess() {
								PromptManager.showToast(PublishIdeaActivity.this, "发布成功");
								PromptManager.closeProgressDialog();
								PublishIdeaActivity.this.onBackPressed();								
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								PromptManager.showToast(PublishIdeaActivity.this, "发布失败:"+arg1);
								PromptManager.closeProgressDialog();
							}
						});
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						PromptManager.showToast(PublishIdeaActivity.this, "发布失败:"+arg1);
						PromptManager.closeProgressDialog();
					}
				});
			}
			
		});
	}

	@OnItemClick(R.id.gv_image)
	public void clickPicItem(AdapterView<?> parent, View view, int position, long id){
		if(position == mAdapter.getCount()-1){
			clickAddPhoto();
			return ;
		}
		//TODO 浏览其他图片
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case SELECT_IMAGE:
				boolean isAddSuccess = data.getBooleanExtra("isAddSuccess", false);
				if(isAddSuccess){
					List<PhotoUpload> list = new ArrayList<PhotoUpload>();
					for (int i = 0; i < mPhotoController.getSelected().size(); i++) {
						list.add(mPhotoController.getSelected().get(i));
						mPhotoController.addNotSelect(mPhotoController.getSelected().get(i));
						//add Item
						IdeaExtraContent ideaExtraContent = new IdeaExtraContent();
						ideaExtraContent.setLocalImage(mPhotoController.getSelected().get(i).getImagePath());
						ideaExtraContent.setContent(mPhotoController.getSelected().get(i).getDesc());
						ideaExtraContent.setUuid(EncryptUtils.encodeMD5(System.currentTimeMillis()+""+mPhotoController.getSelected().get(i).getImagePath()+mPhotoController.getSelected().get(i).getDesc()));
						idea.getExtraContentList().add(ideaExtraContent);
					}
					mAdapter.getData().addAll(list);
					mAdapter.notifyDataSetChanged();
					mPhotoController.clearSelected();
				}
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
