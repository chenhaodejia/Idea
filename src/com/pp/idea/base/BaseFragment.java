package com.pp.idea.base;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;

import com.pp.idea.R;

/**
 * 请在onStart方法中调用{@link setUmengPageName()}设置页面返回路径功能的名称(友盟统计)
 * 
 * @author pphdsny
 * 
 */
public class BaseFragment extends Fragment implements NetErrorListener{

	private String umengPageName;

	public void setUmengPageName(String umengPageName) {
		this.umengPageName = umengPageName;
	}

	@Override
	public void onResume() {
		super.onResume();
//		MobclickAgent.onPageStart(this.getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPageEnd(this.getClass().getName());
	}

	@Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) { 
		super.startActivityForResult(intent, requestCode);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.scale_small);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.scale_small);
	}
	
	@Override
	public void netErrorListener() {
		// TODO Auto-generated method stub
		
	}
}
