package com.huayi.shawn.yirong.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.util.CommonUtil;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.util.ShawnResponseBody;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载服务
 */
public class DownloadService extends Service {

    private Context context;
    private int index = 0;//list下标
    private List<ShawnDto> dataList = new ArrayList<>();

    public DownloadService() {

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
        dataList.addAll(CommonUtil.readDownloadInfo(context));
        OkHttpDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.saveDownloadInfo(context, dataList);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 下载文件
     */
    private void OkHttpDownload() {
        Log.e("index", index+"");
        if (dataList.size() <= 0 || index >= dataList.size()) {
            return;
        }
        final ShawnDto dto = dataList.get(index);
        if (dto.loadState == CONST.loadComplete) {
            index++;
            OkHttpDownload();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(dto.filePath).build(), new Callback() {
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
                                dto.percent = (int)(100*pregressSize/totalSize);
                                Log.e("downpro", dto.percent+"");
                                if (success) {
                                    dto.loadState = CONST.loadComplete;
                                    CommonUtil.saveDownloadInfo(context, dataList);
                                    index++;
                                    OkHttpDownload();
                                }
                                Intent intent = new Intent();
                                intent.setAction(CONST.BROADCAST_DOWNLOAD_PROGRESS);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("data", dto);
                                intent.putExtras(bundle);
                                sendBroadcast(intent);
                            }
                        });

                        final byte[] bytes = body.bytes();
                        try {
                            File files = new File(CONST.DOWNLOAD_ADDR);
                            if (!files.exists()) {
                                files.mkdirs();
                            }
                            File file = new File(files.getAbsolutePath()+"/"+dto.title);
                            FileOutputStream fos = new FileOutputStream(file);
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
        }).start();
    }

}
