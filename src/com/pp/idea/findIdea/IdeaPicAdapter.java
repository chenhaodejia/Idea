package com.pp.idea.findIdea;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.pp.idea.Configure;
import com.pp.idea.GlobalParams;
import com.pp.idea.R;
import com.pp.idea.mode.IdeaExtraContent;
import com.pp.idea.net.base.RequestManager;

public class IdeaPicAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<IdeaExtraContent> data;
	private BitmapDrawable defaultImageDrawable;

	public IdeaPicAdapter(Context context, List<IdeaExtraContent> data) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.data = data;
		defaultImageDrawable = new BitmapDrawable(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_iv_loading));
	}

	@Override
	public int getCount() {
		return data == null?0:data.size();
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
			convertView = mInflater.inflate(R.layout.il_idea_item_pic_item,
					null);
		}
		IdeaExtraContent ideaExtraContent = data.get(position);
		ViewHolder holder = getHolder(convertView);
		holder.build(ideaExtraContent);
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

	class ViewHolder {

		private ImageView iv;
		private ImageContainer ic_pic;

		public ViewHolder(View view) {
			int item = 4;
			iv = (ImageView) view.findViewById(R.id.iv);

			int dimen_10 = mContext.getResources().getDimensionPixelOffset(
					R.dimen.idea_item_margin);
			int dimen_5 = mContext.getResources().getDimensionPixelOffset(
					R.dimen.dimen_3);
			int avatar_width = mContext.getResources().getDimensionPixelOffset(
					R.dimen.item_avatar_width);
			
			int width = GlobalParams.screenWidth - (dimen_10 * 2) - (dimen_5 * (item - 1)) - avatar_width;
			LayoutParams params = iv.getLayoutParams();
			params.width = width / item;
			params.height = width / item;
		}

		public void build(IdeaExtraContent ideaExtraContent) {
			if(ic_pic != null){
				ic_pic.cancelRequest();
			}
			ic_pic = RequestManager.loadImage(ideaExtraContent.getImage()+Configure.PHOTO_SMALL, RequestManager.getImageListener(iv, defaultImageDrawable, defaultImageDrawable));
		}

	}

}
