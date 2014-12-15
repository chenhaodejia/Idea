package com.pp.idea.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.utils.EncryptUtils;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.utils.Utils;

public class UserRegActivity extends BaseActivity {
	
	@ViewInject(R.id.et_email)
	private EditText et_email;
	@ViewInject(R.id.et_password)
	private EditText et_password;
	@ViewInject(R.id.et_password_again)
	private EditText et_password_again;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_user_reg);
	}

	@Override
	protected void init() {
		super.init();
	}
	
	@OnClick(R.id.tv_user_reg)
	public void clickUserReg(View v){
		String email = et_email.getText().toString();
		String password = et_password.getText().toString();
		String passwordAgain = et_password_again.getText().toString();
		if(!authInput(email,password,passwordAgain)){
			return ;
		}
		regNewUser(email,password);
	}

	@OnClick(R.id.title_back)
	public void clickTitleBack(View v){
		onBackPressed();
	}
	
	/**
	 * 注册新用户
	 * 
	 * @param email
	 * @param password
	 */
	private void regNewUser(final String email, String password) {
		//mad5加密
		final String madPassword = password;
		BmobUser user = new BmobUser();
		user.setUsername(email);
		user.setPassword(madPassword);
		user.setEmail(email);
		
		//start reg
		PromptManager.showProgressDialog(this, getResources().getString(R.string.toast_progress_note, "正在注册"));
		user.signUp(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				PromptManager.closeProgressDialog();
				PromptManager.showToast(UserRegActivity.this, "注册成功");
				Intent data = new Intent();
				data.putExtra("email", email);
				data.putExtra("password", madPassword);
				setResult(Activity.RESULT_OK,data);
				UserRegActivity.this.finish();
			}
			
			@Override
			public void onFailure(int code, String msg) {
				switch (code) {
				case 202://用户名已存在
					PromptManager.showToast(UserRegActivity.this, "邮箱已注册");
					et_email.requestFocus();
					break;
				case 203://邮箱已存在
					PromptManager.showToast(UserRegActivity.this, "邮箱已注册");
					et_email.requestFocus();
					break;

				default:
					PromptManager.showToast(UserRegActivity.this, "注册失败，请重试～");
					break;
				}
				PromptManager.closeProgressDialog();
			}
		});
	}

	/**
	 * 验证输入的有效性
	 * 
	 * @param email
	 * @param password
	 * @param passwordAgain
	 * @return
	 */
	private boolean authInput(String email, String password, String passwordAgain) {
		if(TextUtils.isEmpty(email)){
			PromptManager.showToast(this, "请输入邮箱");
			et_email.requestFocus();
			return false;
		}
		if(TextUtils.isEmpty(password)){
			PromptManager.showToast(this, "请输入密码");
			et_password.requestFocus();
			return false;
		}
		if(TextUtils.isEmpty(passwordAgain)){
			PromptManager.showToast(this, "请再次输入密码");
			et_password_again.requestFocus();
			return false;
		}
		if(!Utils.regexMail(email)){
			PromptManager.showToast(this, R.string.toast_email_error);
			et_email.requestFocus();
			return false;
		}
		if(!TextUtils.equals(password, passwordAgain)){
			PromptManager.showToast(this, "两次密码不一致，请检查");
			return false;
		}
		return true;
	}
	
}
