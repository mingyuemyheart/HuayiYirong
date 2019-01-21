package com.huayi.shawn.yirong.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频预览
 */
public class ShawnVideoActivity extends ShawnBaseActivity implements SurfaceHolder.Callback, OnPreparedListener, OnVideoSizeChangedListener, OnCompletionListener, OnClickListener{

	private Context mContext;
	private RelativeLayout reTitle;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer mPlayer;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("mm:ss", Locale.CHINA);
	private Timer timer;
	private Configuration configuration;//方向监听器
	private ProgressBar progressBar;
	private static final int HANDLER_PROCESS = 0;
	private static final int HANDLER_VISIBILITY = 1;
	private boolean executeOnce = true;//只执行一次
	private ImageView ivPlayLand,ivInFull;//播放按钮
	private TextView tvStartTimeLand,tvEndTimeLand;//开始时间
	private SeekBar seekBarLand;//进度条
	private LinearLayout llBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_video);
		if (Build.VERSION.SDK_INT >= 23) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
		mContext = this;
		initWidget();
		initSurfaceView();
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		configuration = newConfig;
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			showPort();
		}else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			showLand();
		}
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		reTitle = findViewById(R.id.reTitle);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		progressBar = findViewById(R.id.progressBar);
		ivPlayLand = findViewById(R.id.ivPlayLand);
		ivPlayLand.setOnClickListener(this);
		seekBarLand = findViewById(R.id.seekBarLand);
		seekBarLand.setOnTouchListener(seekbarListener);
		tvStartTimeLand = findViewById(R.id.tvStartTimeLand);
		tvStartTimeLand.setText("00:00");
		tvEndTimeLand = findViewById(R.id.tvEndTimeLand);
		ivInFull = findViewById(R.id.ivInFull);
		ivInFull.setOnClickListener(this);
		llBottom = findViewById(R.id.llBottom);
		LinearLayout llSurfaceView = findViewById(R.id.llSurfaceView);
		llSurfaceView.setOnClickListener(this);

		String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
		if (!TextUtils.isEmpty(title)) {
			tvTitle.setText(title);
		}
	}

	/**
	 * 禁止seekbar监听事件
	 */
	private OnTouchListener seekbarListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			return true;
		}
	};

	/**
	 * 显示竖屏，隐藏横屏
	 */
	private void showPort() {
		reTitle.setVisibility(View.VISIBLE);
		ivInFull.setImageResource(R.drawable.shawn_icon_expand);
		fullScreen(false);
		if (mPlayer != null) {
			changeVideo(mPlayer.getVideoWidth(), mPlayer.getVideoHeight());
		}
	}

	/**
	 * 显示横屏，隐藏竖屏
	 */
	private void showLand() {
		reTitle.setVisibility(View.GONE);
		ivInFull.setImageResource(R.drawable.shawn_icon_collose);
		fullScreen(true);
		if (mPlayer != null) {
			changeVideo(mPlayer.getVideoWidth(), mPlayer.getVideoHeight());
		}
	}

	/**
	 * 初始化surfaceView
	 */
	private void initSurfaceView() {
		surfaceView = findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setDisplay(holder);
		mPlayer.setOnPreparedListener(this);
		mPlayer.setOnVideoSizeChangedListener(this);
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
		releaseTimer();
		releaseMediaPlayer();
	}

	@Override
	public void onPrepared(MediaPlayer player) {
		tvStartTimeLand.setText(sdf1.format(player.getCurrentPosition()));
		tvEndTimeLand.setText(sdf1.format(player.getDuration()));

		seekBarLand.setProgress(0);
		seekBarLand.setMax(player.getDuration()/1000);

		startPlayVideo();
	}

	/**
	 * 开始播放视频
	 */
	private void startPlayVideo() {
		if (mPlayer != null) {
			if (mPlayer.isPlaying()) {
				ivPlayLand.setImageResource(R.drawable.shawn_icon_play);
				mPlayer.pause();
				releaseTimer();
			}else {
				ivPlayLand.setImageResource(R.drawable.shawn_icon_pause);
				mPlayer.start();

				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (mPlayer.isPlaying() && !seekBarLand.isPressed()) {
							handler.sendEmptyMessage(HANDLER_PROCESS);
						}
					}
				}, 0, 1000);
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLER_PROCESS:
					if (mPlayer != null) {
						int position = mPlayer.getCurrentPosition();
						int duration = mPlayer.getDuration();

						if (position > 0) {
							progressBar.setVisibility(View.GONE);
							if (executeOnce) {
								dismissColunm();
							}
						}

						if (duration > 0) {
							long posLand = seekBarLand.getMax() * position / duration;
							seekBarLand.setProgress((int) posLand);
							tvStartTimeLand.setText(sdf1.format(position));
						}
					}
					break;
				case HANDLER_VISIBILITY:
					llBottom.setVisibility(View.GONE);
					break;

				default:
					break;
			}

		};
	};

	/**
	 * 启动线程,隐藏操作栏
	 */
	private void dismissColunm() {
		handler.removeMessages(HANDLER_VISIBILITY);
		Message msg = new Message();
		msg.what = HANDLER_VISIBILITY;
		handler.sendMessageDelayed(msg, 5000);
		executeOnce = false;
	}

	/**
	 * 改变横竖屏切换是视频的比例
	 * @param videoW
	 * @param videoH
	 */
	private void changeVideo(int videoW, int videoH) {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;

		if (surfaceView == null) {
			return;
		}
		if (videoW == 0 && videoH == 0) {
			return;
		}
		if (videoW >= videoH) {
			surfaceView.setLayoutParams(new LinearLayout.LayoutParams(width, videoH*width/videoW));
		}else {
			surfaceView.setLayoutParams(new LinearLayout.LayoutParams(videoW*height/videoH, height));
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer player, int videoW, int videoH) {
		changeVideo(videoW, videoH);
	}

	@Override
	public void onCompletion(MediaPlayer player) {
		releaseTimer();
		ivPlayLand.setImageResource(R.drawable.shawn_icon_play);
		seekBarLand.setProgress(0);
		tvStartTimeLand.setText("00:00");
		handler.removeMessages(HANDLER_VISIBILITY);
		llBottom.setVisibility(View.VISIBLE);
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

	/**
	 * 释放timer
	 */
	private void releaseTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseTimer();
		releaseMediaPlayer();
	}

	private void exit() {
		if (configuration == null) {
			releaseTimer();
			releaseMediaPlayer();
			finish();
		}else {
			if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
				releaseTimer();
				releaseMediaPlayer();
				finish();
			}else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				exit();
				break;
			case R.id.llSurfaceView:
				if (mPlayer != null && mPlayer.isPlaying()) {
					if (llBottom.getVisibility() == View.VISIBLE) {
						llBottom.setVisibility(View.GONE);
					} else {
						llBottom.setVisibility(View.VISIBLE);
						dismissColunm();
					}
				} else {
					llBottom.setVisibility(View.VISIBLE);
				}
				break;
			case R.id.ivInFull:
				dismissColunm();

				if (configuration == null) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				} else {
					if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					} else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				}
				break;
			case R.id.ivPlayLand:
				dismissColunm();
				startPlayVideo();
				break;
		}
	}

}
