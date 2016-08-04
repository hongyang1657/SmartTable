package com.byids.hy.smarttable;

import android.app.Application;

import com.midea.msmartsdk.openapi.MSmartSDK;

/**
 * Created by hy on 2016/7/18.
 */
public class MideaApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        MSmartSDK.getInstance().initSDKWithAppID(this.getApplicationContext(), "1000", "2f39d871a38a4841aab3be3837e39cf4", "0");
    }
}
