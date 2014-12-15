package com.pp.idea.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.pp.idea.R;
import com.pp.idea.base.BaseFragment;

public class HomeDiscussIdeaFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.il_home_find_idea, null);
		ViewUtils.inject(this, view);
		return view;
	}
}
