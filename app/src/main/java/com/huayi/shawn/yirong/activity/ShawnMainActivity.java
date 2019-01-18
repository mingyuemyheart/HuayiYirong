package com.huayi.shawn.yirong.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.adapter.ShawnMainAdapter;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.util.AuthorityUtil;
import com.huayi.shawn.yirong.util.AutoUpdateUtil;
import com.huayi.shawn.yirong.util.CommonUtil;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.util.SecretUrlUtil;
import com.huayi.shawn.yirong.util.WeatherUtil;
import com.huayi.shawn.yirong.view.sofia.Sofia;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.weather.api.WeatherAPI;
import cn.com.weather.beans.Weather;
import cn.com.weather.constants.Constants;
import cn.com.weather.listener.AsyncResponseHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShawnMainActivity extends ShawnBaseActivity implements AMapLocationListener {

    private Context context;
    private ImageView ivPosition;
    private TextView tvPosition,tvTemp,tvPhe,tvTime;
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private ShawnMainAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局
    private long mExitTime;//记录点击完返回按钮后的long型时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_main);
        context = this;
        Sofia.with(this)
                .invasionStatusBar()//设置顶部状态栏缩进
                .statusBarBackground(Color.TRANSPARENT);//设置状态栏颜色
        MyApplication.addDestoryActivity(this, "ShawnMainActivity");
        initRefreshLayout();
        initWidget();
        initListView();
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
                refresh();
            }
        });
    }

    private void refresh() {
        checkAuthority();
        OkHttpList();
    }

    private void initWidget() {
        AutoUpdateUtil.checkUpdate(this, this, "110", getString(R.string.app_name), true);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.shawn_bg_main_title);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = width;
        params.height = width*71/125;
        imageView.setLayoutParams(params);
        ivPosition = findViewById(R.id.ivPosition);
        tvPosition = findViewById(R.id.tvPosition);
        tvTemp = findViewById(R.id.tvTemp);
        tvPhe = findViewById(R.id.tvPhe);
        TextView tvDate = findViewById(R.id.tvDate);
        tvDate.setText(sdf1.format(new Date()));
        tvTime = findViewById(R.id.tvTime);

        refresh();
    }

    private void initListView() {
        ListView listView = findViewById(R.id.listView);
        mAdapter = new ShawnMainAdapter(this, dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShawnDto dto = dataList.get(position);
                Intent intent = null;
                if (TextUtils.equals(dto.title, "设置")) {
                    intent = new Intent(context, ShawnSettingActivity.class);
                }else if (TextUtils.equals(dto.title, "稿件")) {
                    intent = new Intent(context, ShawnGaojianActivity.class);
                }else if (TextUtils.equals(dto.title, "资源库")) {
                    intent = new Intent(context, ShawnResourceActivity.class);
                }else if (TextUtils.equals(dto.title, "服务提示")) {
                    intent = new Intent(context, ShawnServiceActivity.class);
                }else if (TextUtils.equals(dto.title, "预警")) {
                    intent = new Intent(context, ShawnWarningActivity.class);
                }
                if (intent != null) {
                    intent.putExtra(CONST.ACTIVITY_NAME, dto.title);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (CommonUtil.isLocationOpen(this)) {
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();//初始化定位参数
            AMapLocationClient mLocationClient = new AMapLocationClient(this);//初始化定位
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setOnceLocation(true);//设置是否只定位一次,默认为false
            mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
            mLocationClient.setLocationListener(this);
            mLocationClient.startLocation();//启动定位
        }else {
            ivPosition.setImageResource(R.drawable.shawn_icon_position);
            tvPosition.setText("北京市");
            OkHttpGeo(116.407526,39.904030);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            ivPosition.setImageResource(R.drawable.shawn_icon_position);
            tvPosition.setText(amapLocation.getCity()+amapLocation.getDistrict());
            OkHttpGeo(amapLocation.getLongitude(), amapLocation.getLatitude());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出"+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return false;
    }

    /**
     * 获取天气数据
     */
    private void OkHttpGeo(final double lng, final double lat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpUtil.enqueue(new Request.Builder().url(SecretUrlUtil.geo(lng, lat)).build(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        String result = response.body().string();
                        if (!TextUtils.isEmpty(result)) {
                            try {
                                JSONObject obj = new JSONObject(result);
                                if (!obj.isNull("geo")) {
                                    JSONObject geoObj = obj.getJSONObject("geo");
                                    if (!geoObj.isNull("id")) {
                                        String cityId = geoObj.getString("id");
                                        getWeatherInfo(cityId);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void getWeatherInfo(final String cityId) {
        if (TextUtils.isEmpty(cityId)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherAPI.getWeather2(context, cityId, Constants.Language.ZH_CN, new AsyncResponseHandler() {
                    @Override
                    public void onComplete(final Weather content) {
                        super.onComplete(content);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String result = content.toString();
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);

                                        //实况信息
                                        if (!obj.isNull("l")) {
                                            JSONObject l = obj.getJSONObject("l");
                                            if (!l.isNull("l7")) {
                                                String time = l.getString("l7");
                                                if (!TextUtils.isEmpty(time)) {
                                                    tvTime.setText(time);
                                                }
                                            }
                                            if (!l.isNull("l1")) {
                                                String factTemp = WeatherUtil.lastValue(l.getString("l1"));
                                                if (!TextUtils.isEmpty(factTemp)) {
                                                    tvTemp.setText(factTemp+"°");
                                                }
                                            }
                                            if (!l.isNull("l5")) {
                                                String pheCode = WeatherUtil.lastValue(l.getString("l5"));
                                                if (!TextUtils.isEmpty(pheCode)) {
                                                    tvPhe.setText(getString(WeatherUtil.getWeatherId(Integer.valueOf(pheCode))));
                                                }
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

                    @Override
                    public void onError(Throwable error, String content) {
                        super.onError(error, content);
                    }
                });

            }
        }).start();
    }

    /**
     * 获取主界面数据
     */
    private void OkHttpList() {
        dataList.clear();
        ShawnDto dto = new ShawnDto();
        dto.title = "稿件";
        dataList.add(dto);
        dto = new ShawnDto();
        dto.title = "资源库";
        dataList.add(dto);
        dto = new ShawnDto();
        dto.title = "预警";
        dataList.add(dto);
        dto = new ShawnDto();
        dto.title = "服务提示";
        dataList.add(dto);
        dto = new ShawnDto();
        dto.title = "设置";
        dataList.add(dto);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

//        final String url ="http://47.105.63.187:8081/interfaces/menu/guidance?token="+MyApplication.TOKEN;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpUtil.enqueue(new Request.Builder().url(url).build(), new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                    }
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        if (!response.isSuccessful()) {
//                            return;
//                        }
//                        final String result = response.body().string();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!TextUtils.isEmpty(result)) {
//                                    try {
//                                        dataList.clear();
//                                        JSONArray array = new JSONArray(result);
//                                        for (int i = 0; i < array.length(); i++) {
//                                            ShawnDto dto = new ShawnDto();
//                                            JSONObject itemObj = array.getJSONObject(i);
//                                            if (!itemObj.isNull("name")) {
//                                                dto.title = itemObj.getString("name");
//                                            }
//                                            dataList.add(dto);
//                                        }
//                                        if (mAdapter != null) {
//                                            mAdapter.notifyDataSetChanged();
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                refreshLayout.setRefreshing(false);
//                            }
//                        });
//                    }
//                });
//            }
//        }).start();
    }

    //需要申请的所有权限
    private String[] allPermissions = new String[] {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
    };

    //拒绝的权限集合
    public static List<String> deniedList = new ArrayList<>();
    /**
     * 申请定位权限
     */
    private void checkAuthority() {
        if (Build.VERSION.SDK_INT < 23) {
            startLocation();
        }else {
            deniedList.clear();
            for (String permission : allPermissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permission);
                }
            }
            if (deniedList.isEmpty()) {//所有权限都授予
                startLocation();
            }else {
                String[] permissions = deniedList.toArray(new String[deniedList.size()]);//将list转成数组
                ActivityCompat.requestPermissions(ShawnMainActivity.this, permissions, AuthorityUtil.AUTHOR_LOCATION);
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
                        startLocation();
                    }else {//只要有一个没有授权，就提示进入设置界面设置
                        checkAuthority();
                    }
                }else {
                    for (String permission : permissions) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(ShawnMainActivity.this, permission)) {
                            checkAuthority();
                            break;
                        }
                    }
                }
                break;
        }
    }

}
