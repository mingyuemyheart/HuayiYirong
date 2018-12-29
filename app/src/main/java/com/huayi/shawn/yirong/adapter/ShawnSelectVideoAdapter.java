package com.huayi.shawn.yirong.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
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
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.List;

/**
 * 获取本地所有图片
 */
public class ShawnSelectVideoAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;
	private int width;
	private RelativeLayout.LayoutParams params;

	private final class ViewHolder{
		ImageView imageView,imageView1;
	}

	public ShawnSelectVideoAdapter(Activity activity, List<ShawnDto> mArrayList) {
		this.activity = activity;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
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
		final ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_select_video, null);
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
			new Thread(new Runnable() {
				@Override
				public void run() {
					final Bitmap bitmap = CommonUtil.getVideoThumbnail(dto.filePath, width/4, width/4, MediaStore.Video.Thumbnails.MINI_KIND);
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (bitmap != null) {
								mHolder.imageView.setImageBitmap(bitmap);
								mHolder.imageView.setLayoutParams(params);
							}
						}
					});
				}
			}).start();

		}
		
		if (dto.isSelected) {
			mHolder.imageView1.setImageResource(R.drawable.shawn_icon_selected);
		}else {
			mHolder.imageView1.setImageResource(R.drawable.shawn_icon_unselected);
		}
		
		return convertView;
	}

}
