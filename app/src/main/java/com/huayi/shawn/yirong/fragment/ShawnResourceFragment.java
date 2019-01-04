package com.huayi.shawn.yirong.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnImageActivity;
import com.huayi.shawn.yirong.activity.ShawnSelectFileActivity;
import com.huayi.shawn.yirong.activity.ShawnSelectImageActivity;
import com.huayi.shawn.yirong.activity.ShawnSelectVideoActivity;
import com.huayi.shawn.yirong.activity.ShawnVideoActivity;
import com.huayi.shawn.yirong.adapter.ShawnResourceAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.AuthorityUtil;
import com.huayi.shawn.yirong.util.CommonUtil;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.util.ShawnRequestBody;
import com.huayi.shawn.yirong.util.ShawnResponseBody;
import com.huayi.shawn.yirong.view.UploadProgressDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 资源库-个人库
 */
public class ShawnResourceFragment extends Fragment implements View.OnClickListener {

    private LinearLayout llContainer,llSearch;
    private EditText etSearch;
    private ImageView ivClear,iv4,iv5,iv6;
    private TextView tvCount,tv4,tv5,tv6;
    private ShawnResourceAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();
    private List<ShawnDto> dataLists = new ArrayList<>();//保存数据用
    private List<ShawnDto> searchList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局
    private String columnId,parentId;//资源导航栏id、父级id
    private int itemLevel = 0;//目录层级，默认为第0级
    private MyBroadCastReceiver mReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_resource, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBroadCast();
        initRefreshLayout(view);
        initWidget(view);
        initListView(view);
    }

    /**
     * 初始化下拉刷新布局
     */
    private void initRefreshLayout(View view) {
        refreshLayout = view.findViewById(R.id.refreshLayout);
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
                if (itemLevel == 0) {
                    refresh();
                }else {
                    OkHttpItemList(parentId, parentId);
                }
            }
        });
    }

    /**
     * 重置获取的栏目id、父级id
     */
    private void resetId() {
        columnId = getArguments().getString("columnId");
        parentId = getArguments().getString("parentId");
    }

    private void refresh() {
        resetId();
        refreshLayout.setRefreshing(true);
        dataList.clear();
        OkHttpList();
    }

    private void initWidget(View view) {
        llContainer = view.findViewById(R.id.llContainer);
        llSearch = view.findViewById(R.id.llSearch);
        TextView tvCancel = view.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(textWatcher);
        ivClear = view.findViewById(R.id.ivClear);
        ivClear.setOnClickListener(this);
        tvCount = view.findViewById(R.id.tvCount);
        LinearLayout ll1 = view.findViewById(R.id.ll1);
        ll1.setOnClickListener(this);
        LinearLayout ll2 = view.findViewById(R.id.ll2);
        ll2.setOnClickListener(this);
        LinearLayout ll3 = view.findViewById(R.id.ll3);
        ll3.setOnClickListener(this);
        LinearLayout ll4 = view.findViewById(R.id.ll4);
        ll4.setOnClickListener(this);
        LinearLayout ll5 = view.findViewById(R.id.ll5);
        ll5.setOnClickListener(this);
        LinearLayout ll6 = view.findViewById(R.id.ll6);
        ll6.setOnClickListener(this);
        iv4 = view.findViewById(R.id.iv4);
        iv5 = view.findViewById(R.id.iv5);
        iv6 = view.findViewById(R.id.iv6);
        tv4 = view.findViewById(R.id.tv4);
        tv5 = view.findViewById(R.id.tv5);
        tv6 = view.findViewById(R.id.tv6);

        refresh();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            searchList.clear();
            tvCount.setText("");
            tvCount.setVisibility(View.GONE);
            if (TextUtils.isEmpty(s)) {
                ivClear.setVisibility(View.GONE);
                dataList.clear();
            }else {
                ivClear.setVisibility(View.VISIBLE);
                for (int i = 0; i < dataLists.size(); i++) {
                    ShawnDto dto = dataLists.get(i);
                    if (dto.title.contains(s)) {
                        searchList.add(dto);
                    }
                }
                dataList.clear();
                dataList.addAll(searchList);
                tvCount.setText("搜索结果("+searchList.size()+")");
                tvCount.setVisibility(View.VISIBLE);
            }
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void initListView(View view) {
        ListView listView = view.findViewById(R.id.listView);
        mAdapter = new ShawnResourceAdapter(getActivity(), dataList);
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
                }else if (TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {
                    String fid;
                    if (TextUtils.equals(dto.title, CONST.lastFile)) {//点击返回上级目录
                        fid = dto.pid;
                    }else {//点击获取下一级文件夹内容
                        fid = dto.id;
                    }
                    parentId = fid;
                    if (TextUtils.equals(fid, dto.pid)) {//返回父一级
                        itemLevel--;
                    }else {//进入下一级
                        itemLevel++;
                    }
                    if (itemLevel == 0) {
                        resetId();
                        refresh();
                    }else {
                        OkHttpItemList(fid, dto.pid);
                    }
                }
            }
        });
    }

    /**
     * 初始化广播
     */
    private void initBroadCast() {
        mReceiver = new MyBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONST.BROADCAST_CONTROL);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), CONST.BROADCAST_CONTROL)) {
                setBottomState();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    /**
     * 设置底部操作按钮状态
     */
    private void setBottomState() {
        int count = 0;
        for (int i = 0; i < dataList.size(); i++) {
            ShawnDto dto = dataList.get(i);
            if (dto.isSelected) {
                count++;
            }
        }
        if (count <= 0) {
            iv4.setImageResource(R.drawable.shawn_icon_download_gray);
            iv5.setImageResource(R.drawable.shawn_icon_delete_gray);
            iv6.setImageResource(R.drawable.shawn_icon_rename_gray);
            tv4.setTextColor(getResources().getColor(R.color.text_color4));
            tv5.setTextColor(getResources().getColor(R.color.text_color4));
            tv6.setTextColor(getResources().getColor(R.color.text_color4));
        }else if (count == 1) {
            iv4.setImageResource(R.drawable.shawn_icon_download_blue);
            iv5.setImageResource(R.drawable.shawn_icon_delete_blue);
            iv6.setImageResource(R.drawable.shawn_icon_rename_blue);
            tv4.setTextColor(getResources().getColor(R.color.refresh_color1));
            tv5.setTextColor(getResources().getColor(R.color.refresh_color1));
            tv6.setTextColor(getResources().getColor(R.color.refresh_color1));
        }else {
            iv4.setImageResource(R.drawable.shawn_icon_download_blue);
            iv5.setImageResource(R.drawable.shawn_icon_delete_blue);
            iv6.setImageResource(R.drawable.shawn_icon_rename_gray);
            tv4.setTextColor(getResources().getColor(R.color.refresh_color1));
            tv5.setTextColor(getResources().getColor(R.color.refresh_color1));
            tv6.setTextColor(getResources().getColor(R.color.text_color4));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCancel:
                CommonUtil.hideInputSoft(etSearch, getActivity());
                llContainer.setVisibility(View.VISIBLE);
                llSearch.setVisibility(View.GONE);
                tvCount.setVisibility(View.GONE);
                dataList.clear();
                dataList.addAll(dataLists);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.ivClear:
                etSearch.setText("");
                break;
            case R.id.ll1:
                dialogMkFiles();
                break;
            case R.id.ll2:
                checkAuthority(true);
                break;
            case R.id.ll3:
                etSearch.setText("");
                tvCount.setText("");
                tvCount.setVisibility(View.GONE);
                llContainer.setVisibility(View.GONE);
                llSearch.setVisibility(View.VISIBLE);
                tvCount.setVisibility(View.VISIBLE);
                dataList.clear();
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.ll4:
                checkAuthority(false);
                break;
            case R.id.ll5:
                int count = 0;
                String fileIds = "";
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (dto.isSelected) {
                        fileIds = fileIds+dto.id+",";
                        count++;
                    }
                }
                if (count <= 0) {
                    Toast.makeText(getActivity(), "请选中一个文件", Toast.LENGTH_SHORT).show();
                }else {
                    if (fileIds.endsWith(",")) {
                        fileIds = fileIds.substring(0, fileIds.length()-1);
                    }
                    if (!TextUtils.isEmpty(fileIds)) {
                        dialogDelete(fileIds);
                    }
                }
                break;
            case R.id.ll6:
                count = 0;
                ShawnDto data = null;
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (dto.isSelected) {
                        data = dto;
                        count++;
                    }
                }
                if (count == 1) {
                    dialogRename(data);
                }else if (count <= 0) {
                    Toast.makeText(getActivity(), "请选中一个文件", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "不能同时重命名多个文件", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case 1001://上传文件
                    if (data != null) {
                        List<ShawnDto> list = new ArrayList<>();
                        ShawnDto dto = data.getParcelableExtra("data");
                        if (dto != null) {
                            list.add(dto);
                        }
                        CommonUtil.saveUploadInfo(getActivity(), list);
                        Toast.makeText(getActivity(), "文件上传中", Toast.LENGTH_SHORT).show();
                        if (dto != null) {
                            OkHttpUpload(dto.filePath, dto.fileSize);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 获取资源库数据
     */
    private void OkHttpList() {
        final String url = String.format("http://47.105.63.187:8081/interfaces/Resources/index?token=%s&cid=%s&pid=%s", MyApplication.TOKEN, columnId, parentId);
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                ShawnDto dto = new ShawnDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                if (!itemObj.isNull("id")) {
                                                    dto.id = itemObj.getString("id");
                                                }
                                                if (!itemObj.isNull("pid")) {
                                                    dto.pid = itemObj.getString("pid");
                                                }
                                                if (!itemObj.isNull("name")) {
                                                    dto.title = itemObj.getString("name");
                                                }
                                                if (!itemObj.isNull("time")) {
                                                    dto.time = itemObj.getString("time");
                                                }
                                                if (!itemObj.isNull("type")) {
                                                    dto.fileType = itemObj.getString("type");
                                                }
                                                if (!itemObj.isNull("file_size")) {
                                                    dto.fileSize = itemObj.getLong("file_size");
                                                }
                                                if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {//视频
                                                    if (!itemObj.isNull("file_url")) {
                                                        dto.filePath = itemObj.getString("file_url");
                                                    }
                                                }else {
                                                    if (!itemObj.isNull("file_path")) {
                                                        dto.filePath = itemObj.getString("file_path");
                                                    }
                                                }
                                                dataList.add(dto);
                                                dataLists.add(dto);
                                            }

                                            setBottomState();
                                            if (mAdapter != null) {
                                                mAdapter.notifyDataSetChanged();
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 获取资源库二级列表数据
     */
    private void OkHttpItemList(String id, String pid) {
        refreshLayout.setRefreshing(true);
        dataList.clear();

        ShawnDto dto = new ShawnDto();
        dto.id = id;
        dto.pid = pid;
        dto.title = CONST.lastFile;
        dto.time = "点击返回上级文件夹";
        dto.fileType = "5";
        dataList.add(0, dto);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/childrenlist?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", id);
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("data")) {
                                            JSONArray array = obj.getJSONArray("data");
                                            for (int i = 0; i < array.length(); i++) {
                                                ShawnDto dto = new ShawnDto();
                                                JSONObject itemObj = array.getJSONObject(i);
                                                if (!itemObj.isNull("id")) {
                                                    dto.id = itemObj.getString("id");
                                                }
                                                if (!itemObj.isNull("pid")) {
                                                    dto.pid = itemObj.getString("pid");
                                                }
                                                if (!itemObj.isNull("name")) {
                                                    dto.title = itemObj.getString("name");
                                                }
                                                if (!itemObj.isNull("time")) {
                                                    dto.time = itemObj.getString("time");
                                                }
                                                if (!itemObj.isNull("type")) {
                                                    dto.fileType = itemObj.getString("type");
                                                }
                                                if (!itemObj.isNull("file_size")) {
                                                    dto.fileSize = itemObj.getLong("file_size");
                                                }
                                                if (TextUtils.equals(dto.fileType, CONST.FILETYPE2)) {//视频
                                                    if (!itemObj.isNull("file_url")) {
                                                        dto.filePath = itemObj.getString("file_url");
                                                    }
                                                }else {
                                                    if (!itemObj.isNull("file_path")) {
                                                        dto.filePath = itemObj.getString("file_path");
                                                    }
                                                }
                                                dataList.add(dto);
                                                dataLists.add(dto);
                                            }

                                            setBottomState();
                                            if (mAdapter != null) {
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 新建文件夹
     */
    private void dialogMkFiles() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_mk_files, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvContent = view.findViewById(R.id.tvContent);
        final EditText etFileName = view.findViewById(R.id.etFileName);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvTitle.setText("创建文件夹");
        tvContent.setText("请为此文件夹输入名称");
        tvPositive.setText("创建");
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(etFileName.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入文件夹名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                OkHttpMkdir(etFileName.getText().toString());
            }
        });
    }

    /**
     * 创建文件夹
     * @param fileName 文件夹名称
     */
    private void OkHttpMkdir(String fileName) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(columnId) || TextUtils.isEmpty(parentId)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/newfolder?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("filename", fileName);
        builder.add("cid", columnId);
        builder.add("pid", parentId);
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("code")) {
                                            String code = obj.getString("code");
                                            if (TextUtils.equals(code, "200")) {
                                                if (itemLevel == 0) {
                                                    refresh();
                                                }else {
                                                    OkHttpItemList(parentId, parentId);
                                                }
                                            }
                                        }
                                        if (!obj.isNull("msg")) {
                                            String msg = obj.getString("msg");
                                            if (!TextUtils.isEmpty(msg)) {
                                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
     * 上传文件
     */
    private void dialogUpload() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_upload, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("选择文件类型");
        LinearLayout llImage = view.findViewById(R.id.llImage);
        LinearLayout llVideo = view.findViewById(R.id.llVideo);
        LinearLayout llFile = view.findViewById(R.id.llFile);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);

        final Dialog dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        llImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//调取系统相册
                dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), ShawnSelectImageActivity.class), 1001);
            }
        });

        llVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), ShawnSelectVideoActivity.class), 1001);
            }
        });

        llFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivityForResult(new Intent(getActivity(), ShawnSelectFileActivity.class), 1001);
            }
        });

        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    private UploadProgressDialog uploadProgressDialog;
    private void showDialog() {
        if (uploadProgressDialog == null) {
            uploadProgressDialog = new UploadProgressDialog(getActivity());
        }
        uploadProgressDialog.setPercent(0);
        uploadProgressDialog.show();
    }
    private void cancelDialog() {
        if (uploadProgressDialog != null) {
            uploadProgressDialog.dismiss();
        }
    }
    private void setDialogProgress(int progress) {
        if (uploadProgressDialog != null) {
            uploadProgressDialog.setPercent(progress);
        }
    }
    /**
     * 文件上传
     * @param filePath
     * @param totalSize 文件大小
     */
    private void OkHttpUpload(String filePath, final long totalSize) {
        showDialog();
        if (TextUtils.isEmpty(columnId) || TextUtils.isEmpty(parentId) || TextUtils.isEmpty(filePath)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/uploadfiles?token="+MyApplication.TOKEN;
        final ShawnRequestBody shawnRequestBody = new ShawnRequestBody(file, "application/octet-stream", new ShawnRequestBody.ProgressListener() {
            @Override
            public void transferred(final long size) {
                if (isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setDialogProgress((int)(100*size/totalSize));
                        }
                    });
                }
            }
        });
        String formData = String.format("form-data;name=file; filename=%s", file.getName());
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addPart(Headers.of("Content-Disposition",formData), shawnRequestBody);
        builder.addFormDataPart("cid", columnId);
        builder.addFormDataPart("fid", parentId);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().post(builder.build()).url(url).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String result = response.body().string();
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("code")) {
                                            String code = obj.getString("code");
                                            if (TextUtils.equals(code, "200")) {
                                                if (itemLevel == 0) {
                                                    refresh();
                                                }else {
                                                    OkHttpItemList(parentId, parentId);
                                                }
                                            }
                                        }
                                        if (!obj.isNull("msg")) {
                                            String msg = obj.getString("msg");
                                            if (!TextUtils.isEmpty(msg)) {
                                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                cancelDialog();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 下载文件
     */
    private void dialogDownload() {
        int count = 0;//选中文件个数
        for (int i = 0; i < dataList.size(); i++) {
            ShawnDto dto = dataList.get(i);
            if (dto.isSelected) {
                count++;
            }
        }
        if (count <= 0) {
            Toast.makeText(getActivity(), "请选中一个非文件夹文件", Toast.LENGTH_SHORT).show();
        }else {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.shawn_dialog_delete, null);
            TextView tvTitle = view.findViewById(R.id.tvTitle);
            TextView tvContent = view.findViewById(R.id.tvContent);
            TextView tvNegtive = view.findViewById(R.id.tvNegtive);
            TextView tvPositive = view.findViewById(R.id.tvPositive);

            final Dialog dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
            dialog.setContentView(view);
            dialog.show();

            tvTitle.setText("下载文件");
            tvContent.setText("确定要下载选中文件吗?");
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
                    List<ShawnDto> list = new ArrayList<>();//需要保存的list
                    for (int i = 0; i < dataList.size(); i++) {
                        ShawnDto dto = dataList.get(i);
                        if (dto.isSelected) {
                            if (!TextUtils.equals(dto.fileType, CONST.FILETYPE5)) {//支持下载非文件夹类型文件
                                OkHttpDownload(dto.filePath, dto.title);
                                list.add(dto);
                            }else {
                                Toast.makeText(getActivity(), "不支持文件夹下载", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    CommonUtil.saveDownloadInfo(getActivity(), list);
                    Toast.makeText(getActivity(), "文件下载中", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 下载文件
     * @param filePath
     * @param fileName
     */
    private void OkHttpDownload(final String filePath, final String fileName) {
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(filePath).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        ShawnResponseBody body = new ShawnResponseBody(response.body(), new ShawnResponseBody.ProgressListener() {
                            @Override
                            public void transferred(final long pregressSize, final long totalSize, final boolean success) {
                                if (isAdded()) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setDialogProgress((int)(100*pregressSize/totalSize));
                                            if (success) {
                                                cancelDialog();
                                                Toast.makeText(getActivity(), "下载完成", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });

                        final byte[] bytes = body.bytes();
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    File files = new File(CONST.DOWNLOAD_ADDR);
                                    if (!files.exists()) {
                                        files.mkdirs();
                                    }
                                    FileOutputStream fos = new FileOutputStream(new File(files.getAbsolutePath()+"/"+fileName));
                                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                                    bos.write(bytes);
                                    bos.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 下载文件
     */
    private void downloadFile(String dataUrl, String fileName) {
        if (TextUtils.isEmpty(dataUrl) || TextUtils.isEmpty(fileName)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        DownloadManager dManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(dataUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置下载路径和文件名
        File files = new File(CONST.DOWNLOAD_ADDR);
        if (!files.exists()) {
            files.mkdirs();
        }
//        String fileName = dataUrl.substring(dataUrl.lastIndexOf("/") + 1);//获取文件名称
        request.setDestinationUri(Uri.fromFile(new File(files.getAbsolutePath()+"/"+fileName)));//指定sdcard下载目录
//        request.setDestinationInExternalPublicDir(files.getAbsolutePath(), filename);
        request.setDescription(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        long refernece = dManager.enqueue(request);
//		// 把当前下载的ID保存起来
//		SharedPreferences sPreferences = mContext.getSharedPreferences("downloadplato", 0);
//		sPreferences.edit().putLong("plato", refernece).commit();

    }

    /**
     * 删除文件
     * @param fileIds 文件id，多个以逗号隔开
     */
    private void dialogDelete(final String fileIds) {
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
                OkHttpDelete(fileIds);
            }
        });
    }

    /**
     * 删除文件
     * @param fileIds 文件id，多个以逗号隔开
     */
    private void OkHttpDelete(String fileIds) {
        if (TextUtils.isEmpty(fileIds)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/removelist?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("id", fileIds);
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("code")) {
                                            String code = obj.getString("code");
                                            if (TextUtils.equals(code, "200")) {
                                                if (itemLevel == 0) {
                                                    refresh();
                                                }else {
                                                    OkHttpItemList(parentId, parentId);
                                                }
                                            }
                                        }
                                        if (!obj.isNull("msg")) {
                                            String msg = obj.getString("msg");
                                            if (!TextUtils.isEmpty(msg)) {
                                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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
     * 重命名文件
     */
    private void dialogRename(final ShawnDto data) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_mk_files, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvContent = view.findViewById(R.id.tvContent);
        final EditText etFileName = view.findViewById(R.id.etFileName);
        TextView tvNegtive = view.findViewById(R.id.tvNegtive);
        TextView tvPositive = view.findViewById(R.id.tvPositive);

        final Dialog dialog = new Dialog(getActivity(), R.style.CustomProgressDialog);
        dialog.setContentView(view);
        dialog.show();

        tvTitle.setText("重命名文件");
        tvContent.setText("请为此文件输入新名称");
        etFileName.setHint(data.title);
        tvPositive.setText("重命名");
        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(etFileName.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入新文件名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                OkHttpRename(etFileName.getText().toString(), data.id);
            }
        });
    }

    /**
     * 文件重命名
     * @param fileName 文件名称
     */
    private void OkHttpRename(String fileName, String fileId) {
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(fileId)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/renamefile?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("name", fileName);
        builder.add("id", fileId);
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
                        if (!isAdded()) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("code")) {
                                            String code = obj.getString("code");
                                            if (TextUtils.equals(code, "200")) {
                                                if (itemLevel == 0) {
                                                    refresh();
                                                }else {
                                                    OkHttpItemList(parentId, parentId);
                                                }
                                            }
                                        }
                                        if (!obj.isNull("msg")) {
                                            String msg = obj.getString("msg");
                                            if (!TextUtils.isEmpty(msg)) {
                                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
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

    //需要申请的所有权限
    private String[] allPermissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //拒绝的权限集合
    public static List<String> deniedList = new ArrayList<>();
    /**
     * 申请定位权限
     * @param flag true为上传，false为下载
     */
    private void checkAuthority(boolean flag) {
        if (Build.VERSION.SDK_INT < 23) {
            if (flag) {
                dialogUpload();
            }else {
                dialogDownload();
            }
        }else {
            deniedList.clear();
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permission);
                }
            }
            if (deniedList.isEmpty()) {//所有权限都授予
                if (flag) {
                    dialogUpload();
                }else {
                    dialogDownload();
                }
            }else {
                String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
                if (flag) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, AuthorityUtil.AUTHOR_LOCATION);
                }else {
                    ActivityCompat.requestPermissions(getActivity(), permissions, AuthorityUtil.AUTHOR_STORAGE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AuthorityUtil.AUTHOR_LOCATION:
                if (grantResults.length > 0) {
                    boolean isAllGranted = true;//是否全部授权
                    for (int gResult : grantResults) {
                        if (gResult != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false;
                            break;
                        }
                    }
                    if (isAllGranted) {//所有权限都授予
                        dialogUpload();
                    }else {//只要有一个没有授权，就提示进入设置界面设置
//                        checkAuthority();
                    }
                }else {
                    for (String permission : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
//                            checkAuthority();
                            break;
                        }
                    }
                }
                break;
            case AuthorityUtil.AUTHOR_STORAGE:
                if (grantResults.length > 0) {
                    boolean isAllGranted = true;//是否全部授权
                    for (int gResult : grantResults) {
                        if (gResult != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false;
                            break;
                        }
                    }
                    if (isAllGranted) {//所有权限都授予
                        dialogDownload();
                    }else {//只要有一个没有授权，就提示进入设置界面设置
//                        checkAuthority();
                    }
                }else {
                    for (String permission : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
//                            checkAuthority();
                            break;
                        }
                    }
                }
                break;
        }
    }

}
