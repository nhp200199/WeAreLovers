package com.phucnguyen.lovereminder.core.common

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import javax.inject.Inject

class PermissionHelperImpl @Inject constructor()
    : IPermissionHelper {

    private val permissionListeners: MutableMap<Int, MutableSet<IPermissionHelper.PermissionListener>> = mutableMapOf()

    override fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun allPermissionGranted(context: Context, permissions: List<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    override fun registerPermissionListener(requestCode: Int, listener: IPermissionHelper.PermissionListener) {
        if (!permissionListeners.containsKey(requestCode)) {
            permissionListeners[requestCode] = mutableSetOf(listener)
        } else {
            permissionListeners[requestCode]?.add(listener)
        }
    }

    override fun unregisterPermissionListener(requestCode: Int, listener: IPermissionHelper.PermissionListener) {
        permissionListeners[requestCode]?.let {
            it.remove(listener)
            if (it.isEmpty()) {
                permissionListeners.remove(requestCode)
            }
        }
    }

    private fun getDeniedPermissions(context: Context, permissions: List<String>): Array<String> {
        return permissions.filter { !isPermissionGranted(context, it) }.toTypedArray()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionListeners.containsKey(requestCode).let {
            if (permissions.isEmpty() || grantResults.isEmpty()) {
                permissionListeners[requestCode]?.forEach { it.onPermissionDenied(permissions.toList()) }
            } else {
                if (permissions.size == 1) {
                    handleSinglePermissionResult(permissions, grantResults[0], requestCode)
                } else {
                    handleMultiplePermissionResult(permissions, grantResults)
                }
            }
        }
    }

    private fun handleSinglePermissionResult(permissions: Array<out String>, grantResult: Int, requestCode: Int) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            permissionListeners[requestCode]?.forEach {
                it.onPermissionGranted()
            }
        } else {
            permissionListeners[requestCode]?.forEach {
                it.onPermissionDenied(permissions.toList())
            }
        }
    }

    private fun handleMultiplePermissionResult(permissions: Array<out String>, grantResults: IntArray) {
        val deniedPermissions = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission)
            }
        }
        if (deniedPermissions.isEmpty()) {
            permissionListeners.forEach {
                it.value.forEach { listener ->
                    listener.onPermissionGranted()
                }
            }
        } else {
            permissionListeners.forEach {
                it.value.forEach { listener ->
                    listener.onPermissionDenied(deniedPermissions)
                }
            }
        }
    }
}