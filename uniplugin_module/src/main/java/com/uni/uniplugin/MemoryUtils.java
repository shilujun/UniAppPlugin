package com.uni.uniplugin;

import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Create by tengtao
 * on 2022/4/27 20:45
 */
public class MemoryUtils {
    /**
     * @return
     */
    public static String getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2 = "";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                if (str2.contains("MemTotal:")) {
                    return str2.replace("MemTotal:", "").replace("kB", "").trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

}