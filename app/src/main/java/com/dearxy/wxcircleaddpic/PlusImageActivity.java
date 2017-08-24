package com.dearxy.wxcircleaddpic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class PlusImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager; //展示图片的ViewPager
    private TextView positionTv; //图片的位置，第几张图片
    private List<String> imgList; //图片的数据源
    private int position; //第几张图片
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_image);

        imgList = getIntent().getStringArrayListExtra(MainConstant.IMG_LIST);
        position = getIntent().getIntExtra(MainConstant.POSITION, 0);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        positionTv = (TextView) findViewById(R.id.position_tv);
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.delete_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除图片
                deletePic();
            }
        });
        viewPager.addOnPageChangeListener(this);
        mAdapter = new ViewPagerAdapter(this, imgList);
        viewPager.setAdapter(mAdapter);
        if (position < 0) {
            position = 0;
        }
        if (position >= viewPager.getAdapter().getCount()) {
            position = viewPager.getAdapter().getCount() - 1;
        }
        viewPager.setCurrentItem(position); //设置当前item
        positionTv.setText(position + 1 + "/" + imgList.size());
    }

    //删除图片
    private void deletePic() {
        CancelOrOkDialog dialog = new CancelOrOkDialog(this, "要删除这张图片吗?") {
            @Override
            public void ok() {
                super.ok();
                dismiss();
                Intent intent = getIntent();
                intent.putExtra(MainConstant.POSITION, position);
                setResult(MainConstant.RESULT_CODE_PLUS_IMG, intent);
                finish();
            }
        };
        dialog.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        positionTv.setText(position + 1 + "/" + imgList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
