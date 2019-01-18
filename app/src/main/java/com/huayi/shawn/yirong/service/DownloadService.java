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

    public DownloadService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        OkHttpDownload();
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
     * 下载文件
     */
    private void OkHttpDownload() {
        final List<ShawnDto> dataList = new ArrayList<>();
        dataList.addAll(CommonUtil.readDownloadInfo(context));
        final List<ShawnDto> dataList1 = new ArrayList<>();
        final List<ShawnDto> dataList2 = new ArrayList<>();
        dataList1.clear();
        dataList2.clear();
        //按照loadState区分正在下载、下载完成
        for (int i = 0; i < dataList.size(); i++) {
            ShawnDto dto = dataList.get(i);
            if (dto.loadState == CONST.loadComplete) {
                dataList2.add(dto);
            }else {
                dataList1.add(dto);
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataList1.size() <= 0) {
                    return;
                }
                final ShawnDto dto = dataList1.get(0);
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
                                    dataList2.add(0, dto);
                                    dataList1.remove(0);
                                    CommonUtil.saveDownloadInfo(context, dataList);
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
