package com.huayi.shawn.yirong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.adapter.ShawnSelectFileAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索本地文件
 */
public class ShawnSelectFileActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context mContext;
    private ShawnSelectFileAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_select_file);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mContext = this;
        initRefreshLayout();
        initWidget();
        initGridView();
    }

    /**
     * 初始化下拉刷新布局
     */
    private void initRefreshLayout() {
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeResources(CONST.color1, CONST.color2, CONST.color3, CONST.color4);
        refreshLayout.setProgressViewEndTarget(true, 400);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFiles();
            }
        });
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("选择文件");
        TextView tvControl = findViewById(R.id.tvControl);
        tvControl.setOnClickListener(this);
        tvControl.setText("确定");
        tvControl.setVisibility(View.VISIBLE);

        loadFiles();
    }

    private void initGridView() {
        ListView listView = findViewById(R.id.listView);
        mAdapter = new ShawnSelectFileAdapter(mContext, dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "该类型文件不支持预览", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取相册信息
     */
    private void loadFiles() {
        dataList.clear();
        final String[] suffix = new String[] {CONST.doc,CONST.docx,CONST.ppt,CONST.pptx,CONST.pdf,CONST.xls,CONST.xlsx,CONST.txt,CONST.zip,CONST.rar};
        File rootPath = Environment.getExternalStorageDirectory();//获取sdcard根目录
        final File[] files = rootPath.listFiles();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getFileName(files, suffix);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter != null) {
                            mAdapter.notifyDataSetChanged();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void getFileName(File[] files, String[] suffix) {
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileName(file.listFiles(), suffix);
                }else {
                    String fileName = file.getName();
                    if (!TextUtils.isEmpty(fileName)) {
                        for (String s : suffix) {
                            if (fileName.endsWith(s)) {
                                ShawnDto dto = new ShawnDto();
                                dto.title = fileName;
                                dto.fileType = CONST.FILETYPE4;
                                dto.filePath = file.getAbsolutePath();
                                dto.fileSize = file.length();
                                dataList.add(dto);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;
            case R.id.tvControl:
                String filePath = null;
                long fileSize = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (dto.isSelected) {
                        filePath = dto.filePath;
                        fileSize = dto.fileSize;
                        break;
                    }
                }
                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(this, "请选取一个文件", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("filePath", filePath);
                intent.putExtra("fileSize", fileSize);
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
    }

}
