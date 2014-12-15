package com.pp.idea.home;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.GlobalParams;
import com.pp.idea.HomeActivity;
import com.pp.idea.R;
import com.pp.idea.base.BaseFragment;
import com.pp.idea.findIdea.IdeaAdapter;
import com.pp.idea.mode.Idea;
import com.pp.idea.publish.PublishIdeaActivity;
import com.pp.idea.utils.PromptManager;

public class HomePublishIdeaFragment extends BaseFragment {

	private static final int MAX_PAGE_SIZE = 10;
	
	@ViewInject(R.id.lv_idea)
	private ListView lv_idea;
	
	private int currentPage = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.il_home_my_idea, null);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		loadData();
	}
	
	public void loadData() {
		loadFristData();
	}

	private void loadFristData() {
		loadRemoteData(1);
	}
	
	private void loadNextPageData(){
		loadRemoteData(currentPage +1);
	}
	
	private void loadRemoteData(int page){
		currentPage = page;
		BmobQuery<Idea> query = new BmobQuery<Idea>();
		query.addWhereRelatedTo("ideas", new BmobPointer(GlobalParams.userInfo));
		query.setLimit(MAX_PAGE_SIZE);
		query.setSkip((page-1) * MAX_PAGE_SIZE);
		query.order("-createdAt");
		query.findObjects(getActivity(), new FindListener<Idea>() {
			
			@Override
			public void onSuccess(List<Idea> data) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setUser(GlobalParams.userInfo);
				}
				lv_idea.setAdapter(new IdeaAdapter(getActivity(), data));
				PromptManager.closeProgressDialog();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				PromptManager.showToast(getActivity(), "网络异常，请重试～");
				PromptManager.closeProgressDialog();
			}
		});
	}

	@OnClick(R.id.title_publish_idea)
	public void clickPublishIdea(View v){
		Intent intent = new Intent(getActivity(),PublishIdeaActivity.class);
		startActivity(intent);
	}
	
	@OnClick(R.id.title_menu)
	public void clickMenu(View v){
		((HomeActivity)getActivity()).openMenu();
	}
	
}
