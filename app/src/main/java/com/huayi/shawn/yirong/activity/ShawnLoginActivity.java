package com.huayi.shawn.yirong.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.util.sofia.Sofia;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 登录
 */
public class ShawnLoginActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etUserName,etPwd;
	private ImageView ivUClose,ivPClose;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_login);
		mContext = this;
		Sofia.with(this)
				.invasionStatusBar()//设置顶部状态栏缩进
				.statusBarBackground(Color.TRANSPARENT);//设置状态栏颜色
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getRealMetrics(dm);
		int width = dm.widthPixels;

		loadingView = findViewById(R.id.loadingView);
		etUserName = findViewById(R.id.etUserName);
		etUserName.addTextChangedListener(uTextWatcher);
		etPwd = findViewById(R.id.etPwd);
		etPwd.addTextChangedListener(pTextWatcher);
		ivUClose = findViewById(R.id.ivUClose);
		ivUClose.setOnClickListener(this);
		ivPClose = findViewById(R.id.ivPClose);
		ivPClose.setOnClickListener(this);
		TextView tvLogin = findViewById(R.id.tvLogin);
		tvLogin.setOnClickListener(this);
		ImageView ivLogo = findViewById(R.id.ivLogo);
		ViewGroup.LayoutParams params = ivLogo.getLayoutParams();
		params.width = width/2;
		ivLogo.setLayoutParams(params);
	}

	private TextWatcher uTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s)) {
				ivUClose.setVisibility(View.VISIBLE);
			}else {
				ivUClose.setVisibility(View.GONE);
			}
		}
	};

	private TextWatcher pTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s)) {
				ivPClose.setVisibility(View.VISIBLE);
			}else {
				ivPClose.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 登录
	 */
	private void OkHttpLogin() {
		if (TextUtils.isEmpty(etUserName.getText().toString())) {
			Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(etPwd.getText().toString())) {
			Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}

		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://47.105.63.187:8081/interfaces/login/login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("uname", etUserName.getText().toString());
		builder.add("upwd", etPwd.getText().toString());
		final RequestBody body = builder.build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().post(body).url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject object = new JSONObject(result);
										if (!object.isNull("code")) {
											String code = object.getString("code");
											if (TextUtils.equals(code, "200")) {//成功
												MyApplication.USERNAME = etUserName.getText().toString();
												MyApplication.PASSWORD = etPwd.getText().toString();
												MyApplication.TOKEN = object.getString("data");
												MyApplication.saveUserInfo(mContext);
												startActivity(new Intent(mContext, ShawnMainActivity.class));
												finish();
											} else {
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
													}
												}
											}
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								loadingView.setVisibility(View.GONE);
							}
						});
					}
				});
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ivUClose:
				etUserName.setText("");
				break;
			case R.id.ivPClose:
				etPwd.setText("");
				break;
			case R.id.tvLogin:
				OkHttpLogin();
				break;

		default:
			break;
		}
	}

}
