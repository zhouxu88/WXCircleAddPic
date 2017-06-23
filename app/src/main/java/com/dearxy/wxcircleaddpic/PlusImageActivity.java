package com.dearxy.wxcircleaddpic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;

public class PlusImageActivity extends AppCompatActivity {

    private PhotoView photoView;
    private int position; //第几张图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_image);

        initView();
        String picPath = getIntent().getStringExtra(MainConstant.PIC_PATH); //图片路径
        position = getIntent().getIntExtra(MainConstant.POSITION, 0);
        Glide.with(this).load(picPath).into(photoView); //显示放大的图片
    }

    private void initView() {
        photoView = (PhotoView) findViewById(R.id.photoView);
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
}
