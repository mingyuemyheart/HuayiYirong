package com.huayi.shawn.yirong.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnImageActivity;
import com.huayi.shawn.yirong.activity.ShawnSelectImageActivity;
import com.huayi.shawn.yirong.activity.ShawnSelectVideoActivity;
import com.huayi.shawn.yirong.adapter.ShawnResourceAdapter;
import com.huayi.shawn.yirong.adapter.ShawnSelectVideoAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.AuthorityUtil;
import com.huayi.shawn.yirong.util.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 资源库-个人库
 */
public class ShawnResourceFragment extends Fragment implements View.OnClickListener {

    private ShawnResourceAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局
    private String columnId,fileId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_resource, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                refresh();
            }
        });
    }

    private void refresh() {
        dataList.clear();
        OkHttpList();
    }

    private void initWidget(View view) {
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

        columnId = getArguments().getString("columnId");
        fileId = getArguments().getString("fileId");

        refresh();
    }

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
                if (TextUtils.equals(dto.fileType, "1")) {
                    intent = new Intent(getActivity(), ShawnImageActivity.class);
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    intent.putExtra(CONST.WEB_URL, dto.dataUrl);
                    startActivity(intent);
                }else if (TextUtils.equals(dto.fileType, "2")) {
                }else if (TextUtils.equals(dto.fileType, "3")) {
                }else if (TextUtils.equals(dto.fileType, "4")) {
                }else if (TextUtils.equals(dto.fileType, "5")) {
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int count;
        ShawnDto data;
        switch (v.getId()) {
            case R.id.ll1:
                dialogMkFiles();
                break;
            case R.id.ll2:
                dialogUpload("选择文件类型");
                break;
            case R.id.ll3:

                break;
            case R.id.ll4:
                checkAuthority(false);
                break;
            case R.id.ll5:
                count = 0;
                data = null;
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (dto.isSelected) {
                        data = dto;
                        count++;
                    }
                }
                if (count == 1) {
                    dialogDelete(data);
                }else if (count <= 0) {
                    Toast.makeText(getActivity(), "请选中一个文件", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "不能同时删除多个文件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll6:
                count = 0;
                data = null;
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
                case 1001://上传图片
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            String filePath = bundle.getString("filePath");
                            OkHttpUpload(filePath);
                        }
                    }
                    break;
                case 1002://上传图片
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            String filePath = bundle.getString("filePath");
                            OkHttpUpload(filePath);
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
        final String url = String.format("http://47.105.63.187:8081/interfaces/Resources/index?token=%s&cid=%s&pid=%s", MyApplication.TOKEN, columnId, fileId);
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
                                                if (!itemObj.isNull("name")) {
                                                    dto.title = itemObj.getString("name");
                                                }
                                                if (!itemObj.isNull("time")) {
                                                    dto.time = itemObj.getString("time");
                                                }
                                                if (!itemObj.isNull("type")) {
                                                    dto.fileType = itemObj.getString("type");
                                                }
                                                if (!itemObj.isNull("file_path")) {
                                                    dto.dataUrl = itemObj.getString("file_path");
                                                }
                                                dataList.add(dto);
                                            }
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
        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(columnId) || TextUtils.isEmpty(fileId)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/newfolder?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("filename", fileName);
        builder.add("cid", columnId);
        builder.add("pid", fileId);
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
                                                refresh();
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
    private void dialogUpload(String title) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_dialog_upload, null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
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
                startActivityForResult(new Intent(getActivity(), ShawnSelectVideoActivity.class), 1002);
            }
        });

        llFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "开发中，敬请期待！", Toast.LENGTH_SHORT).show();
            }
        });

        tvNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 文件上传
     * @param filePath
     */
    private void OkHttpUpload(String filePath) {
        if (TextUtils.isEmpty(columnId) || TextUtils.isEmpty(fileId) || TextUtils.isEmpty(filePath)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/uploadfiles?token="+MyApplication.TOKEN;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("cid", columnId);
        builder.addFormDataPart("fid", fileId);
        builder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
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
                                                refresh();
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
     * 下载文件
     */
    private void dialogDownload() {
        int count = 0;
        for (int i = 0; i < dataList.size(); i++) {
            ShawnDto dto = dataList.get(i);
            if (dto.isSelected) {
                count++;
            }
        }
        if (count <= 0) {
            Toast.makeText(getActivity(), "请选中一个文件", Toast.LENGTH_SHORT).show();
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
                    for (int i = 0; i < dataList.size(); i++) {
                        ShawnDto dto = dataList.get(i);
                        if (dto.isSelected) {
                            downloadFile(dto.dataUrl, dto.title);
                        }
                    }
                }
            });
        }
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
        File file = new File(files.getAbsolutePath()+"/"+fileName);
        if (!file.exists()) {
            return;
        }
        request.setDestinationUri(Uri.fromFile(file));//指定sdcard下载目录
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
     */
    private void dialogDelete(final ShawnDto data) {
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
        tvContent.setText("确定要删除"+"\""+data.title+"\""+"吗?");
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
                OkHttpDelete(data.id);
            }
        });
    }

    /**
     * 删除文件
     */
    private void OkHttpDelete(String fileId) {
        if (TextUtils.isEmpty(fileId)) {
            Toast.makeText(getActivity(), "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/removelist?token="+MyApplication.TOKEN;
        FormBody.Builder builder = new FormBody.Builder();
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
                                                refresh();
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
                                                refresh();
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
