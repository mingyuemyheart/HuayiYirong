package com.huayi.shawn.yirong.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 稿件-审核
 */
public class ShawnGaojianCheckAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater mInflater;
	private List<ShawnDto> mArrayList;

	private final class ViewHolder{
		TextView tvTitle,tvContent,tvTime,tvState,tvPass,tvRefuse;
		LinearLayout llControl;
	}

	public ShawnGaojianCheckAdapter(Activity activity, List<ShawnDto> mArrayList) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_gaojian_check, null);
			mHolder = new ViewHolder();
			mHolder.tvTitle = convertView.findViewById(R.id.tvTitle);
			mHolder.tvContent = convertView.findViewById(R.id.tvContent);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			mHolder.tvState = convertView.findViewById(R.id.tvState);
			mHolder.tvPass = convertView.findViewById(R.id.tvPass);
			mHolder.tvRefuse = convertView.findViewById(R.id.tvRefuse);
			mHolder.llControl = convertView.findViewById(R.id.llControl);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		final ShawnDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.title)) {
			mHolder.tvTitle.setText(dto.title);
		}

		if (!TextUtils.isEmpty(dto.content)) {
			mHolder.tvContent.setText(dto.content);
		}

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvTime.setText(dto.time);
		}

		if (!TextUtils.isEmpty(dto.state)) {
			if (TextUtils.equals(dto.state, "待审核")) {
				mHolder.tvState.setTextColor(0xff628BF0);
			}else if (TextUtils.equals(dto.state, "待二审")) {
				mHolder.tvState.setTextColor(activity.getResources().getColor(R.color.red));
			}
			mHolder.tvState.setText(dto.state);
		}

		mHolder.tvPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OkHttpPass(position, dto);
			}
		});

		mHolder.tvRefuse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OkHttpRefuse(position, dto);
			}
		});

		if (dto.isSelected) {
			mHolder.llControl.setVisibility(View.VISIBLE);
		}else {
			mHolder.llControl.setVisibility(View.GONE);
		}

		return convertView;
	}

	/**
	 * 通过
	 * @param position
	 * @param dto
	 */
	private void OkHttpPass(final int position, final ShawnDto dto) {
		if (TextUtils.isEmpty(dto.id)) {
			Toast.makeText(activity, "稿件信息异常，审核失败", Toast.LENGTH_SHORT).show();
			return;
		}
		final String url = String.format("http://47.105.63.187:8081/interfaces/article/verify?token=%s&id=%s", MyApplication.TOKEN, dto.id);
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("code")) {
											String code = obj.getString("code");
											if (TextUtils.equals(code, "200")) {
												if (TextUtils.equals(dto.state, "待审核")) {
													dto.state = "待二审";
												}else if (TextUtils.equals(dto.state, "待二审")) {
													dto.state = "待发布";
												}
												mArrayList.remove(position);
												notifyDataSetChanged();
												Toast.makeText(activity, "审核通过", Toast.LENGTH_SHORT).show();
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

	/**
	 * 打回
	 * @param position
	 * @param dto
	 */
	private void OkHttpRefuse(final int position, final ShawnDto dto) {
		if (TextUtils.isEmpty(dto.id)) {
			Toast.makeText(activity, "稿件信息异常，审核失败", Toast.LENGTH_SHORT).show();
			return;
		}
		final String url = String.format("http://47.105.63.187:8081/interfaces/article/reject?token=%s&id=%s", MyApplication.TOKEN, dto.id);
		new Thread(new Runnable() {
			@Override
			public void run() {
				OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {
					}
					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String result = response.body().string();
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (!TextUtils.isEmpty(result)) {
									try {
										JSONObject obj = new JSONObject(result);
										if (!obj.isNull("code")) {
											String code = obj.getString("code");
											if (TextUtils.equals(code, "200")) {
												if (TextUtils.equals(dto.state, "待审核")) {
													dto.state = "审核失败";
												}else if (TextUtils.equals(dto.state, "待二审")) {
													dto.state = "审核失败";
												}
												mArrayList.remove(position);
												notifyDataSetChanged();
												Toast.makeText(activity, "已打回", Toast.LENGTH_SHORT).show();
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

}
