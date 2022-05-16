package com.uni.uniplugin;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;

/**
 * Create by tengtao
 * on 2022/4/27 19:53
 */
public class DeviceModule extends UniModule {

    private static final int DEVICEINFO_UNKNOWN = -1;

    /**
     * cpu 指定指令集
     * cpu 核数
     * cpu 最小频率
     * cpu 最大频率
     * sim卡状态
     * imei1
     * imei2
     * network 标识无法获取
     * ram 大小
     * rom 大小
     *
     * @return
     */
    @UniJSMethod(uiThread = true)
    public void getDeviceInfo(UniJSCallback callback) {
        Context context = mUniSDKInstance.getContext();
        JSONObject resultObj = new JSONObject();

        //cpu 指定指令集
        resultObj.put("cpu_abi", getABIs());
        //cpu  核数、cpu 最小频率、cpu 最大频率
        resultObj.put("cpu", getCPU());
        //sim卡状态、 imei1、 imei2
        resultObj.put("telephony", getTelephony(context));
        resultObj.put("memory", getMemory());

        callback.invoke(resultObj);
    }


    /**
     * 获取
     *
     * @return
     */
    private String[] getABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            return new String[]{Build.CPU_ABI};
        }
    }

    /**
     * cpu  核数、cpu 最小频率、cpu 最大频率
     *
     * @return
     */
    private JSONObject getCPU() {
        JSONObject cpu = new JSONObject();
        cpu.put("cpu_processors_count", Runtime.getRuntime().availableProcessors());
        cpu.put("cpu_min_freq", DevicesUtils.getMinCpuFreq());
        cpu.put("cpu_max_freq", DevicesUtils.getMaxCpuFreq());
        return cpu;
    }

    /**
     * @return
     */
    private JSONObject getTelephony(Context context) {
        JSONObject cpu = new JSONObject();
        cpu.put("sim_state", PhoneUtils.getSimState(context));
        return cpu;
    }


    /**
     * ram 大小 单位kB
     * rom 大小 单位kB
     *
     * @return
     */
    private JSONObject getMemory() {
        JSONObject cpu = new JSONObject();
        cpu.put("ram_total", MemoryUtils.getTotalMemory());

        try {
            long romTotal = MemoryUtils.getTotalInternalMemorySize() / 1024;
            cpu.put("rom_total", romTotal + "");
        } catch (Exception e) {
            cpu.put("rom_total", "0");

            e.printStackTrace();
        }
        return cpu;
    }


}