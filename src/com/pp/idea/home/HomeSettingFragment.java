package com.pp.idea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.HomeActivity;
import com.pp.idea.R;
import com.pp.idea.base.BaseFragment;

public class HomeSettingFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.il_home_setting, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@OnClick(R.id.title_menu)
	public void clickMenu(View v) {
		((HomeActivity) getActivity()).openMenu();
	}
}
