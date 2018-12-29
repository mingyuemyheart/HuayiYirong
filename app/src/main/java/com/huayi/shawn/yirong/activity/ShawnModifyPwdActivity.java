package com.huayi.shawn.yirong.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.util.OkHttpUtil;
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
 * 修改密码
 */
public class ShawnModifyPwdActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etPwdOld,etPwdNew,etPwdConfirm;
	private ImageView ivUClose,ivPClose,ivCClose;
	private AVLoadingIndicatorView loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_modify_pwd);
		if (Build.VERSION.SDK_INT >= 23) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
		}
		mContext = this;
		initWidget();
	}

	/**
	 * 初始化控件
	 */
	private void initWidget() {
		loadingView = findViewById(R.id.loadingView);
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("修改密码");
		etPwdOld = findViewById(R.id.etPwdOld);
		etPwdOld.addTextChangedListener(uTextWatcher);
		etPwdNew = findViewById(R.id.etPwdNew);
		etPwdNew.addTextChangedListener(pTextWatcher);
		etPwdConfirm = findViewById(R.id.etPwdConfirm);
		etPwdConfirm.addTextChangedListener(cTextWatcher);
		ivUClose = findViewById(R.id.ivUClose);
		ivUClose.setOnClickListener(this);
		ivPClose = findViewById(R.id.ivPClose);
		ivPClose.setOnClickListener(this);
		ivCClose = findViewById(R.id.ivCClose);
		ivCClose.setOnClickListener(this);
		TextView tvLogin = findViewById(R.id.tvLogin);
		tvLogin.setOnClickListener(this);
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

	private TextWatcher cTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s)) {
				ivCClose.setVisibility(View.VISIBLE);
			}else {
				ivCClose.setVisibility(View.GONE);
			}
		}
	};

	/**
	 * 修改密码
	 */
	private void OkHttpModify() {
		if (TextUtils.isEmpty(etPwdOld.getText().toString())) {
			Toast.makeText(mContext, "请输入原密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(etPwdNew.getText().toString())) {
			Toast.makeText(mContext, "请输入新密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!TextUtils.equals(etPwdNew.getText().toString(), etPwdConfirm.getText().toString())) {
			Toast.makeText(mContext, "两次输入新密码不一致", Toast.LENGTH_SHORT).show();
			return;
		}

		loadingView.setVisibility(View.VISIBLE);
		final String url = "http://47.105.63.187:8081/interfaces/member/index?token="+MyApplication.TOKEN;
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("pwd", etPwdOld.getText().toString());
		builder.add("npwd", etPwdNew.getText().toString());
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
												MyApplication.PASSWORD = etPwdNew.getText().toString();
												MyApplication.saveUserInfo(mContext);
												if (!object.isNull("msg")) {
													String msg = object.getString("msg");
													if (msg != null) {
														Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
													}
												}
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
			case R.id.llBack:
				finish();
				break;
			case R.id.ivUClose:
				etPwdOld.setText("");
				break;
			case R.id.ivPClose:
				etPwdNew.setText("");
				break;
			case R.id.ivCClose:
				etPwdConfirm.setText("");
				break;
			case R.id.tvLogin:
				OkHttpModify();
				break;

		default:
			break;
		}
	}

}
