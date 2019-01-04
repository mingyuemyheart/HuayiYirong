package com.huayi.shawn.yirong.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnWarningDetailActivity;
import com.huayi.shawn.yirong.adapter.ShawnWarningAdapter;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.WarningDto;
import com.huayi.shawn.yirong.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警-地图
 */
public class ShawnWarningMapFragment extends Fragment implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {

    private MapView mapView;//高德地图
    private AMap aMap;//高德地图
    private Marker selectMarker;
    private LatLng locationLatLng = new LatLng(24.880095, 102.832892);
    private List<WarningDto> warningList = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_warning_map, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAmap(view, savedInstanceState);
    }

    /**
     * 初始化高德地图
     */
    private void initAmap(View view, Bundle bundle) {
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(bundle);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 3.7f));
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkersToMap();
                CommonUtil.drawDistrict(getActivity(), "浙江", aMap);
            }
        });
    }

    /**
     * 移除地图上指定marker
     */
    private void removeMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        warningList.clear();
        warningList.addAll(getArguments().<WarningDto>getParcelableArrayList("dataList"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                removeMarkers();
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (WarningDto dto : warningList) {
                    MarkerOptions optionsTemp = new MarkerOptions();
                    optionsTemp.title(dto.lat+","+dto.lng);
                    optionsTemp.anchor(0.5f, 0.5f);
                    LatLng latlng = new LatLng(dto.lat, dto.lng);
                    optionsTemp.position(latlng);
                    builder.include(latlng);

                    View view = inflater.inflate(R.layout.shawn_warning_marker_icon, null);
                    ImageView ivMarker = view.findViewById(R.id.ivMarker);
                    Bitmap bitmap = null;
                    if (dto.color.equals(CONST.blue[0])) {
                        bitmap = CommonUtil.getImageFromAssetsFile(getActivity(),"warning/"+dto.type+CONST.blue[1]+CONST.imageSuffix);
                    }else if (dto.color.equals(CONST.yellow[0])) {
                        bitmap = CommonUtil.getImageFromAssetsFile(getActivity(),"warning/"+dto.type+CONST.yellow[1]+CONST.imageSuffix);
                    }else if (dto.color.equals(CONST.orange[0])) {
                        bitmap = CommonUtil.getImageFromAssetsFile(getActivity(),"warning/"+dto.type+CONST.orange[1]+CONST.imageSuffix);
                    }else if (dto.color.equals(CONST.red[0])) {
                        bitmap = CommonUtil.getImageFromAssetsFile(getActivity(),"warning/"+dto.type+CONST.red[1]+CONST.imageSuffix);
                    }
                    if (bitmap == null) {
                        bitmap = CommonUtil.getImageFromAssetsFile(getActivity(),"warning/"+"default"+CONST.imageSuffix);
                    }
                    ivMarker.setImageBitmap(bitmap);
                    optionsTemp.icon(BitmapDescriptorFactory.fromView(view));
                    Marker marker = aMap.addMarker(optionsTemp);
                    markers.add(marker);
                    expandMarker(marker);
                }
                if (warningList.size() > 0) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
                }
            }
        }).start();
    }

    private void expandMarker(Marker marker) {
        ScaleAnimation animation = new ScaleAnimation(0,1,0,1);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(300);
        marker.setAnimation(animation);
        marker.startAnimation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            selectMarker = marker;
            if (selectMarker.isInfoWindowShown()) {
                selectMarker.hideInfoWindow();
            }else {
                selectMarker.showInfoWindow();
            }
        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (selectMarker != null) {
            selectMarker.hideInfoWindow();
        }
    }

    @Override
    public View getInfoContents(final Marker marker) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.shawn_warning_marker_info, null);
        final List<WarningDto> infoList = new ArrayList<>();

        for (WarningDto dto : warningList) {
            if (TextUtils.equals(marker.getTitle(), dto.lat+","+dto.lng)) {
                infoList.add(dto);
            }
        }

        ListView mListView = view.findViewById(R.id.listView);
        ShawnWarningAdapter mAdapter = new ShawnWarningAdapter(getActivity(), infoList, true);
        mListView.setAdapter(mAdapter);
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        if (infoList.size() == 1) {
            params.height = (int) CommonUtil.dip2px(getActivity(), 30);
        }else if (infoList.size() == 2) {
            params.height = (int) CommonUtil.dip2px(getActivity(), 60);
        }else if (infoList.size() > 2){
            params.height = (int) CommonUtil.dip2px(getActivity(), 90);
        }
        mListView.setLayoutParams(params);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                WarningDto data = infoList.get(arg2);
                Intent intent = new Intent(getActivity(), ShawnWarningDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

}
