package com.huayi.shawn.yirong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.adapter.ShawnSelectVideoAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取本地所有视频
 */
public class ShawnSelectVideoActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private ShawnSelectVideoAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_select_video);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mContext = this;
        initWidget();
        initGridView();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("选择视频");
        TextView tvControl = findViewById(R.id.tvControl);
        tvControl.setOnClickListener(this);
        tvControl.setText("确定");
        tvControl.setVisibility(View.VISIBLE);

        loadImages();
    }

    private void initGridView() {
        GridView gridView = findViewById(R.id.gridView);
        mAdapter = new ShawnSelectVideoAdapter(this, dataList);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShawnDto dto = dataList.get(position);
                Intent intent = new Intent(mContext, ShawnVideoActivity.class);
                intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                intent.putExtra(CONST.VIDEO_PATH, dto.filePath);
                startActivity(intent);

            }
        });
    }

    /**
     * 获取相册信息
     */
    private void loadImages() {
        dataList.clear();
        dataList.addAll(CommonUtil.getAllLocalVideos(this));
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.tvControl:
                List<ShawnDto> list = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (dto.isSelected) {
                        list.add(dto);
                    }
                }
                if (list.size() <= 0) {
                    Toast.makeText(this, "请选取一个文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) list);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
    }

}
