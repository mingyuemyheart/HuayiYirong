package com.huayi.shawn.yirong.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.MyApplication;

/**
 * 设置
 */
public class ShawnSettingActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_setting);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = this;
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("账号与安全");
        LinearLayout llPwd = findViewById(R.id.llPwd);
        llPwd.setOnClickListener(this);
        TextView tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(this);
    }

    /**
     * 登出对话框
     */
    private void dialogLogout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_cache, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(context, R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvContent.setText("确定要退出登录？");
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
                MyApplication.clearUserInfo(context);
                MyApplication.destoryActivity();
                startActivity(new Intent(context, ShawnLoginActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.llPwd:
                startActivity(new Intent(this, ShawnModifyPwdActivity.class));
                break;
            case R.id.tvLogout:
                dialogLogout();
                break;
        }
    }

}
