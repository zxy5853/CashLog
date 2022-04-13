package com.zzr.cash_log.email

import android.annotation.SuppressLint
import android.os.Environment
import android.provider.Settings
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Author: zzr
 * Date: 2021/11/30
 * Desc: 邮件发送工具类
 */
object MailUtils {
    val logFolderPath = Environment.getExternalStorageDirectory().absolutePath + "/SZX/order_log/"
    val zipPath = Environment.getExternalStorageDirectory().absolutePath + "/SZX/"
//    private const val zipName = "order_log.zip"

    //    val zipFullPath = zipPath + zipName
    private const val LOG_FILE_CREATE_TIME_FORMAT = "yyyy-MM-dd_HH"

    @SuppressLint("SimpleDateFormat")
    private val dateFormat1 = SimpleDateFormat(LOG_FILE_CREATE_TIME_FORMAT)

    /**
     * 将指定时间段内的log打包并通过邮件发送，需要在子线程调用
     * @param startTime yyyy-MM-dd_HH
     * @param endTime yyyy-MM-dd_HH
     * @param zipFileName deviceNo_endTime
     *
     */
    fun zipAndSendLogByMail(startTime: String?, endTime: String?, zipFileName: String) {
        val temp = getLogFiles(startTime, endTime)
        Log.e("MailUtils", temp?.toString().orEmpty())
        if (temp != null) {
            val zipFullPath = zipPath + zipFileName
            val zipSuccess = ZipUtils.zipFiles(temp, zipFullPath)
            if (zipSuccess) {
                sendFileMail(fileNames = arrayListOf(zipFileName))
            }
        }
    }

    fun getZipFile(startTime: String?, endTime: String?, zipFileName: String): File? {
        val temp = getLogFiles(startTime, endTime)
        Log.e("MailUtils", temp?.toString().orEmpty())
        if (temp != null) {
            val zipFullPath = zipPath + zipFileName
            val zipSuccess = ZipUtils.zipFiles(temp, zipFullPath)
            return if (zipSuccess) {
                File(zipFullPath)
            } else null
        }
        return null
    }

    fun sendFileMail(
        mailServerHost: String = "smtp.lehui.com",
        mailServerPost: String = "25",
        userName: String = "szx_support@lehui.com",
        passWord: String = "********",
        fromAddress: String = "szx_support@lehui.com",
        emailReceiver: Array<String> = arrayOf("邮箱1", "邮箱2"),
        fileNames: ArrayList<String>
//        fileName: String = zipPath
    ) {
        //先设置邮件
        val info = MailSendInfo()
        info.mailServerHost = mailServerHost
        info.mailServerPost = mailServerPost
        info.isValidate = false
        info.userName = userName
        info.passWord = passWord//邮箱密码
        info.fromAddress = fromAddress
        //以下三个内容是需要修改的
        info.toAddresses = emailReceiver
        info.subject = "获取设备指令日志"
        info.content = "指令日志上报"

        val senMail = MultiMailSend()  //这个类用来发送邮件
//        senMail.setEmailSentListener { deleteZip() }
        senMail.sendAttachment(info, fileNames)
    }

    fun getLogFiles(startTime: String?, endTime: String?): ArrayList<String>? {
        val logFolder = File(logFolderPath)
        if (logFolder.exists() && logFolder.isDirectory) {
            val logFiles = logFolder.listFiles()
            if (logFiles != null && logFiles.isNotEmpty()) {
                val fileMap = HashMap<String, String>()
                logFiles.forEach {
                    fileMap[it.name.replace("order_", "").replace("_order.txt", "")] =
                        it.absolutePath
                }
                if (fileMap.isNotEmpty()) {
                    val fileKeys = fileMap.keys
                    val files = ArrayList<String>()
                    fileKeys.forEach {
                        if (isInTimeRange(startTime, endTime, it)) {
                            files.add(fileMap[it].orEmpty())
                        }
                    }
                    return files
                }
            }
            return null
        }
        return null
    }

    private fun isInTimeRange(startTime: String?, endTime: String?, targetTime: String): Boolean {
        if (startTime != null) {
            var endTempTime = endTime
            if (endTempTime == null) {
                endTempTime = startTime
            }
            return if (startTime == endTempTime) {
                //只需要一个文件
                startTime == targetTime
            } else {
                //多个文件 判断文件时间是否在时间段内
                isTargetTime(startTime, endTempTime, targetTime)
            }
        }
        return false
    }

    private fun isTargetTime(startTime: String, endTime: String, targetTime: String): Boolean {
        return try {
            val startDate = dateFormat1.parse(startTime)!!.time
            val endDate = dateFormat1.parse(endTime)!!.time
            val targetDate = dateFormat1.parse(targetTime)!!.time
            targetDate in startDate..endDate
        } catch (e: Exception) {
            Log.e("MailUtils", "获取目标Log失败：", e)
            false
        }
    }

    private fun isNeedTime(startTime: String, endTime: String, targetTime: String): Boolean {
        try {
            val startCal = Calendar.getInstance()
            val endCal = Calendar.getInstance()
            val targetCal = Calendar.getInstance()
            val startDate = dateFormat1.parse(startTime)!!
            val endDate = dateFormat1.parse(endTime)!!
            val targetDate = dateFormat1.parse(targetTime)!!
            startCal.time = startDate
            endCal.time = endDate
            targetCal.time = targetDate

            if (isSameDay(startCal, endCal)) {
                //开始和结束是同一天，只需要取开始和结束的小时之间的log
                return if (isSameDay(startCal, targetCal)) {
                    //同一天,比较小时
                    targetCal[Calendar.HOUR_OF_DAY] in startCal[Calendar.HOUR_OF_DAY]..endCal[Calendar.HOUR_OF_DAY]
                } else {
                    false
                }
            } else {
                //不是同一天，看日期是否在开始与结束之间
                return when {
                    isSameDay(startCal, targetCal) -> {
                        //与开始日期是同一天，比较小时
                        targetCal[Calendar.HOUR_OF_DAY] >= startCal[Calendar.HOUR_OF_DAY]
                    }
                    isSameDay(targetCal, endCal) -> {
                        //与结束日期是同一天
                        targetCal[Calendar.HOUR_OF_DAY] <= endCal[Calendar.HOUR_OF_DAY]
                    }
                    else -> {
                        targetDate.after(startDate) && targetDate.before(endDate)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MailUtils", "获取目标Log失败：", e)
            return false
        }
    }


    private fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
        return if (cal1 != null && cal2 != null) {
            cal1[Calendar.ERA] == cal2[Calendar.ERA]
                    && cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
                    && cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
        } else {
            false
        }
    }

    fun deleteZip(zipFilePath: String) {
        val zipFile = File(zipFilePath)
        if (zipFile.exists()) {
            zipFile.delete()
        }
    }

}