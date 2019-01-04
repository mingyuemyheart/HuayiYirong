package com.huayi.shawn.yirong.activity;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 视频预览
 * @author shawn_sun
 */
public class ShawnVideoActivity extends ShawnBaseActivity implements View.OnClickListener, SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

	private SurfaceHolder surfaceHolder;
	private MediaPlayer mPlayer;
	private ImageView ivPlay;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_video);
		initWidget();
		initSurfaceView();
	}

	/**
	 * 显示竖屏，隐藏横屏
	 */
	private void showPort() {
		fullScreen(false);
	}

	private void fullScreen(boolean enable) {
		if (enable) {
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(lp);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attr = getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attr);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		ivPlay = findViewById(R.id.ivPlay);
		ivPlay.setOnClickListener(this);

		if (getIntent().hasExtra(CONST.ACTIVITY_NAME)) {
			String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
			if (!TextUtils.isEmpty(title)) {
				tvTitle.setText(title);
			}
		}

		showPort();
	}

	private void setSurfaceViewLayout() {
//		DisplayMetrics dm = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getRealMetrics(dm);
//		int w = dm.widthPixels,h = dm.heightPixels;
//		int width = 0,height = 0;
//		if (getIntent().hasExtra("width") && getIntent().hasExtra("height")) {
//			width = (int) getIntent().getLongExtra("width", 0);
//			height = (int) getIntent().getLongExtra("height", 0);
//		}
//		if (width == 0 && height == 0) {
//			width = w;
//			height = h;
//		}else {
//			height = w*height/width;
//			width = w;
//		}
//		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
//		surfaceView.setLayoutParams(params);
	}

	/**
	 * 初始化surfaceView
	 */
	private void initSurfaceView() {
		SurfaceView surfaceView = findViewById(R.id.surfaceView);
		surfaceView.setOnClickListener(this);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

		setSurfaceViewLayout();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setDisplay(holder);
		mPlayer.setOnPreparedListener(this);
		mPlayer.setOnCompletionListener(this);
		//设置显示视频显示在SurfaceView上
		try {
			String dataUrl = null;
			if (getIntent().hasExtra(CONST.WEB_URL)) {
				dataUrl = getIntent().getStringExtra(CONST.WEB_URL);
			}else if (getIntent().hasExtra(CONST.VIDEO_PATH)) {
				dataUrl = getIntent().getStringExtra(CONST.VIDEO_PATH);
			}
			if (!TextUtils.isEmpty(dataUrl)) {
				mPlayer.setDataSource(dataUrl);
				mPlayer.prepareAsync();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceHolder = holder;
		releaseMediaPlayer();
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		loadingView.setVisibility(View.GONE);
		swithVideo();
	}

	private void swithVideo() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				ivPlay.setImageResource(R.drawable.shawn_icon_play);
			}else {
				mPlayer.start();
				ivPlay.setImageResource(R.drawable.shawn_icon_pause);
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		handler.removeMessages(1001);
		ivPlay.setVisibility(View.VISIBLE);
		ivPlay.setImageResource(R.drawable.shawn_icon_play);
	}

	/**
	 * 释放MediaPlayer资源
	 */
	private void releaseMediaPlayer() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1001:
					ivPlay.setVisibility(View.GONE);
					break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ivPlay.setVisibility(View.GONE);
		releaseMediaPlayer();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				finish();
				break;
			case R.id.surfaceView:
				if (ivPlay.getVisibility() == View.VISIBLE) {
					ivPlay.setVisibility(View.GONE);
				}else {
					ivPlay.setVisibility(View.VISIBLE);
					handler.removeMessages(1001);
					Message msg = handler.obtainMessage(1001);
					msg.what = 1001;
					handler.sendMessageDelayed(msg, 5000);
				}
				break;
			case R.id.ivPlay:
				swithVideo();
				break;

		}
	}
}
