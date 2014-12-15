package com.pp.idea.home;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import com.pp.idea.Configure;

public class HomeFragmentAdapter {

	private static final String TAG = "HomeFragmentAdapter";
	private static final boolean DEBUG = true;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mCurTransaction;
	private Fragment mCurrentPrimaryItem;

	public HomeFragmentAdapter(FragmentManager fm) {
		this.mFragmentManager = fm;
	}

	public Fragment getItem(int position) {
		Fragment ft = null;
		switch (position) {
		case Configure.HOME_FIND_IDEA:
			ft = new HomeFindIdeaFragment();
			break;
		case Configure.HOME_DISCUSS_IDEA:
			ft = new HomeDiscussIdeaFragment();
			break;
		case Configure.HOME_PUBLISH_IDEA:
			ft = new HomePublishIdeaFragment();
			break;
		case Configure.HOME_SETTING:
			ft = new HomeSettingFragment();
			break;
		case Configure.HOME_COLLECT_IDEA:
			ft = new HomeCollectIdeaFragment();
			break;
		case Configure.HOME_TUIJIAN:
			ft = new HomeTuijianFragment();
			break;
		}
		return ft;
	}

	public int getCount() {
		return 5;
	}

	public String makeFragmentName(int id, long itemId) {

		return id + "" + itemId;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	public Fragment instantiateItem(int id, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}

		final long itemId = getItemId(position);

		// Do we already have this fragment?
		String name = makeFragmentName(id, itemId);
		Fragment fragment = mFragmentManager.findFragmentByTag(name);
		if (fragment != null) {
			if (DEBUG)
				Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
			mCurTransaction.attach(fragment);
		} else {
			fragment = getItem(position);
			if (DEBUG)
				Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
			mCurTransaction.add(id, fragment, makeFragmentName(id, itemId));
		}
		if (fragment != mCurrentPrimaryItem) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		return fragment;
	}

	private long getItemId(int position) {
		
		return position;
	}

	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = mFragmentManager.beginTransaction();
		}
		if (DEBUG)
			Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object + " v=" + ((Fragment) object).getView());
		mCurTransaction.detach((Fragment) object);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			mCurrentPrimaryItem = fragment;
		}
	}

	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
//			mFragmentManager.executePendingTransactions();
		}
	}

}
