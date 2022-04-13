package com.zzr.cash_log;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Author: zzr
 * Date: 2021/11/17
 * Desc: 本地日志工具类
 */
public class LocalLogUtils {
    private final static String TAG = "LocalLogUtils";
    //每个小时的日志都是记录在同一个文件
    private final static String LOG_FILE_CREATE_TIME_FORMAT = "yyyy-MM-dd_HH";
    private final static String LOG_FILE_SUFFIX = "_log.txt";
    //日志记入的时间
    private final static String LOG_RECORD_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private String mLogDir;
    private String filePrefix;

    public void saveLogToFile(String content) {
        StringBuffer sb = new StringBuffer();
        long time = System.currentTimeMillis();
        sb.append(">>> 时间 ");
        sb.append(CrashUtils.formatDate(new Date(time), LOG_RECORD_TIME_FORMAT));
        sb.append(" >>> ");
        sb.append(content);
//        sb.append("\r\n");
        CrashUtils.writeToFile(mLogDir, generateLogFileName(filePrefix, time), sb.toString(), "utf-8");
    }

    //生成日志文件名
    private String generateLogFileName(String prefix, long time) {
        cleanLog(mLogDir);
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("_");
        sb.append(CrashUtils.formatDate(new Date(time), LOG_FILE_CREATE_TIME_FORMAT));
        sb.append(LOG_FILE_SUFFIX);
        return sb.toString();
    }

    public void setDirNameAndFilePrefix(String folderName, String prefix) {
        mLogDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName;
        filePrefix = prefix;
    }

    private static String getDirPath(String folderName) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + folderName;
    }

    public static void cleanLog(List<String> folderNames) {
        for (String folderName : folderNames) {
            if (folderName != null && !folderName.isEmpty()) {
                cleanLog(getDirPath(folderName));
            }
        }
    }

    private static void cleanLog(String folderPath) {
        File dir = new File(folderPath);
        if (dir.exists() && dir.isDirectory()) {
            for (String s : Objects.requireNonNull(dir.list())) {
                File logFile = new File(folderPath + "/" + s);
                if (needDelete(logFile.getName())) {
                    logFile.delete();
                }
            }
        }
    }

    private static Boolean needDelete(String fileName) {
        try {
            if (fileName == null || fileName.isEmpty()) return false;
            String[] nameSplit = fileName.split("_");
            if (nameSplit == null || nameSplit.length < 2) return true;
            else {
                String createTimeStr = nameSplit[1];
                if (createTimeStr == null || createTimeStr.isEmpty()) return true;
                else {
                    @SuppressLint("SimpleDateFormat")
                    Long createTime = Objects.requireNonNull(new SimpleDateFormat("yyyy-MM-dd").parse(createTimeStr)).getTime();
                    Long currentTime = System.currentTimeMillis();
                    return (currentTime - createTime) >= 3 * 24 * 60 * 60 * 1000;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "日志删除异常：", e);
            return true;
        }
    }
}
