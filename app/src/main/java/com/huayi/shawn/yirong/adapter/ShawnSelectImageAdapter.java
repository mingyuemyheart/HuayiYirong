package com.huayi.shawn.yirong.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * 获取本地所有图片
 */
public class ShawnSelectImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;
	private RelativeLayout.LayoutParams params;

	private final class ViewHolder{
		ImageView imageView,imageView1;
	}

	public ShawnSelectImageAdapter(Context context, List<ShawnDto> mArrayList) {
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		params = new RelativeLayout.LayoutParams(width/4, width/4);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_select_image, null);
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.imageView1 = convertView.findViewById(R.id.imageView1);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		mHolder.imageView1.setTag(position);
		
		final ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.filePath)) {
			File file = new File(dto.filePath);
			if (file.exists()) {
				Picasso.get().load(file).centerCrop().resize(200, 200).into(mHolder.imageView);
				mHolder.imageView.setLayoutParams(params);
			}
		}
		
		if (dto.isSelected) {
			mHolder.imageView1.setImageResource(R.drawable.shawn_icon_selected);
		}else {
			mHolder.imageView1.setImageResource(R.drawable.shawn_icon_unselected);
		}

		mHolder.imageView1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dto.isSelected = !dto.isSelected;
				notifyDataSetChanged();
			}
		});
		
		return convertView;
	}

}
