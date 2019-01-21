package com.huayi.shawn.yirong.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huayi.shawn.yirong.R;
import com.huayi.shawn.yirong.common.CONST;
import com.huayi.shawn.yirong.fragment.ShawnDownloadFragment;
import com.huayi.shawn.yirong.fragment.ShawnUploadFragment;
import com.huayi.shawn.yirong.view.MainViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源库-传输列表
 */
public class ShawnTranslationActivity extends ShawnBaseActivity implements View.OnClickListener {

    private Context context;
    private TextView tv1,tv2;
    private MainViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shawn_activity_translation);
        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        context = this;
        initWidget();
        initViewPager();
    }

    private void initWidget() {
        LinearLayout llBack = findViewById(R.id.llBack);
        llBack.setOnClickListener(this);
        tv1 = findViewById(R.id.tv1);
        tv1.setText("下载列表");
        tv1.setOnClickListener(new MyOnClickListener(0));
        tv2 = findViewById(R.id.tv2);
        tv2.setText("上传列表");
        tv2.setOnClickListener(new MyOnClickListener(1));
    }

    /**
     * 初始化viewPager
     */
    private void initViewPager() {
        Fragment fragment = new ShawnDownloadFragment();
        fragments.add(fragment);
        fragment = new ShawnUploadFragment();
        fragments.add(fragment);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setSlipping(true);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                tv1.setTextColor(Color.WHITE);
                tv2.setTextColor(getResources().getColor(R.color.text_color3));
                tv1.setBackgroundResource(R.drawable.shawn_bg_corner_left_press);
                tv2.setBackgroundResource(R.drawable.shawn_bg_corner_right);
            }else if (arg0 == 1) {
                tv1.setTextColor(getResources().getColor(R.color.text_color3));
                tv2.setTextColor(Color.WHITE);
                tv1.setBackgroundResource(R.drawable.shawn_bg_corner_left);
                tv2.setBackgroundResource(R.drawable.shawn_bg_corner_right_press);
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

    /**
     * 发送刷新数据广播
     */
    private void sendFreshBroadcast() {
        Intent intent = new Intent();
        intent.setAction(CONST.BROADCAST_REFRESH_RESOURCE);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sendFreshBroadcast();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                sendFreshBroadcast();
                break;

        }
    }

}
