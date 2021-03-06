package com.huayi.shawn.yirong.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnImageActivity;
import com.huayi.shawn.yirong.activity.ShawnVideoActivity;
import com.huayi.shawn.yirong.adapter.ShawnTranslationCompleteAdapter;
import com.huayi.shawn.yirong.adapter.ShawnTranslationPercentAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.service.DownloadService;
import com.huayi.shawn.yirong.service.UploadService;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源库-上传列表
 */
public class ShawnUploadFragment extends Fragment implements View.OnClickListener {

    private TextView tvLoading,tvComplete;
    private List<ShawnDto> dataList = new ArrayList<>();
    private ShawnTranslationPercentAdapter mAdapter1;
    private List<ShawnDto> dataList1 = new ArrayList<>();
    private ShawnTranslationCompleteAdapter mAdapter2;
    private List<ShawnDto> dataList2 = new ArrayList<>();
    private MyBroadcastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_upload, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget(view);
        initBroadcast();
        initListView1(view);
        initListView2(view);
    }

    private void initBroadcast() {
        mReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONST.BROADCAST_UPLOAD_PROGRESS);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CONST.BROADCAST_UPLOAD_PROGRESS)) {
                ShawnDto data = intent.getParcelableExtra("data");
                if (data != null) {
                    for (ShawnDto dto : dataList1) {
                        if (TextUtils.equals(dto.title, data.title) && dto.fileId == data.fileId) {
                            dto.percent = data.percent;
                            dto.loadState = data.loadState;
                            Log.e("uploadPro", dto.percent+"");
                            if (dto.loadState == CONST.loadComplete) {
                                dataList2.add(0, dto);
                                if (mAdapter2 != null) {
                                    mAdapter2.notifyDataSetChanged();
                                }
                                if (dataList1.size() > 0) {
                                    dataList1.remove(0);
                                }
                                if (dataList1.size() <= 0) {
                                    tvLoading.setVisibility(View.GONE);
                                }else {
                                    tvLoading.setVisibility(View.VISIBLE);
                                }
                                tvLoading.setText("正在上传("+dataList1.size()+")");
                                tvComplete.setText("上传完成("+dataList2.size()+")");
                            }
                            if (mAdapter1 != null) {
                                mAdapter1.notifyDataSetChanged();
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void unregisterBroadCast() {
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    private void initWidget(View view) {
        tvLoading = view.findViewById(R.id.tvLoading);
        tvComplete = view.findViewById(R.id.tvComplete);
        LinearLayout llDelete = view.findViewById(R.id.llDelete);
        llDelete.setOnClickListener(this);

        dataList.clear();
        dataList.addAll(CommonUtil.readUploadInfo(getActivity()));
        dataList1.clear();
        dataList2.clear();
        //按照loadState区分正在上传、上传完成
        for (int i = 0; i < dataList.size(); i++) {
            ShawnDto dto = dataList.get(i);
            if (dto.loadState == CONST.loadComplete) {
                dataList2.add(dto);
            }else {
                dataList1.add(dto);
            }
        }
        if (dataList1.size() <= 0) {
            tvLoading.setVisibility(View.GONE);
        }else {
            tvLoading.setVisibility(View.VISIBLE);
        }
        tvLoading.setText("正在上传("+dataList1.size()+")");
        tvComplete.setText("上传完成("+dataList2.size()+")");
        if (mAdapter1 != null) {
            mAdapter1.notifyDataSetChanged();
        }
        if (mAdapter2 != null) {
            mAdapter2.notifyDataSetChanged();
        }
    }

    private void initListView1(View view) {
        ListView listView = view.findViewById(R.id.listView1);
        mAdapter1 = new ShawnTranslationPercentAdapter(getActivity(), dataList1);
        listView.setAdapter(mAdapter1);
    }

    private void initListView2(View view) {
        ListView listView = view.findViewById(R.id.listView2);
        mAdapter2 = new ShawnTranslationCompleteAdapter(getActivity(), dataList2);
        listView.setAdapter(mAdapter2);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShawnDto dto = dataList2.get(position);
                Intent intent;
                //1图片、2视频、3音频、4文档、5文件夹
                if (TextUtils.equals(dto.fileType, CONST.FILETYPE1)) {
                    intent = new Intent(getActivity(), ShawnImageActivity.class);
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    intent.putExtra(CONST.IMAGE_PATH, dto.filePath);
                    startActivity(intent);
                }else if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {
                    intent = new Intent(getActivity(), ShawnVideoActivity.class);
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    intent.putExtra(CONST.WEB_URL, dto.filePath);
                    startActivity(intent);
                }else if (TextUtils.equals(dto.fileType, CONST.FILETYPE3)) {
                    intent = new Intent(getActivity(), ShawnVideoActivity.class);
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    intent.putExtra(CONST.WEB_URL, dto.filePath);
                    startActivity(intent);
                }else if (TextUtils.equals(dto.fileType, CONST.FILETYPE4)) {
                    CommonUtil.intentWPSOffice(getActivity(), dto.filePath);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        CommonUtil.saveUploadInfo(getActivity(), dataList1);
//        CommonUtil.saveUploadInfo(getActivity(), dataList2);
        unregisterBroadCast();
    }

    /**
     * 删除文件
     */
    private void dialogDelete() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_delete, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvContent = view.findViewById(R.id.tvContent);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvTitle.setText("删除文件");
        tvContent.setText("确定要删除选中文件吗?");
        tvPositive.setText("确定");
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
                //先停止下载，在删除
                getActivity().stopService(new Intent(getActivity(), DownloadService.class));

                List<ShawnDto> list1 = new ArrayList<>();
                for (int i = 0; i < dataList1.size(); i++) {
                    ShawnDto dto = dataList1.get(i);
                    if (!dto.isSelected) {
                        list1.add(dto);
                    }
                }
                dataList1.clear();
                dataList1.addAll(list1);
                if (mAdapter1 != null) {
                    mAdapter1.notifyDataSetChanged();
                }

                List<ShawnDto> list2 = new ArrayList<>();
                for (int i = 0; i < dataList2.size(); i++) {
                    ShawnDto dto = dataList2.get(i);
                    if (!dto.isSelected) {
                        list2.add(dto);
                    }
                }
                dataList2.clear();
                dataList2.addAll(list2);
                if (mAdapter2 != null) {
                    mAdapter2.notifyDataSetChanged();
                }

                if (dataList1.size() <= 0) {
                    tvLoading.setVisibility(View.GONE);
                }else {
                    tvLoading.setVisibility(View.VISIBLE);
                }
                tvLoading.setText("正在上传("+dataList1.size()+")");
                tvComplete.setText("上传完成("+dataList2.size()+")");

                CommonUtil.saveUploadInfo(getActivity(), dataList1);
                CommonUtil.saveUploadInfo(getActivity(), dataList2);
                getActivity().startService(new Intent(getActivity(), UploadService.class));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llDelete:
                dialogDelete();
                break;
        }
    }

}
