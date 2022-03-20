package com.example.filedemo3.crash


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build

import java.io.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


/**
 * @ClassName MyUncaughtExceptionHandler
 * @Description 全局捕捉异常
 */
class CrashHandle2(  // 上下文
    private val context: Context
) : Thread.UncaughtExceptionHandler {
    // 会输出到文件中
    private lateinit var stringBuilder: StringBuilder

    // 系统异常处理器
    private var defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler? = null

    // 初始化
    fun init() {
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable?) {
        if (throwable == null) {
            defaultUncaughtExceptionHandler!!.uncaughtException(thread, throwable)
        }

        // 创建集合对象
        stringBuilder = StringBuilder()
        // 记录时间
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS", Locale.getDefault())
        val date = simpleDateFormat.format(Date())


        // 记录应用版本信息
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            addMessage("版本名", pi.versionName)
            addMessage("版本号", pi.versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        // 记录设备信息
        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                val obj = field[null]
                if (obj != null) {
                    //  addMessage(field.getName(), obj);
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
                addMessage("error", "记录设备信息失败！" + e.message)
            }
        }

        // 添加分隔符

        //  addMessage(null, "========================   崩溃日志   =========================");


        // 记录崩溃信息
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        throwable?.printStackTrace(printWriter)
        printWriter.close()
        addMessage(null, writer.toString())

        // 生成路径，保存至/Android/data/包名，无需读写权限
        try {
            val root = context.getExternalFilesDir("log")
            val filename = "err.log"
            val file = File(root, filename)
            val fos = FileOutputStream(file)
            fos.write(stringBuilder.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            defaultUncaughtExceptionHandler!!.uncaughtException(thread, throwable)
        }

        // 启动崩溃异常页面
        val intent = Intent(context, CrashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // 请勿修改，否则无法打开页面
        intent.putExtra("error", stringBuilder.toString())
        context.startActivity(intent)
        System.exit(1) // 请勿修改，否则无法打开页面
    }

    // 添加数据
    private fun addMessage(key: String?, obj: Any) {
        // 对数组做一下处理
        if (obj is Array<*>) {
            val array = ArrayList(listOf(*obj))
            stringBuilder.append("$key=$array\n")
        }
        // 其他的都直接添加
        if (key == null) {
            stringBuilder.append(
                """
                    ${obj.toString()}
                    
                    """.trimIndent()
            )
        } else {
            stringBuilder!!.append("$key=${obj.toString()}\n")
        }
    }

    companion object {
        // 单例
        @SuppressLint("StaticFieldLeak")
        private var myUncaughtExceptionHandler: CrashHandle2? = null

        // 获取单例
        @Synchronized
        fun getInstance(ctx: Context): CrashHandle2 {
            if (myUncaughtExceptionHandler == null) {
                myUncaughtExceptionHandler = CrashHandle2(ctx)
            }
            return myUncaughtExceptionHandler as CrashHandle2
        }
    }
}