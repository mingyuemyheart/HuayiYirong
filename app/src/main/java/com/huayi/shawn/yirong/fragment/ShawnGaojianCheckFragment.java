package com.huayi.shawn.yirong.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.adapter.ShawnGaojianCheckAdapter;
import com.huayi.shawn.yirong.common.MyApplication;
import com.huayi.shawn.yirong.dto.ShawnDto;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.util.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 稿件-审核
 */
public class ShawnGaojianCheckFragment extends Fragment {

    private ShawnGaojianCheckAdapter mAdapter;
    private List<ShawnDto> dataList = new ArrayList<>();
    private SwipeRefreshLayout refreshLayout;//下拉刷新布局

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_gaojian, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshLayout(view);
        initListView(view);
        refresh();
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

    private void initListView(View view) {
        ListView listView = view.findViewById(R.id.listView);
        mAdapter = new ShawnGaojianCheckAdapter(getActivity(), dataList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < dataList.size(); i++) {
                    ShawnDto dto = dataList.get(i);
                    if (position == i) {
                        dto.isSelected = !dto.isSelected;
                    }else {
                        dto.isSelected = false;
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void OkHttpList() {
        final String url = "http://47.105.63.187:8081/interfaces/article/getverify?token="+MyApplication.TOKEN;
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
                                                if (!itemObj.isNull("title")) {
                                                    dto.title = itemObj.getString("title");
                                                }
                                                if (!itemObj.isNull("brief")) {
                                                    dto.content = itemObj.getString("brief");
                                                }
                                                if (!itemObj.isNull("time")) {
                                                    dto.time = itemObj.getString("time");
                                                }
                                                if (!itemObj.isNull("state")) {
                                                    dto.state = itemObj.getString("state");
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

}
