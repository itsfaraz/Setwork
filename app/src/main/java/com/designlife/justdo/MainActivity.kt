package com.designlife.justdo

import android.annotation.SuppressLint
import android.database.CursorWindow
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.NavHostFragment
import com.designlife.justdo.common.utils.AppServiceLocator
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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}