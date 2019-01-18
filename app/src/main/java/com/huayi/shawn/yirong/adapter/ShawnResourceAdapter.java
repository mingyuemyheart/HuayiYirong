package com.huayi.shawn.yirong.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.List;

/**
 * 资源库-个人库
 */
public class ShawnResourceAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle,tvTime,tvSize;
		ImageView imageView,ivSelected;
	}

	public ShawnResourceAdapter(Context context, List<ShawnDto> mArrayList) {
		this.context = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_resource, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvSize = convertView.findViewById(R.id.tvSize);
			mHolder.ivSelected = convertView.findViewById(R.id.ivSelected);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
		}

		if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mHolder.tvSize.setText("");
		}else {
			mHolder.tvSize.setText(CommonUtil.getFormatSize(dto.fileSize));
		}

		//1图片、2视频、3音频、4文档、5文件夹
		if (TextUtils.equals(dto.fileType, CONST.FILETYPE1)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_image);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_video);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE3)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE4)) {
			String lowCase = dto.filePath.toLowerCase();
			if (lowCase.endsWith(CONST.doc) || lowCase.endsWith(CONST.docx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_word);
			}else if (lowCase.endsWith(CONST.ppt) || lowCase.endsWith(CONST.pptx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_ppt);
			}else if (lowCase.endsWith(CONST.pdf)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_pdf);
			}else if (lowCase.endsWith(CONST.xls) || lowCase.endsWith(CONST.xlsx)) {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_xls);
			}else {
				mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
			}
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_files);
		}else {
			mHolder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}

		if (TextUtils.equals(dto.title, CONST.lastFile)) {
			mHolder.ivSelected.setVisibility(View.INVISIBLE);
		}else {
			mHolder.ivSelected.setVisibility(View.VISIBLE);
			if (dto.isSelected) {
				mHolder.ivSelected.setImageResource(R.drawable.shawn_icon_selected);
			}else {
				mHolder.ivSelected.setImageResource(R.drawable.shawn_icon_unselected);
			}
		}

		mHolder.ivSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dto.isSelected = !dto.isSelected;
				notifyDataSetChanged();

				//发布广播，更新资源库底部操作按钮状态
				Intent intent = new Intent();
				intent.setAction(CONST.BROADCAST_CONTROL);
				context.sendBroadcast(intent);
			}
		});

		return convertView;
	}

}
