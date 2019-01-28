package com.huayi.shawn.yirong.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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
    private int index = 0;//list下标
    private List<ShawnDto> dataList = new ArrayList<>();

    public UploadService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (index >= dataList.size()) {
            index = 0;
        }
        dataList.clear();
        dataList.addAll(CommonUtil.readUploadInfo(context));
        OkHttpUpload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.saveUploadInfo(context, dataList);
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
        Log.e("index", index+"");
        if (dataList.size() <= 0 || index >= dataList.size()) {
            return;
        }
        final ShawnDto dto = dataList.get(index);
        if (dto.loadState == CONST.loadComplete) {
            index++;
            OkHttpUpload();
            return;
        }
        if (TextUtils.isEmpty(dto.columnId) || TextUtils.isEmpty(dto.pid) || TextUtils.isEmpty(dto.filePath)) {
            index++;
            OkHttpUpload();
            return;
        }
        File file = new File(dto.filePath);
        if (!file.exists()) {//文件不存在
            index++;
            OkHttpUpload();
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
                    CommonUtil.saveUploadInfo(context, dataList);
                    index++;
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
                        //发送刷新数据广播，刷新资源库界面数据，为了停留在资源库界面刷新所用
                        Intent intent1 = new Intent();
                        intent1.setAction(CONST.BROADCAST_REFRESH_RESOURCE);
                        sendBroadcast(intent1);
                    }
                });
            }
        }).start();
    }

}
