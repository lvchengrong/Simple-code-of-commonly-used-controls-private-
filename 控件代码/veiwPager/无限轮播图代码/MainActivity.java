package recycleviewpager.baidu.com.recycleviewpager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager vp_viewpager;
    private ArrayList<ImageView> imas;
    private LinearLayout ll_point;
    private ImageView iv_redpoint;
    private int newposition;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            vp_viewpager.setCurrentItem(vp_viewpager.getCurrentItem() + 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //请求不要title
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initView();
        initData();
        initAdapter();
        initListener();
        initState();

    }

    private void initView() {
        vp_viewpager = (ViewPager) findViewById(R.id.vp_viewpager);
        ll_point = (LinearLayout) findViewById(R.id.ll_point);
        iv_redpoint = (ImageView) findViewById(R.id.iv_redpoint);
    }


    private void initData() {

        int[] images = {R.drawable.test1, R.drawable.test2, R.drawable.test3, R.drawable.test4};
        imas = new ArrayList<ImageView>();
        for (int i = 0; i < images.length; i++) {
            ImageView image = new ImageView(this);
//            image.setImageResource(images[i]);图片不能占满imageview
            image.setBackgroundResource(images[i]);//图片填满整个imageview
            imas.add(image);

            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
            if (i != 0) {
                params.leftMargin = 30;
            }
            point.setLayoutParams(params);
            ll_point.addView(point);

        }
    }

    private void initAdapter() {
        vp_viewpager.setAdapter(new MyPagerAdapter());
    }

    private void initListener() {
        vp_viewpager.addOnPageChangeListener(new MyListener());
    }

    //状态初始化
    private void initState() {
        vp_viewpager.setCurrentItem(500000 / 2 - 50000 % imas.size());
        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 500000;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            newposition = position % (imas.size());

            ImageView imageView = imas.get(newposition);
            container.addView(imageView);

            //实现触摸后自动轮播停止,需要处理图片的点击事件
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //事件取消(由父容器通知),当手指在图片上移动时,防止自动轮播开始
                        case MotionEvent.ACTION_CANCEL:
                            handler.removeCallbacksAndMessages(null);
                            break;
                        case MotionEvent.ACTION_UP:
                            handler.sendMessageDelayed(Message.obtain(), 2000);
                            break;
                    }
                    //返回false,为了能触发setOnClickListener点击事件,返回true,则不能触发点击事件
                    return false;
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "我是第"+(position%imas.size()+1)+"张图片", Toast.LENGTH_SHORT).show();
                }
            });

            return imageView;
        }
    }

    private class MyListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_redpoint.getLayoutParams();
            if (position % imas.size() != imas.size() - 1) {
                params.leftMargin = (int) ((position % imas.size() + positionOffset) * 60);
            } else {
                //防止手动从右向左时红点显示异常
                params.leftMargin = (imas.size() - 1) * 60;
            }
            iv_redpoint.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //实现无限轮播
            handler.removeCallbacksAndMessages(null);
            handler.sendMessageDelayed(Message.obtain(), 2000);
        }
    }

}
