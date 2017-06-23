package com.dearxy.wxcircleaddpic;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 仿微信朋友圈添加图片的效果
 */
public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_CODE = 100; //选择照片
    private static final int REQUEST_PREVIEW_CODE = 101; //预览
    private static final int TAKE_PHOTO_REQUEST_CODE = 102; //读取相册权限的请求码
    private Context mContext;
    private GridView gridView;
    private List<String> mPicList; //上传的图片凭证的数据源
    private GridViewAdapter mGridViewAdapter; //展示上传的图片的适配器
    private int proofPicCount; //已经选择的凭证图片的数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        gridView = (GridView) findViewById(R.id.gridView);
        initGridView();
    }

    //初始化展示上传图片的GridView
    private void initGridView() {
        mPicList = new ArrayList<>();
        mGridViewAdapter = new GridViewAdapter(mContext, mPicList);
        gridView.setAdapter(mGridViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //添加凭证图片
                    checkPhotoPermission();
                } else {
                    //查看大图
                    Intent intent = new Intent(mContext, PlusImageActivity.class);
                    //图片的路径
                    intent.putExtra(MainConstant.PIC_PATH, mPicList.get(position));
                    intent.putExtra(MainConstant.POSITION, position);
                    startActivityForResult(intent, MainConstant.REQUEST_CODE_MAIN);
                }
            }
        });
    }

    /**
     * 检查读取相册的权限
     */
    private void checkPhotoPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(mContext, mPermissionList)) {
                //已经同意过
                selectPic();
            } else {
                //未同意过,或者说是拒绝了，再次申请权限
                EasyPermissions.requestPermissions(
                        this,  //上下文
                        "打开相册需要读取sd卡的权限", //提示文言
                        TAKE_PHOTO_REQUEST_CODE, //请求码
                        mPermissionList //权限列表
                );
            }
        } else {
            //6.0以下，不需要授权
            selectPic();
        }
    }

    //打开相册选择凭证图片，最多5张
    private void selectPic() {
        PhotoPickerIntent intent = new PhotoPickerIntent(mContext);
        intent.setSelectModel(SelectModel.MULTI);
        intent.setShowCarema(true); // 是否显示拍照， 默认false
        intent.setMaxTotal(MainConstant.MAX_SELECT_PIC_NUM); // 最多选择照片数量，默认为9
        startActivityForResult(intent, REQUEST_CAMERA_CODE);
    }

    // 处理返回照片地址
    private void refreshAdapter(final ArrayList<String> paths) {
        for (int i = 0; i < paths.size(); i++) {
            Log.i(TAG, "path:---->" + paths.get(i));
            proofPicCount++;
            if (proofPicCount <= MainConstant.MAX_SELECT_PIC_NUM) {
                //添加图片到GridView
                mPicList.add(paths.get(i));
            }
        }
        mGridViewAdapter.notifyDataSetChanged();
    }

    //同意授权
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        Log.i(TAG, "onPermissionsGranted:" + requestCode + ":" + list.size());
        selectPic();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show(); //打开系统设置，手动授权
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE:
                    //拒绝授权后，从系统设置了授权后，返回APP进行相应的操作
                    Log.i(TAG, "onPermissionsDenied:------>自定义设置授权后返回APP");
                    selectPic();
                    break;
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    refreshAdapter(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                // 预览
                case REQUEST_PREVIEW_CODE:
                    refreshAdapter(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
            }
        }
        if (requestCode == MainConstant.REQUEST_CODE_MAIN && resultCode == MainConstant.RESULT_CODE_PLUS_IMG) {
            //查看大图页面删除了图片
            int position = data.getIntExtra(MainConstant.POSITION, 0); //要删除的图片的位置
            mPicList.remove(position);
            mGridViewAdapter.notifyDataSetChanged();
        }
    }
}
