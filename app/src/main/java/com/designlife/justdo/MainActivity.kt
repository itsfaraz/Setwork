package com.designlife.justdo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.designlife.justdo.common.utils.AppServiceLocator
import com.designlife.justdo.permission.PermissionHandler
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel.Companion.getAppThemeFromOrdinal
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel.Companion.getFontSizeFromOrdinal
import com.designlife.justdo.settings.presentation.viewmodel.SettingViewModel.Companion.getListItemHeightFromOrdinal
import com.designlife.justdo.ui.theme.updateSystemColor
import com.designlife.justdo.ui.theme.updateSystemFont
import com.designlife.justdo.ui.theme.updateSystemListSize
import com.designlife.justdo.ui.theme.updateSystemUIMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isNotificationPermissionGranted = false
    private var isStorageWritePermissionGranted = false
    private var isStorageReadPermissionGranted = false
//    private var isStorageReadImagePermissionGranted = false
//    private var isStorageReadVideoPermissionGranted = false
//    private var isStorageReadAudioPermissionGranted = false
//    private var isManageExternalStoragePermissionGranted = false


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionHandler.checkAllPermissions(this)){
            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                isManageExternalStoragePermissionGranted = permissions[Manifest.permission.MANAGE_EXTERNAL_STORAGE] ?: isManageExternalStoragePermissionGranted
//                isStorageReadImagePermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: isStorageReadImagePermissionGranted
//                isStorageReadVideoPermissionGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: isStorageReadVideoPermissionGranted
//                isStorageReadAudioPermissionGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: isStorageReadAudioPermissionGranted
                isStorageWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isStorageWritePermissionGranted
                isStorageReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isStorageReadPermissionGranted
                isNotificationPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: isNotificationPermissionGranted
            }
            requestPermissions()
        }

        resizeCursorWindow()
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        observeDarkModeChanges()
        observeSettingChanges()
    }


    // Preference Changes Observe
    private fun observeSettingChanges() {
        val appStore = AppServiceLocator.provideAppStoreRepository(this)
        CoroutineScope(Dispatchers.IO).launch {
            appStore.observerSettingPreferences().collectLatest {
                Log.i("UPDATE", "observeSettingChanges: Font Update Received")
                updateSystemFont(getFontSizeFromOrdinal(it.selectedFontSize))
                updateSystemListSize(getListItemHeightFromOrdinal(it.selectedListItemHeight))
                updateSystemUIMode(getAppThemeFromOrdinal(it.selectedAppTheme))
            }
        }
    }

    companion object{
        val isDarkModeTheme : MutableState<Boolean> = mutableStateOf(false);
    }

    // System UI Mode Observe
    private fun observeDarkModeChanges() {
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            isDarkModeTheme.value = isSystemInDarkTheme()
            updateSystemColor(isDarkModeTheme.value)
            SettingViewModel.updateDarkModeSetting(isDarkModeTheme.value)
        }
    }

    private fun resizeCursorWindow() {
        try {
            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun requestPermissions() {
        isNotificationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

//        isManageExternalStoragePermissionGranted = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.MANAGE_EXTERNAL_STORAGE
//        ) == PackageManager.PERMISSION_GRANTED

        isStorageWritePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isStorageReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

//        isStorageReadImagePermissionGranted = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_MEDIA_IMAGES
//        ) == PackageManager.PERMISSION_GRANTED
//
//        isStorageReadVideoPermissionGranted = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_MEDIA_VIDEO
//        ) == PackageManager.PERMISSION_GRANTED
//
//        isStorageReadAudioPermissionGranted = ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.READ_MEDIA_AUDIO
//        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequestList = ArrayList<String>()

        if (!isNotificationPermissionGranted) {
            permissionRequestList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

//        if (!isManageExternalStoragePermissionGranted) {
//            permissionRequestList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//        }

        if (!isStorageWritePermissionGranted) {
            permissionRequestList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (!isStorageReadPermissionGranted) {
            permissionRequestList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

//        if (!isStorageReadImagePermissionGranted) {
//            permissionRequestList.add(Manifest.permission.READ_MEDIA_IMAGES)
//        }
//
//        if (!isStorageReadVideoPermissionGranted) {
//            permissionRequestList.add(Manifest.permission.READ_MEDIA_VIDEO)
//        }
//
//        if (!isStorageReadAudioPermissionGranted) {
//            permissionRequestList.add(Manifest.permission.READ_MEDIA_AUDIO)
//        }

        if (permissionRequestList.isNotEmpty()) {
            permissionLauncher.launch(permissionRequestList.toTypedArray())
        }
    }
}