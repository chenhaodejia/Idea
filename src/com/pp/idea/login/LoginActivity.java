package com.pp.idea.login;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.GlobalParams;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.event.UserLoginEvent;
import com.pp.idea.mode.User;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.utils.Utils;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements PlatformActionListener {

	private static final int USER_REG = 1;
	protected static final int LOGIN_CANCEL = 1;
	protected static final int LOGIN_ERROR = 2;
	protected static final int LOGIN_SUCCESS = 3;

	@ViewInject(R.id.et_email)
	private EditText et_email;
	@ViewInject(R.id.et_password)
	private EditText et_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_login);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void loadData() {
		super.loadData();
	}

	@OnClick(R.id.tv_login)
	public void clickLogin(View v) {
		String email = et_email.getText().toString();
		String password = et_password.getText().toString();
		if (TextUtils.isEmpty(email)) {
			PromptManager.showToast(this, "请输入邮箱");
			et_email.requestFocus();
			return;
		}
		if (TextUtils.isEmpty(password)) {
			PromptManager.showToast(this, "请输入密码");
			et_email.requestFocus();
			return;
		}
		if (!Utils.regexMail(email)) {
			PromptManager.showToast(this, R.string.toast_email_error);
			return;
		}
		login(email, password);
	}

	@OnClick(R.id.tv_reg_user)
	public void clickReg(View v) {
		Intent intent = new Intent(this, UserRegActivity.class);
		startActivityForResult(intent, USER_REG);
	}

	@OnClick(R.id.tv_forget_password)
	public void clickForgetPassword(View v) {
		Intent intent = new Intent(this, ForgertPasswordActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.title_back)
	public void clickBack(View v) {
		onBackPressed();
	}

	@OnClick(R.id.other_login_sina)
	public void clickLoginSina(View v) {
		authorize(new SinaWeibo(this));
	}

	@OnClick(R.id.other_login_qq)
	public void clickLoginQQ(View v) {
		authorize(new QZone(this));
	}

	/**
	 * 验证平台登陆
	 * 
	 * @param plat
	 */
	private void authorize(Platform plat) {
		PromptManager.showProgressDialog(this, "正在登录，请稍候...");
		plat.setPlatformActionListener(this);
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	/**
	 * 
	 * @param email
	 * @param password
	 *            必须是不经过MD5加密的
	 */
	private void login(String email, String password) {
		PromptManager.showProgressDialog(this, getResources().getString(R.string.toast_progress_note, "正在登录"));
		BmobUser user = new BmobUser();
		user.setUsername(email);
		user.setPassword(password);
		loginRemote(user);
	}

	/**
	 * 登录服务器
	 * @param user
	 */
	private void loginRemote(BmobUser user) {
		user.login(this, new SaveListener() {

			@Override
			public void onSuccess() {
				User userInfo = BmobUser.getCurrentUser(LoginActivity.this, User.class);
				loginSuccess(userInfo);
			}

			@Override
			public void onFailure(int code, String msg) {
				switch (code) {
				case 101:
					PromptManager.showToast(LoginActivity.this, "邮箱或密码不正确");
					break;

				default:
					PromptManager.showToast(LoginActivity.this, "登录失败，请重试～");
					break;
				}
				PromptManager.closeProgressDialog();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case USER_REG:// 注册成功返回
				String email = data.getStringExtra("email");
				String password = data.getStringExtra("password");
				login(email, password);
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCancel(Platform arg0, int arg1) {
		handler.sendEmptyMessage(LOGIN_CANCEL);
	}

	@Override
	public void onComplete(Platform plat, int arg1, HashMap<String, Object> userInfo) {
		// handler.sendEmptyMessage(LOGIN_SUCCESS);
		final String avatar;
		final String provider;
		final String openId;
		final String name;
		if (plat.getName().equals(SinaWeibo.NAME)) {
			avatar = userInfo.get("avatar_hd").toString();
			provider = "weibo";
			openId = userInfo.get("id").toString();
			name = userInfo.get("name").toString();
		} else {
			avatar = userInfo.get("figureurl_qq_2").toString();
			provider = "qq";
			openId = plat.getDb().getUserId();
			name = userInfo.get("nickname").toString();
		}
		// 查询是否有此用户
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("username", openId + "_" + provider);
		query.findObjects(this, new FindListener<User>() {

			@Override
			public void onSuccess(List<User> object) {
				if (object == null || object.size() == 0) {
					// 未注册
					User user = new User();
					user.setUsername(openId + "_" + provider);
					user.setPassword(openId + "_" + provider);
					user.setAvatar(avatar);
					user.setOtherName(name);
					user.setStatus(0);
					user.setOpenId(openId);
					user.setSource(provider);
					regNewUser(user);
				} else {
					loginSuccess(object.get(0));
				}
			}

			@Override
			public void onError(int code, String msg) {
				handler.sendEmptyMessage(LOGIN_ERROR);
			}
		});

	}

	/**
	 * 注册新用户
	 * 
	 * @param user
	 */
	protected void regNewUser(final User user) {
		user.signUp(this, new SaveListener() {

			@Override
			public void onSuccess() {
				// 注册成功
				loginRemote(user);
			}

			@Override
			public void onFailure(int code, String msg) {
				switch (code) {
				case 202:// 用户名已存在
					PromptManager.showToast(LoginActivity.this, "邮箱已注册");
					et_email.requestFocus();
					break;
				case 203:// 邮箱已存在
					PromptManager.showToast(LoginActivity.this, "邮箱已注册");
					et_email.requestFocus();
					break;

				default:
					PromptManager.showToast(LoginActivity.this, "注册失败，请重试～");
					break;
				}
				handler.sendEmptyMessage(LOGIN_ERROR);
			}
		});
	}

	protected void loginSuccess(User user) {
		GlobalParams.userInfo = user;
		EventBus.getDefault().post(new UserLoginEvent());
		handler.sendEmptyMessage(LOGIN_SUCCESS);
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		handler.sendEmptyMessage(LOGIN_ERROR);
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOGIN_CANCEL:
				PromptManager.showToast(LoginActivity.this, "登录取消");
				PromptManager.closeProgressDialog();
				break;
			case LOGIN_ERROR:
				PromptManager.showToast(LoginActivity.this, "登录失败，请重试～");
				PromptManager.closeProgressDialog();
				break;
			case LOGIN_SUCCESS:
				PromptManager.showToast(LoginActivity.this, "登录成功");
				PromptManager.closeProgressDialog();
				onBackPressed();
				break;

			default:
				break;
			}
		};
	};

}
