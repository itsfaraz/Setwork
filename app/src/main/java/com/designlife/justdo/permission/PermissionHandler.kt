package com.designlife.justdo.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

object PermissionHandler {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val notificationPermissionString = android.Manifest.permission.POST_NOTIFICATIONS
    val storageWritePermissionString = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    val storageReadPermissionString = android.Manifest.permission.READ_EXTERNAL_STORAGE
//    @RequiresApi(Build.VERSION_CODES.R)
//    val manageExternalStoragePermissionString = android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    val storageReadImagePermissionString = android.Manifest.permission.READ_MEDIA_IMAGES
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    val storageReadVideoPermissionString = android.Manifest.permission.READ_MEDIA_VIDEO
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    val storageReadAudioPermissionString = android.Manifest.permission.READ_MEDIA_AUDIO

    fun checkAllPermissions(context: Context) : Boolean{
//        val manageExternalStoragePermissionResult = context.checkCallingOrSelfPermission(manageExternalStoragePermissionString)
//        val manageExternalStoragePermission = manageExternalStoragePermissionResult == PackageManager.PERMISSION_GRANTED

        val storageWritePermissionResult = context.checkCallingOrSelfPermission(storageWritePermissionString)
        val storageWritePermission = storageWritePermissionResult == PackageManager.PERMISSION_GRANTED

        val storageReadPermissionResult = context.checkCallingOrSelfPermission(storageReadPermissionString)
        val storageReadPermission = storageReadPermissionResult == PackageManager.PERMISSION_GRANTED

//        val storageReadImagePermissionResult = context.checkCallingOrSelfPermission(storageReadImagePermissionString)
//        val storageReadImagePermission = storageReadImagePermissionResult == PackageManager.PERMISSION_GRANTED
//
//        val storageReadVideoPermissionResult = context.checkCallingOrSelfPermission(storageReadVideoPermissionString)
//        val storageReadVideoPermission = storageReadVideoPermissionResult == PackageManager.PERMISSION_GRANTED
//
//        val storageReadAudioPermissionResult = context.checkCallingOrSelfPermission(storageReadAudioPermissionString)
//        val storageReadAudioPermission = storageReadAudioPermissionResult == PackageManager.PERMISSION_GRANTED

        val notificationPermissionResult = context.checkCallingOrSelfPermission(notificationPermissionString)
        val notificationPermission = notificationPermissionResult == PackageManager.PERMISSION_GRANTED

        return storageWritePermission && storageReadPermission && notificationPermission // && storageReadImagePermission && storageReadVideoPermission && storageReadAudioPermission && manageExternalStoragePermission
    }

}