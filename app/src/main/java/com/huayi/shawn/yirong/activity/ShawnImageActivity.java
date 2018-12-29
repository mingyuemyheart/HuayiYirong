package com.huayi.shawn.yirong.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;

/**
 * 图片预览
 */
public class ShawnImageActivity extends ShawnBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_image);
        initWidget();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        PhotoView imageView = findViewById(R.id.imageView);

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        String dataUrl = getIntent().getStringExtra(CONST.WEB_URL);
        if (!TextUtils.isEmpty(dataUrl)) {
            Picasso.get().load(dataUrl).into(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
        }
    }

}
