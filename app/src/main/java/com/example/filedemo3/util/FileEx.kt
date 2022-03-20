package com.example.filedemo3.util

import java.io.File

fun File.hasParent(): Boolean {
    this.parent?.let {
        return true
    }
    return false
}
