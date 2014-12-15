package com.pp.idea.net;

import android.content.Context;

import cn.bmob.v3.listener.SaveListener;

import com.pp.idea.Configure;
import com.pp.idea.R.id;
import com.pp.idea.mode.Idea;

public class IdeaApi {

	private Context context;

	public IdeaApi(Context context){
		this.context = context;
	}
	
	public void addNewIdea(Idea idea, SaveListener listener){
		idea.save(context, listener);
	}
}
