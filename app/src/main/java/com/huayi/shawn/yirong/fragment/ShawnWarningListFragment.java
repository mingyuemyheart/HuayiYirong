package com.huayi.shawn.yirong.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.activity.ShawnWarningDetailActivity;
import com.huayi.shawn.yirong.adapter.ShawnWarningAdapter;
import com.huayi.shawn.yirong.dto.WarningDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警-列表
 */
public class ShawnWarningListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shawn_fragment_warning_list, null);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListView(view);
    }

    private void initListView(View view) {
        final List<WarningDto> warningList = new ArrayList<>();
        warningList.addAll(getArguments().<WarningDto>getParcelableArrayList("dataList"));
        ListView listView = view.findViewById(R.id.listView);
        ShawnWarningAdapter mAdapter = new ShawnWarningAdapter(getActivity(), warningList, false);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WarningDto data = warningList.get(position);
                Intent intent = new Intent(getActivity(), ShawnWarningDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
