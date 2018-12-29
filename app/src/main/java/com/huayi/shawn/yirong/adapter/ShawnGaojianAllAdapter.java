package com.huayi.shawn.yirong.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.dto.ShawnDto;

import java.util.List;

/**
 * 稿件-全部稿件
 */
public class ShawnGaojianAllAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle,tvContent,tvTime,tvState;
		ImageView ivSource;
	}

	public ShawnGaojianAllAdapter(Activity activity, List<ShawnDto> mArrayList) {
		this.activity = activity;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_gaojian_all, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvState = convertView.findViewById(R.id.tvState);
			mHolder.ivSource = convertView.findViewById(R.id.ivSource);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}

		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
		}

		if (TextUtils.equals(dto.source, "微信")) {
			mHolder.ivSource.setImageResource(R.drawable.shawn_icon_wx);
		}else if (TextUtils.equals(dto.source, "微博")) {
			mHolder.ivSource.setImageResource(R.drawable.shawn_icon_weibo);
		}

		if (!TextUtils.isEmpty(dto.state)) {
			if (TextUtils.equals(dto.state, "待审核")) {
				mHolder.tvState.setTextColor(0xff628BF0);
			}else if (TextUtils.equals(dto.state, "待二审")) {
				mHolder.tvState.setTextColor(activity.getResources().getColor(R.color.red));
			}else if (TextUtils.equals(dto.state, "待提交")) {
				mHolder.tvState.setTextColor(0xff63B100);
			}else if (TextUtils.equals(dto.state, "发布成功")) {
				mHolder.tvState.setTextColor(activity.getResources().getColor(R.color.text_color4));
			}
			mHolder.tvState.setText(dto.state);
		}

		return convertView;
	}

}
