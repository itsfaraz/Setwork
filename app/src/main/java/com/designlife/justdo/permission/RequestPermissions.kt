package com.designlife.justdo.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.ACTION_REQUEST_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSIONS
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions.Companion.EXTRA_PERMISSION_GRANT_RESULTS


//class RequestPermissions : ActivityResultContract<Array<String>, Map<String, Boolean>>() {
//    override fun createIntent(
//        context: Context,
//        input: Array<String>
//    ): Intent {
//        return Intent(ACTION_REQUEST_PERMISSIONS).putExtra(EXTRA_PERMISSIONS, input);
//    }
//
//    override fun parseResult(
//        resultCode: Int,
//        intent: Intent?
//    ): Map<String, Boolean> {
//        if (resultCode !== Activity.RESULT_OK) return emptyMap()
//        if (intent == null) return emptyMap()
//
//        val permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS)
//        val grantResults = intent.getIntArrayExtra(EXTRA_PERMISSION_GRANT_RESULTS)
//        if (grantResults == null || permissions == null) return emptyMap()
//
//        val result = HashMap<String,Boolean>();
//        for (i in permissions.size) {
//            result.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
//        }
//        return result
//    }
//}