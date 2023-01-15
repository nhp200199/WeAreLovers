package com.phucnguyen.lovereminder.model

import android.net.Uri

class Image {
    var uri: Uri? = null
    var description: String? = null
    var isChecked = false

    constructor() {}
    constructor(uri: Uri?, description: String?) {
        this.uri = uri
        this.description = description
    }

    fun setCheck(checked: Boolean): Boolean {
        return checked.also { isChecked = it }
    }

    fun toggleChecked() {
        isChecked = !isChecked
    }
}