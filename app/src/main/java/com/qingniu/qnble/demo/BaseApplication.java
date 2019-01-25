package com.qingniu.qnble.demo;

import android.app.Application;
import android.util.Log;

import com.qingniu.qnble.utils.QNLogUtils;
import com.yolanda.health.qnblesdk.listener.QNResultCallback;
import com.yolanda.health.qnblesdk.out.QNBleApi;

/**
 * @author: hekang
 * @description:
 * @date: 2018/03/21 20:20
 */

public class BaseApplication extends Application {

    public String mAppId = "123456789";

    @Override
    public void onCreate() {
        super.onCreate();
        String encryptPath = "file:///android_asset/123456789.qn";
//
        QNLogUtils.setLogEnable(true);
        QNBleApi mQNBleApi = QNBleApi.getInstance(this);
        mQNBleApi.initSdk(mAppId, encryptPath, new QNResultCallback() {
            @Override
            public void onResult(int code, String msg) {
                Log.d("BaseApplication", "初始化文件" + msg);
            }
        });

        instance = this;

    }

    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }
}
