package com.phucnguyen.lovereminder.utils

import android.app.Activity
import android.content.Context

interface IPermissionHelper {
    interface PermissionListener {
        fun onPermissionGranted()
        fun onPermissionDenied(deniedPermissions: List<String>)
        fun showPermissionGuide()
    }
    fun requestPermission(activity: Activity,
                          permission: String,
                          requestCode: Int)
    fun isPermissionGranted(context: Context, permission: String): Boolean
    fun allPermissionGranted(context: Context, permissions: List<String>): Boolean
    fun requestMultiplePermissions(activity: Activity,
                                   permissions: List<String>,
                                   requestCode: Int)

    fun registerPermissionListener(requestCode: Int, listener: PermissionListener)
    fun unregisterPermissionListener(requestCode: Int, listener: PermissionListener)
}