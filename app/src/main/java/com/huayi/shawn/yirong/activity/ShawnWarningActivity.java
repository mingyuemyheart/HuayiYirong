package com.huayi.shawn.yirong.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.dto.WarningDto;
import com.huayi.shawn.yirong.fragment.ShawnWarningListFragment;
import com.huayi.shawn.yirong.fragment.ShawnWarningMapFragment;
import com.huayi.shawn.yirong.util.OkHttpUtil;
import com.huayi.shawn.yirong.view.MainViewPager;
import com.wang.avi.AVLoadingIndicatorView;

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
 * 预警
 */
public class ShawnWarningActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView iv1,iv2;
    private TextView tv1,tv2;
    private List<WarningDto> warningList = new ArrayList<>();
    private AVLoadingIndicatorView loadingView;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_warning);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = this;
        initWidget();
    }

    private void initWidget() {
        loadingView = findViewById(R.id.loadingView);
        TextView tvTitle = findViewById(R.id.tvTitle);
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        LinearLayout ll1 = findViewById(R.id.ll1);
        ll1.setOnClickListener(new MyOnClickListener(0));
        LinearLayout ll2 = findViewById(R.id.ll2);
        ll2.setOnClickListener(new MyOnClickListener(1));
        iv1 = findViewById(R.id.iv1);
        iv2 = findViewById(R.id.iv2);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);

        String title = getIntent().getStringExtra(CONST.ACTIVITY_NAME);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }

        OkHttpWarning();
    }

    /**
     * 获取预警信息
     */
    private void OkHttpWarning() {
        final String url = "http://decision-admin.tianqi.cn/Home/test/yrGetWarns/proid/1001?org_flag=1";
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(result)) {
                                    try {
                                        JSONObject obj = new JSONObject(result);
                                        if (!obj.isNull("list")) {
                                            warningList.clear();
                                            JSONArray jsonArray = obj.getJSONArray("list");
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONArray tempArray = jsonArray.getJSONArray(i);
                                                WarningDto dto = new WarningDto();
                                                dto.html = tempArray.getString(1);
                                                String[] array = dto.html.split("-");
                                                String item0 = array[0];
                                                String item1 = array[1];
                                                String item2 = array[2];

                                                dto.item0 = item0;
                                                dto.provinceId = item0.substring(0, 2);
                                                dto.type = item2.substring(0, 5);
                                                dto.color = item2.substring(5, 7);
                                                dto.time = item1;
                                                dto.lng = tempArray.getDouble(2);
                                                dto.lat = tempArray.getDouble(3);
                                                dto.name = tempArray.getString(0);

                                                warningList.add(dto);
                                            }

                                            initViewPager();

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                loadingView.setVisibility(View.GONE);
                            }
                        });
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        Fragment fragment = new ShawnWarningMapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) warningList);
        fragment.setArguments(bundle);
        fragments.add(fragment);
        fragment = new ShawnWarningListFragment();
        bundle = new Bundle();
        bundle.putParcelableArrayList("dataList", (ArrayList<? extends Parcelable>) warningList);
        fragment.setArguments(bundle);
        fragments.add(fragment);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setSlipping(false);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                iv1.setImageResource(R.drawable.shawn_icon_map_press);
                iv2.setImageResource(R.drawable.shawn_icon_list);
                tv1.setTextColor(0xff628BF0);
                tv2.setTextColor(getResources().getColor(R.color.text_color3));
            }else if (arg0 == 1) {
                iv1.setImageResource(R.drawable.shawn_icon_map);
                iv2.setImageResource(R.drawable.shawn_icon_list_press);
                tv1.setTextColor(getResources().getColor(R.color.text_color3));
                tv2.setTextColor(0xff628BF0);
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /**
     * 头标点击监听
     * @author shawn_sun
     */
    private class MyOnClickListener implements View.OnClickListener {
        private int index;

        private MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true);
            }
        }
    }

    /**
     * @ClassName: MyPagerAdapter
     * @Description: TODO填充ViewPager的数据适配器
     * @author Panyy
     * @date 2013 2013年11月6日 下午2:37:47
     *
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(fragments.get(position).getView());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) { // 如果fragment还没有added
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.add(fragment, fragment.getClass().getSimpleName());
                ft.commit();
                /**
                 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
                 * 会在进程的主线程中,用异步的方式来执行。
                 * 如果想要立即执行这个等待中的操作,就要调用这个方法(只能在主线程中调用)。
                 * 要注意的是,所有的回调和相关的行为都会在这个调用中被执行完成,因此要仔细确认这个方法的调用位置。
                 */
                getFragmentManager().executePendingTransactions();
            }

            if (fragment.getView().getParent() == null) {
                container.addView(fragment.getView()); // 为viewpager增加布局
            }
            return fragment.getView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                finish();
                break;

        }
    }

}
