package com.uni.uniplugin;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Create by tengtao
 * on 2022/4/27 20:36
 */
public class PhoneUtils {

    /**
     * SIM的状态信息：
     * SIM_STATE_UNKNOWN         未知状态 0
     SIM_STATE_ABSENT          没插卡 1
     SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2
     SIM_STATE_PUK_REQUIRED    锁定状态，需要用户的PUK码解锁 3
     SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4
     SIM_STATE_READY           就绪状态 5
     */
    public static int getSimState(Context context){
       return getTelephonyManager(context).getSimState();

    }

    private static TelephonyManager getTelephonyManager(Context context){
      return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

    }

}