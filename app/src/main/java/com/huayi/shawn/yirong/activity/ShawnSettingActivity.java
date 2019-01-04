package com.huayi.shawn.yirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.manager.DataCleanManager;
import com.huayi.shawn.yirong.util.AutoUpdateUtil;
import com.huayi.shawn.yirong.util.CommonUtil;

/**
 * 设置
 */
public class ShawnSettingActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context context;
    private TextView tvCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_setting);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = this;
        MyApplication.addDestoryActivity(this, "ShawnSettingActivity");
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        LinearLayout llUser = findViewById(R.id.llUser);
        llUser.setOnClickListener(this);
        LinearLayout llCache = findViewById(R.id.llCache);
        llCache.setOnClickListener(this);
        LinearLayout llFeedback = findViewById(R.id.llFeedback);
        llFeedback.setOnClickListener(this);
        LinearLayout llAbout = findViewById(R.id.llAbout);
        llAbout.setOnClickListener(this);
        LinearLayout llVersion = findViewById(R.id.llVersion);
        llVersion.setOnClickListener(this);
        tvCache = findViewById(R.id.tvCache);
        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(CommonUtil.getVersion(context));

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        getCache();

    }

    private void getCache() {
        try {
            tvCache.setText(DataCleanManager.getCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    private void dialogCache() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(this, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText("确定清除缓存？");
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                DataCleanManager.clearCache(context);
                getCache();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llUser:
                startActivity(new Intent(this, ShawnUserActivity.class));
                break;
            case R.id.llCache:
                dialogCache();
                break;
            case R.id.llFeedback:

                break;
            case R.id.llAbout:

                break;
            case R.id.llVersion:
                AutoUpdateUtil.checkUpdate(this, this, "110", getString(R.string.app_name), false);
                break;
        }
    }

}
