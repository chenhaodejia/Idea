package com.pp.idea;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.BmobUser;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.nineoldandroids.view.ViewHelper;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.event.UserLoginEvent;
import com.pp.idea.home.HomeFragmentAdapter;
import com.pp.idea.login.LoginActivity;
import com.pp.idea.net.base.RequestManager;
import com.pp.idea.utils.BitmapLruCache;
import com.pp.idea.utils.DragUtil;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.view.DragLayout;
import com.pp.idea.view.DragLayout.DragListener;
import com.pp.idea.view.DragLayout.Status;

import de.greenrobot.event.EventBus;

public class HomeActivity extends BaseActivity {

	@ViewInject(R.id.dl)
	private DragLayout dl;
	@ViewInject(R.id.container)
	private FrameLayout container;
	@ViewInject(R.id.tv_user_name)
	private TextView tv_username;
	@ViewInject(R.id.iv_avatar)
	private ImageView iv_avatar;

	private int currentPosition;
	private HomeFragmentAdapter homeFragmentAdapter;
	private long tempTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DragUtil.initImageLoader(this);
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(UserLoginEvent event) {
		setUserInfo();
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_home);
	}

	@Override
	protected void init() {
		super.init();
		initParams();
		homeFragmentAdapter = new HomeFragmentAdapter(getFragmentManager());
		initDragLayout();
		initView();
		setUserInfo();
	}

	private void initParams() {
		tv_username.setMaxWidth(GlobalParams.screenWidth / 3);
	}

	@Override
	protected void loadData() {
		super.loadData();
		switchFragment(Configure.HOME_FIND_IDEA);
	}

	private void setUserInfo() {
		BitmapDrawable defaultImageDrawable = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.detail_portrait_default));
		if (GlobalParams.userInfo != null) {
			if (TextUtils.isEmpty(GlobalParams.userInfo.getOtherName())) {
				tv_username.setText(GlobalParams.userInfo.getUsername());
			} else {
				tv_username.setText(GlobalParams.userInfo.getOtherName());
			}
			RequestManager.loadImage(GlobalParams.userInfo.getAvatar(), RequestManager.getImageListener(iv_avatar, defaultImageDrawable, defaultImageDrawable));
		} else {
			tv_username.setText("登录");
			iv_avatar.setImageDrawable(defaultImageDrawable);
		}
	}

	private void initDragLayout() {
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				// lv.smoothScrollToPosition(new Random().nextInt(30));
			}

			@Override
			public void onClose() {
			}

			@Override
			public void onDrag(float percent) {
				animate(percent);
			}
		});
	}

	private void animate(float percent) {
		ViewGroup vg_left = dl.getVg_left();
		ViewGroup vg_main = dl.getVg_main();

		float f1 = 1 - percent * 0.3f;
		ViewHelper.setScaleX(vg_main, f1);
		ViewHelper.setScaleY(vg_main, f1);
		ViewHelper.setTranslationX(vg_left, -vg_left.getWidth() / 2.2f + vg_left.getWidth() / 2.2f * percent);
		ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setAlpha(vg_left, percent);
		// ViewHelper.setAlpha(iv_icon, 1 - percent);

		int color = (Integer) DragUtil.evaluate(percent, Color.parseColor("#ff000000"), Color.parseColor("#00000000"));
		dl.getBackground().setColorFilter(color, Mode.SRC_OVER);
	}

	private void initView() {
		// iv_icon = (ImageView) findViewById(R.id.iv_icon);
		// iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
		// gv_img = (GridView) findViewById(R.id.gv_img);
		// tv_noimg = (TextView) findViewById(R.id.iv_noimg);
		// gv_img.setFastScrollEnabled(true);
		// adapter = new ImageAdapter(this);
		// gv_img.setAdapter(adapter);
		// gv_img.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// Intent intent = new Intent(MainActivity.this,
		// ImageActivity.class);
		// intent.putExtra("path", adapter.getItem(position));
		// startActivity(intent);
		// }
		// });
		// lv = (ListView) findViewById(R.id.lv);
		// lv.setAdapter(new ArrayAdapter<String>(MainActivity.this,
		// R.layout.item_text, new String[] { "NewBee", "ViCi Gaming",
		// "Evil Geniuses", "Team DK", "Invictus Gaming", "LGD",
		// "Natus Vincere", "Team Empire", "Alliance", "Cloud9",
		// "Titan", "Mousesports", "Fnatic", "Team Liquid",
		// "MVP Phoenix", "NewBee", "ViCi Gaming",
		// "Evil Geniuses", "Team DK", "Invictus Gaming", "LGD",
		// "Natus Vincere", "Team Empire", "Alliance", "Cloud9",
		// "Titan", "Mousesports", "Fnatic", "Team Liquid",
		// "MVP Phoenix" }));
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1,
		// int position, long arg3) {
		// Toast.makeText(getApplicationContext(), "click " + position,
		// Toast.LENGTH_LONG).show();
		// }
		// });
		// iv_icon.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// dl.open();
		// }
		// });
	}

	public void switchFragment(final int position) {
		if (currentPosition != 0 && currentPosition == position) {
			closeMenu();
			return;
		}
		currentPosition = position;
		Fragment fragment = homeFragmentAdapter.instantiateItem(R.id.container, position);
		homeFragmentAdapter.setPrimaryItem(container, position, fragment);
		homeFragmentAdapter.finishUpdate(container);
		closeMenu();
	}

	@OnClick(R.id.menu_find)
	public void clickMenuFindIdea(View v) {
		switchFragment(Configure.HOME_FIND_IDEA);
	}
	
	@OnClick(R.id.menu_collect)
	public void clickMenuCollectIdea(View v) {
		switchFragment(Configure.HOME_COLLECT_IDEA);
	}
	
	@OnClick(R.id.menu_setting)
	public void clickMenuSetting(View v) {
		switchFragment(Configure.HOME_SETTING);
	}
	
	@OnClick(R.id.menu_tuijian)
	public void clickMenuTuijian(View v) {
		switchFragment(Configure.HOME_TUIJIAN);
	}

	@OnClick(R.id.menu_my_idea)
	public void clickMenuMyIdea(View v) {
		if(GlobalParams.userInfo == null){
			PromptManager.showToast(this, R.string.toast_no_login);
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return ;
		}
		switchFragment(Configure.HOME_PUBLISH_IDEA);
	}

	@OnClick(R.id.rl_user)
	public void clickUser(View v) {
		if (GlobalParams.userInfo == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		} else {
			logout();
		}
	}

	public void logout() {
		PromptManager.showProgressDialog(this, getResources().getString(R.string.toast_progress_note, "正在注销"));
		BmobUser.logOut(this);
		GlobalParams.userInfo = null;
		EventBus.getDefault().post(new UserLoginEvent());
		//
		PromptManager.showToast(this, "注销成功");
		PromptManager.closeProgressDialog();
	}

	public void closeMenu() {
		if (dl.getStatus() == Status.Close) {
			return;
		}
		dl.close();
	}

	public void openMenu() {
		if (dl.getStatus() == Status.Open) {
			return;
		}
		dl.open();
	}

	public void swtichMenu() {
		if (dl.getStatus() == Status.Open) {
			dl.close();
		} else if (dl.getStatus() == Status.Close) {
			dl.open();
		}
	}

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - tempTime > 2000) {
			PromptManager.showToast(this, "再按一次退出程序");
			tempTime = System.currentTimeMillis();
			return;
		} else {
			exitApp();
		}
		tempTime = System.currentTimeMillis();
	}

	private void exitApp() {
		BitmapLruCache.getInstance(this).clear();
		this.finish();
	}
}
