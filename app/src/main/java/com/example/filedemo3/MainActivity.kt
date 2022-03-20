package com.example.filedemo3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filedemo3.databinding.ActivityMainBinding
import com.example.filedemo3.util.hasParent
import java.io.File

class MainActivity : AppCompatActivity() {
    private val contents: MutableList<File> = ArrayList()
    private lateinit var currentFolder: File
    private var mExitTime: Long = 0
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val adapter = FileListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestManagerAllFilesPermission(this)
        println("setData")
        setData()
        initView()
    }

    fun initView() {
        println("初始化recycler")
        val recyclerView = binding.recycler
        recyclerView.adapter = adapter
        adapter.addData(contents)
        println("contents=$contents")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        println("适配器项目总共" + adapter.itemCount)
        adapter.setOnItemClick { _, _, data ->
//            val item = data[position]
            if (data.isDirectory) {
                switchDirectory(File(data.absolutePath + File.separator))
                Toast.makeText(this, data.name, Toast.LENGTH_SHORT).show()
                return@setOnItemClick
            }
            Toast.makeText(this, data.name, Toast.LENGTH_SHORT).show()
        }
        println("初始化完毕")
    }

    fun setData() {
        currentFolder = Environment.getExternalStorageDirectory()
        contents.addAll(currentFolder.listFiles()!!.sortedWith(compareBy({ !it.isDirectory }, {
            it.nameWithoutExtension.lowercase(Locale.getDefault())
        })))
    }

    fun switchDirectory(dir: File) {
        if (dir.isFile) return
        val list = dir.listFiles()!!.toMutableList().sortedWith(compareBy({ !it.isDirectory }, {
            it.nameWithoutExtension.lowercase(Locale.getDefault())
        }))
        currentFolder = dir
        adapter.reset(list)
    }
    
    private fun requestManagerAllFilesPermission(c: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "请给我权限", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                intent.data = Uri.parse("package:${c.packageName}")
                c.startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        if (currentFolder.hasParent()) {
            currentFolder = currentFolder.parentFile!!
            switchDirectory(currentFolder)
            return
        } else if (System.currentTimeMillis() - this.mExitTime > (2000)) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            this.mExitTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()
    }
}
