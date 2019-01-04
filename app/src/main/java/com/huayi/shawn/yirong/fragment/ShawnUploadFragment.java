package com.huayi.shawn.yirong.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnImageActivity;
import com.huayi.shawn.yirong.activity.ShawnVideoActivity;
import com.huayi.shawn.yirong.adapter.ShawnTranslationAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源库-上传列表
 */
public class ShawnUploadFragment extends Fragment {

    private TextView tvComplete;

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
        initListView(view);
    }

    private void initWidget(View view) {
        tvComplete = view.findViewById(R.id.tvComplete);
    }

    private void initListView(View view) {
        final List<ShawnDto> dataList = new ArrayList<>();
        dataList.addAll(CommonUtil.readUploadInfo(getActivity()));
        tvComplete.setText("上传完成("+dataList.size()+")");

        ListView listView = view.findViewById(R.id.listView);
        ShawnTranslationAdapter mAdapter = new ShawnTranslationAdapter(getActivity(), dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShawnDto dto = dataList.get(position);
                Intent intent;
                //1图片、2视频、3音频、4文档、5文件夹
                if (TextUtils.equals(dto.fileType, CONST.FILETYPE1)) {
                    intent = new Intent(getActivity(), ShawnImageActivity.class);
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    intent.putExtra(CONST.WEB_URL, dto.filePath);
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
                    Toast.makeText(getActivity(), "该类型文件不支持预览", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
