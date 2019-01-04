package com.huayi.shawn.yirong.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.List;

/**
 * 主界面
 */
public class ShawnMainAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;
	private int width;

	private final class ViewHolder{
		TextView tvTitle;
		ImageView imageView;
	}

	public ShawnMainAdapter(Context context, List<ShawnDto> mArrayList) {
		this.context = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_main, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}

		if (TextUtils.equals(dto.title, "资源库")) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_resource);
		}else if (TextUtils.equals(dto.title, "稿件")) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_gaojian);
		}else if (TextUtils.equals(dto.title, "预警")) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_warning);
		}else if (TextUtils.equals(dto.title, "服务提示")) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_service);
		}else if (TextUtils.equals(dto.title, "设置")) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_setting);
		}

		AbsListView.LayoutParams params = new AbsListView.LayoutParams(width-(int)CommonUtil.dip2px(context, 10), width/5);
		convertView.setLayoutParams(params);

		return convertView;
	}

}
