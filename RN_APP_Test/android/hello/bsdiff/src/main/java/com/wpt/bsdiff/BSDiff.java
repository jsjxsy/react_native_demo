package com.wpt.bsdiff;

/**
 * @author xiaosy
 * @create 2019-04-23
 * @Describe
 **/
public class BSDiff {
    static {
        System.loadLibrary("bsdiff");
    }
    /**
     * 将增量文件合成为新的Apk
     * @param oldApkPath 当前Apk路径
     * @param newApkPath 合成后的Apk保存路径
     * @param patchPath 增量文件路径
     * @return
     */
    public static native int patch(String oldApkPath, String newApkPath, String patchPath);
}
