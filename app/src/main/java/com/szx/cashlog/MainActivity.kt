package com.szx.cashlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zzr.cash_log.LocalLogUtils

class MainActivity : AppCompatActivity() {
    private val logUtil by lazy { LocalLogUtils() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logUtil.setDirNameAndFilePrefix("LOG", "error")
        logUtil.saveLogToFile("这里有个错误信息，记录下来")
    }
}