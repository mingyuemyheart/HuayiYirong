package com.huayi.shawn.yirong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.util.OkHttpUtil;

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
 * 欢迎界面
 */
public class ShawnWelcomeActivity extends ShawnBaseActivity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_welcome);

		//点击Home键后再点击APP图标，APP重启而不是回到原来界面
		if (!isTaskRoot()) {
			finish();
			return;
		}
		//点击Home键后再点击APP图标，APP重启而不是回到原来界面

		mContext = this;

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!TextUtils.isEmpty(MyApplication.USERNAME) && !TextUtils.isEmpty(MyApplication.PASSWORD)) {
					OkHttpLogin();
				}else {
					startActivity(new Intent(mContext, ShawnLoginActivity.class));
					finish();
				}
			}
		}, 2000);

	}

	/**
	 * 登录
	 */
	private void OkHttpLogin() {
		final String url = "http://47.105.63.187:8081/interfaces/login/login";
		FormBody.Builder builder = new FormBody.Builder();
		builder.add("uname", MyApplication.USERNAME);
		builder.add("upwd", MyApplication.PASSWORD);
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

							}
						});
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onKeyDown(int KeyCode, KeyEvent event){
		if (KeyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(KeyCode, event);
	}
	
}
