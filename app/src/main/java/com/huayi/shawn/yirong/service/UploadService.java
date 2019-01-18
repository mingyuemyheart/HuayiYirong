package com.huayi.shawn.yirong.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.util.ShawnRequestBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传服务
 */
public class UploadService extends Service {

    private Context context;

    public UploadService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OkHttpUpload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 文件上传
     */
    private void OkHttpUpload() {
        final List<ShawnDto> dataList = new ArrayList<>();
        dataList.addAll(CommonUtil.readUploadInfo(context));
        final List<ShawnDto> dataList1 = new ArrayList<>();
        final List<ShawnDto> dataList2 = new ArrayList<>();
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
            return;
        }
        final ShawnDto dto = dataList1.get(0);
        if (TextUtils.isEmpty(dto.columnId) || TextUtils.isEmpty(dto.pid) || TextUtils.isEmpty(dto.filePath)) {
            Toast.makeText(context, "数据异常，修改失败", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(dto.filePath);
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        final String url = "http://47.105.63.187:8081/interfaces/Resources/uploadfiles?token="+MyApplication.TOKEN;
        final ShawnRequestBody shawnRequestBody = new ShawnRequestBody(file, "application/octet-stream", new ShawnRequestBody.ProgressListener() {
            @Override
            public void transferred(final long pregressSize) {
                dto.percent = (int)(100*pregressSize/dto.fileSize);
                Log.e("uploadProgress", dto.percent+"");
                if (pregressSize >= dto.fileSize) {
                    dto.loadState = CONST.loadComplete;
                    dataList2.add(0, dto);
                    dataList1.remove(0);
                    CommonUtil.saveUploadInfo(context, dataList);
                    OkHttpUpload();
                }
                Intent intent = new Intent();
                intent.setAction(CONST.BROADCAST_UPLOAD_PROGRESS);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", dto);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }
        });
        String formData = String.format("form-data;name=file; filename=%s", file.getName());
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addPart(Headers.of("Content-Disposition",formData), shawnRequestBody);
        builder.addFormDataPart("cid", dto.columnId);
        builder.addFormDataPart("fid", dto.pid);
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
                    }
                });
            }
        }).start();
    }

}
