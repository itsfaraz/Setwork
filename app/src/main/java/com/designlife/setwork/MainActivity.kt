package com.designlife.setwork

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.CursorWindow
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.designlife.setwork.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.setwork.permission.PermissionHandler
import com.designlife.setwork.settings.presentation.viewmodel.SettingViewModel
import com.designlife.setwork.settings.presentation.viewmodel.SettingViewModel.Companion.getAppThemeFromOrdinal
import com.designlife.setwork.settings.presentation.viewmodel.SettingViewModel.Companion.getFontSizeFromOrdinal
import com.designlife.setwork.settings.presentation.viewmodel.SettingViewModel.Companion.getListItemHeightFromOrdinal
import com.designlife.justdo.setworkllm.SetworkOLLM
import com.designlife.setwork.ui.theme.updateSystemColor
import com.designlife.setwork.ui.theme.updateSystemFont
import com.designlife.setwork.ui.theme.updateSystemListSize
import com.designlife.setwork.ui.theme.updateSystemUIMode
import com.designlife.justdo_provider.SetworkProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import org.koin.android.ext.android.inject
import java.lang.reflect.Field
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isNotificationPermissionGranted = false
    private var isStorageWritePermissionGranted = false
    private var isLocationPermissionGranted = false

    //    private var isStorageReadImagePermissionGranted = false
//    private var isStorageReadVideoPermissionGranted = false
//    private var isStorageReadAudioPermissionGranted = false
//    private var isManageExternalStoragePermissionGranted = false


    private var isStorageReadPermissionGranted = false

    private val setworkProvider : SetworkProvider by inject()
    private val appStoreRepository : AppStoreRepository by inject()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (!PermissionHandler.checkAllPermissions(this)){
            permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
//                isManageExternalStoragePermissionGranted = permissions[Manifest.permission.MANAGE_EXTERNAL_STORAGE] ?: isManageExternalStoragePermissionGranted
//                isStorageReadImagePermissionGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: isStorageReadImagePermissionGranted
//                isStorageReadVideoPermissionGranted = permissions[Manifest.permission.READ_MEDIA_VIDEO] ?: isStorageReadVideoPermissionGranted
//                isStorageReadAudioPermissionGranted = permissions[Manifest.permission.READ_MEDIA_AUDIO] ?: isStorageReadAudioPermissionGranted
                isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: isLocationPermissionGranted
                isStorageWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isStorageWritePermissionGranted
                isStorageReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isStorageReadPermissionGranted
                isNotificationPermissionGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] ?: isNotificationPermissionGranted
            }
            requestPermissions()
        }
        lifecycleScope.launch {
            ollmSDK = SetworkOLLM.chatSDK(this@MainActivity)
        }
        Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        resizeCursorWindow()
        setContentView(R.layout.activity_main)
        observeDarkModeChanges()
        observeSettingChanges()
        deviceKnown()
    }

    private fun deviceKnown() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) return

            if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            location?.let {
                val isFresh = System.currentTimeMillis() - it.time < 10 * 60 * 1000

                if (isFresh) {
                    setworkProvider
                        .addLocation(it.latitude, it.longitude)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    // Preference Changes Observe
    @OptIn(InternalSerializationApi::class)
    private fun observeSettingChanges() {

        CoroutineScope(Dispatchers.IO).launch {
            appStoreRepository.observerSettingPreferences().collectLatest {
                updateSystemFont(getFontSizeFromOrdinal(it.selectedFontSize))
                updateSystemListSize(getListItemHeightFromOrdinal(it.selectedListItemHeight))
                updateSystemUIMode(getAppThemeFromOrdinal(it.selectedAppTheme))
            }
        }
    }

    companion object{
        val isDarkModeTheme : MutableState<Boolean> = mutableStateOf(false)
        internal var ollmSDK : SetworkOLLM? = null
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

        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
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


        if (!isLocationPermissionGranted) {
            permissionRequestList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

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



    override fun onDestroy() {
        super.onDestroy()
        try {
            SetworkOLLM.destroy()
            lifecycleScope.cancel()
            this.viewModelStore.clear()
//            AppServiceLocator.clear()
        }catch (e : Exception){
            Log.e("CLEAN_FLOW", "onDestroy: MainActivity ::Exception during state clearing")
        }
    }
}