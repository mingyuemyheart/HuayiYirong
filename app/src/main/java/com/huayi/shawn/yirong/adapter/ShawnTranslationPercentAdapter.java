package com.huayi.shawn.yirong.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.czp.library.ArcProgress;
import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.List;

/**
 * 资源库-传输列表-正在下载
 */
public class ShawnTranslationPercentAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle,tvSize;
		ImageView imageView,ivSelected;
		ArcProgress progressBar;
	}

	public ShawnTranslationPercentAdapter(Activity activity, List<ShawnDto> mArrayList) {
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
		ViewHolder mholder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_translation_percent, null);
			mholder = new ViewHolder();
			mholder.imageView = convertView.findViewById(R.id.imageView);
			mholder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mholder.tvSize = convertView.findViewById(R.id.tvSize);
			mholder.ivSelected = convertView.findViewById(R.id.ivSelected);
			mholder.progressBar = convertView.findViewById(R.id.progressBar);
			convertView.setTag(mholder);
		}else {
			mholder = (ViewHolder) convertView.getTag();
		}

		final ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mholder.tvTitle.setText(dto.title);
		}

		if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mholder.tvSize.setText("");
		}else {
			mholder.tvSize.setText(CommonUtil.getFormatSize(dto.fileSize));
		}

		//1图片、2视频、3音频、4文档、5文件夹
		if (TextUtils.equals(dto.fileType, CONST.FILETYPE1)) {
			mholder.imageView.setImageResource(R.drawable.shawn_icon_image);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {
			mholder.imageView.setImageResource(R.drawable.shawn_icon_video);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE3)) {
			mholder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE4)) {
			String lowCase = dto.filePath.toLowerCase();
			if (lowCase.endsWith(CONST.doc) || lowCase.endsWith(CONST.docx)) {
				mholder.imageView.setImageResource(R.drawable.shawn_icon_word);
			}else if (lowCase.endsWith(CONST.ppt) || lowCase.endsWith(CONST.pptx)) {
				mholder.imageView.setImageResource(R.drawable.shawn_icon_ppt);
			}else if (lowCase.endsWith(CONST.pdf)) {
				mholder.imageView.setImageResource(R.drawable.shawn_icon_pdf);
			}else if (lowCase.endsWith(CONST.xls) || lowCase.endsWith(CONST.xlsx)) {
				mholder.imageView.setImageResource(R.drawable.shawn_icon_xls);
			}else {
				mholder.imageView.setImageResource(R.drawable.shawn_icon_txt);
			}
		}else if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
			mholder.imageView.setImageResource(R.drawable.shawn_icon_files);
		}else {
			mholder.imageView.setImageResource(R.drawable.shawn_icon_txt);
		}

		mholder.progressBar.setProgress(dto.percent);
		if (dto.loadState == CONST.loadComplete) {//load完成
			mholder.progressBar.setVisibility(View.GONE);
		}else {
			mholder.progressBar.setVisibility(View.VISIBLE);
		}

		mholder.progressBar.setOnCenterDraw(new ArcProgress.OnCenterDraw() {
			@Override
			public void draw(Canvas canvas, RectF rectF, float x, float y, float storkeWidth, int progress) {
				Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
				textPaint.setTextSize(30);
				textPaint.setColor(activity.getResources().getColor(R.color.refresh_color1));
				String progressStr = String.valueOf(progress+"%");
				float textX = x-(textPaint.measureText(progressStr)/2);
				float textY = y-((textPaint.descent()+textPaint.ascent())/2);
				canvas.drawText(progressStr,textX,textY,textPaint);
			}
		});

		if (dto.isSelected) {
			mholder.ivSelected.setImageResource(R.drawable.shawn_icon_selected);
		}else {
			mholder.ivSelected.setImageResource(R.drawable.shawn_icon_unselected);
		}

		mholder.ivSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dto.isSelected = !dto.isSelected;
				notifyDataSetChanged();
			}
		});

		return convertView;
	}

}
