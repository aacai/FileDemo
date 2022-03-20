package com.example.filedemo3

import android.view.ViewGroup
import com.example.filedemo3.databinding.FileItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class FileListAdapter() : BaseAdapter<FileItemBinding,File>() {

    override fun getViewBinding(parent: ViewGroup): FileItemBinding =
        newBindingViewHolder(parent, false)

    override fun initView(binding: FileItemBinding, position: Int, data: File) {
        binding.apply {
            iconView.setImageResource(
                if (data.isDirectory) {
                    R.drawable.ic_round_folder
                } else {
                    R.drawable.ic_round_file
                }
            )
            titleView.text = data.name
            summaryView.text = ""
        }
    }
}