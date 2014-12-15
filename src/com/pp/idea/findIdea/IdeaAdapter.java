package com.pp.idea.findIdea;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.pp.idea.R;
import com.pp.idea.mode.Idea;
import com.pp.idea.net.base.RequestManager;
import com.pp.idea.utils.Utils;

public class IdeaAdapter extends BaseAdapter {

	private Context context;
	private List<Idea> data;
	private LayoutInflater mInflater;
	private BitmapDrawable defaultImageDrawable;

	public IdeaAdapter(Context context, List<Idea> data) {
		this.context = context;
		this.data = data;
		mInflater = LayoutInflater.from(context);
		defaultImageDrawable = new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(), R.drawable.detail_portrait_default));
	}

	@Override
	public int getCount() {
		// return data.size();
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.il_idea_item, null);
		}
		Idea idea = data.get(position);
		ViewHolder holder = getHolder(convertView);
		holder.build(idea);
		return convertView;
	}

	private ViewHolder getHolder(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder == null) {
			holder = new ViewHolder(view);
			view.setTag(holder);
		}
		return holder;
	}

	class ViewHolder implements OnClickListener {

		private ImageView iv_avatar;
		private TextView tv_name;
		private TextView tv_title;
		private TextView tv_content;
		private TextView tv_date;
		private TextView tv_lignt_num;
		private TextView tv_comment_num;
		private GridView gv_pic;
		private View tv_operator_support;
		private View tv_operator_unsupport;
		private View tv_operator_comment;
		private View tv_operator_more;
		private ImageContainer ic_avatat;

		public ViewHolder(View view) {
			iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			tv_title = (TextView) view.findViewById(R.id.tv_title);
			tv_content = (TextView) view.findViewById(R.id.tv_content);
			tv_date = (TextView) view.findViewById(R.id.tv_date);
			tv_lignt_num = (TextView) view.findViewById(R.id.tv_lignt_num);
			tv_comment_num = (TextView) view.findViewById(R.id.tv_comment_num);
			gv_pic = (GridView) view.findViewById(R.id.gv_pic);
			tv_operator_support = view.findViewById(R.id.tv_operator_support);
			tv_operator_unsupport = view.findViewById(R.id.tv_operator_unsupport);
			tv_operator_comment = view.findViewById(R.id.tv_operator_comment);
			tv_operator_more = view.findViewById(R.id.tv_operator_more);
		}

		public void build(Idea idea) {
			setData(idea);
			if(ic_avatat != null){
				ic_avatat.cancelRequest();
			}
			ic_avatat = RequestManager.loadImage(idea.getUser().getAvatar(), RequestManager.getImageListener(iv_avatar, defaultImageDrawable, defaultImageDrawable));
			gv_pic.setAdapter(new IdeaPicAdapter(context,idea.getExtraContentList()));
			
			tv_operator_support.setOnClickListener(this);
			tv_operator_unsupport.setOnClickListener(this);
			tv_operator_comment.setOnClickListener(this);
			tv_operator_more.setOnClickListener(this);
		}

		private void setData(Idea idea) {
			if(TextUtils.isEmpty(idea.getUser().getOtherName())){
				tv_name.setText(idea.getUser().getUsername());
			}else{
				tv_name.setText(idea.getUser().getOtherName());
			}
			tv_title.setText(idea.getTitle());
			tv_content.setText(idea.getContent());
			String time = getCurrentTimeDt(idea.getCreatedAt());
			tv_date.setText(time);
			tv_lignt_num.setText((idea.getLiked()+new Random().nextInt(100))+"人赞");
			tv_comment_num.setText((idea.getLookNum()+new Random().nextInt(100))+"条评论");
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tv_operator_support:
				if(tv_operator_support.isSelected()){
					return ;
				}
				tv_operator_support.setSelected(true);
				tv_operator_unsupport.setSelected(false);
				break;
			case R.id.tv_operator_unsupport:
				if(tv_operator_unsupport.isSelected()){
					return ;
				}
				tv_operator_unsupport.setSelected(true);
				tv_operator_support.setSelected(false);
				break;
			case R.id.tv_operator_comment:

				break;
			case R.id.tv_operator_more:
				
				break;

			default:
				break;
			}
		}

	}

	public String getCurrentTimeDt(String createdAt) {
		long currentTime = System.currentTimeMillis();
		long createTime = Utils.paserTime(createdAt);
		long dt = currentTime - createTime;
		String returnTime = "";
		if(dt < 1000 * 60){//<1分钟
			returnTime = "刚刚";
		}else if(dt < 1000 * 60 * 60){//小于1小时
			returnTime = (dt/1000/60)+"分钟前";
		}else if(dt < 1000 * 60 * 60 *24){//小于24小时
			returnTime = (dt/1000/60/60)+"小时前";
		}else{
			returnTime = "1天前";
		}
		return returnTime;
	}

}
