package com.huayi.shawn.yirong.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;

/**
 * 上传显示进度
 */
public class UploadProgressDialog extends Dialog {

	private Context mContext;
	private TextView tvPercent;

	public UploadProgressDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	public void setStyle(int featureId) {
		requestWindowFeature(featureId);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(R.color.transparent);
		setContentView(R.layout.shawn_dialog_upload_progress);
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		ImageView imageView = findViewById(R.id.imageView);
		tvPercent = findViewById(R.id.tvPercent);

		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.shawn_round_animation);
		imageView.startAnimation(animation);
	}
	
	public void setPercent(int percent) {
		if (tvPercent != null) {
			tvPercent.setText(percent + "%");
		}
	}
	
}
