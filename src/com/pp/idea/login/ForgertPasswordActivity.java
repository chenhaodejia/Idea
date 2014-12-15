package com.pp.idea.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordListener;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pp.idea.R;
import com.pp.idea.base.BaseActivity;
import com.pp.idea.utils.PromptManager;
import com.pp.idea.utils.Utils;

public class ForgertPasswordActivity extends BaseActivity {
	
	@ViewInject(R.id.et_email)
	private EditText et_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.il_forgert_password);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	protected void loadData() {
		super.loadData();
	}

	@OnClick(R.id.title_back)
	public void clickBack(View v) {
		onBackPressed();
	}
	
	@OnClick(R.id.tv_forgert_password)
	public void clickSendForgertPasswordLink(View v){
		final String email = et_email.getText().toString();
		if(TextUtils.isEmpty(email)){
			PromptManager.showToast(this, "请输入邮箱");
			return ;
		}
		if(!Utils.regexMail(email)){
			PromptManager.showToast(this, R.string.toast_email_error);
			return ;
		}
		//发送重置密码链接
		PromptManager.showProgressDialog(this, getResources().getString(R.string.toast_progress_note, "正在重置"));
		BmobUser.resetPassword(this, email, new ResetPasswordListener() {
		    @Override
		    public void onSuccess() {
		    	PromptManager.showToast(ForgertPasswordActivity.this,"重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
		    	ForgertPasswordActivity.this.onBackPressed();
		    	PromptManager.closeProgressDialog();
		    }
		    @Override
		    public void onFailure(int code, String e) {
		    	PromptManager.showToast(ForgertPasswordActivity.this,"重置密码失败:" + e);
		    	PromptManager.closeProgressDialog();
		    }
		});
	}

	
}
