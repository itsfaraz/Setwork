package com.designlife.setwork.settings.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.designlife.setwork.common.domain.entities.SettingPreference
import com.designlife.setwork.common.domain.repositories.appstore.AppStoreRepository
import com.designlife.setwork.common.utils.HardStorage
import com.designlife.setwork.common.utils.HardStorage.backupImport
import com.designlife.setwork.common.utils.enums.ViewType
import com.designlife.setwork.settings.presentation.entity.LoaderStatus
import com.designlife.setwork.settings.presentation.enums.AppBackup
import com.designlife.setwork.settings.presentation.enums.AppFontSize
import com.designlife.setwork.settings.presentation.enums.AppListHeight
import com.designlife.setwork.settings.presentation.enums.AppTheme
import com.designlife.setwork.settings.presentation.enums.GeneralSettingView
import com.designlife.setwork.settings.presentation.enums.LoaderState
import com.designlife.setwork.settings.presentation.events.SettingEvents
import com.designlife.setwork.ui.theme.updateSystemColor
import com.designlife.setwork.ui.theme.updateSystemFont
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel(
    private val appStoreRepository: AppStoreRepository
) : ViewModel() {


    private val _pickerVisibility: MutableState<Boolean> = mutableStateOf(false)
    val pickerVisibility: State<Boolean> = _pickerVisibility

    private val _defaultScreen: MutableState<ViewType> = mutableStateOf(ViewType.TASK)
    val defaultScreen: State<ViewType> = _defaultScreen

    private val _appTheme: MutableState<AppTheme> = mutableStateOf(AppTheme.LIGHT)
    val appTheme: State<AppTheme> = _appTheme

    private val _fontSize: MutableState<AppFontSize> = mutableStateOf(AppFontSize.MEDIUM)
    val fontSize: State<AppFontSize> = _fontSize

    private val _listHeight: MutableState<AppListHeight> = mutableStateOf(AppListHeight.MEDIUM)
    val listHeight: State<AppListHeight> = _listHeight

    private val _selectedPickerView: MutableState<GeneralSettingView> =
        mutableStateOf(GeneralSettingView.DEFAULT_SCREEN)
    val selectedPickerView: State<GeneralSettingView> = _selectedPickerView

    private val _pickerItemList: MutableState<List<String>> = mutableStateOf(emptyList())
    val pickerItemList: State<List<String>> = _pickerItemList

    private val screenList: List<String> = listOf(
        "Task Screen",
        "Note Screen",
        "Deck Screen"
    )

    private val appThemeList: List<String> = listOf(
        "Light",
        "Dark",
    )

    private val fontSizeList: List<String> = listOf(
        "Small",
        "Medium",
        "Large",
    )

    private val lineHeightList: List<String> = listOf(
        "Half",
        "One",
        "One & Half",
    )

    private val _loaderVisibility: MutableState<Boolean> = mutableStateOf(false)
    val loaderVisibility: State<Boolean> = _loaderVisibility

    private val _appBackupSetting: MutableState<AppBackup> = mutableStateOf(AppBackup.NONE)

    private val _loaderStatus: MutableState<LoaderStatus> = mutableStateOf(initLoaderStatus())
    val loaderStatus: State<LoaderStatus> = _loaderStatus


    private val _isExportReady: MutableState<Boolean> = mutableStateOf(false)
    val isExportReady: State<Boolean> = _isExportReady


    fun onEvent(event: SettingEvents) {
        when (event) {
            is SettingEvents.OnPickerToggle -> {
                _pickerVisibility.value = event.toggleValue
            }

            is SettingEvents.OnGeneralSettingViewChange -> {
                _selectedPickerView.value = event.generalSettingView
                _pickerItemList.value = when (event.generalSettingView) {
                    GeneralSettingView.DEFAULT_SCREEN -> screenList
                    GeneralSettingView.APP_THEME -> appThemeList
                    GeneralSettingView.FONT_SIZE -> fontSizeList
                    GeneralSettingView.LIST_HEIGHT -> lineHeightList
                }
            }

            is SettingEvents.OnPickerItemClick -> {
                when (_selectedPickerView.value) {
                    GeneralSettingView.DEFAULT_SCREEN -> {
                        _defaultScreen.value = when (event.index) {
                            0 -> ViewType.TASK
                            1 -> ViewType.NOTE
                            2 -> ViewType.DECK
                            else -> ViewType.TASK
                        }
                        updatePreferenceSetting()
                    }

                    GeneralSettingView.APP_THEME -> {
                        _appTheme.value = when (event.index) {
                            0 -> AppTheme.LIGHT
                            1 -> AppTheme.DARK
                            else -> AppTheme.LIGHT
                        }
                        updateAppTheme(_appTheme.value)
                        updatePreferenceSetting()
                    }

                    GeneralSettingView.FONT_SIZE -> {
                        _fontSize.value = when (event.index) {
                            0 -> AppFontSize.SMALL
                            1 -> AppFontSize.MEDIUM
                            2 -> AppFontSize.LARGE
                            else -> AppFontSize.MEDIUM
                        }
                        updateAppFont(_fontSize.value)
                        updatePreferenceSetting()
                    }

                    GeneralSettingView.LIST_HEIGHT -> {
                        _listHeight.value = when (event.index) {
                            0 -> AppListHeight.SMALL
                            1 -> AppListHeight.MEDIUM
                            2 -> AppListHeight.LARGE
                            else -> AppListHeight.MEDIUM
                        }
                        updatePreferenceSetting()
                    }
                }
            }

            is SettingEvents.OnLoaderToggle -> {
                _loaderVisibility.value = event.toggleValue
            }

            is SettingEvents.OnImportEvent -> {
                importData(event.context, event.data)
            }

            is SettingEvents.OnExportEvent -> {
                exportData()
            }

            is SettingEvents.OnImportExportCompute -> {
                _isExportReady.value = event.state
            }

            is SettingEvents.OnLoaderVisibility -> {
                _loaderVisibility.value = event.state
            }
        }
    }

    private fun initLoaderStatus(): LoaderStatus {
        return LoaderStatus("", LoaderState.NONE, "")
    }

    fun initSettingPreferences() {
        viewModelScope.launch {
            val settingPreferences = appStoreRepository.getSettingPreferences()
            settingPreferences?.let {
                Log.i("SETTING_PREFERENCE", "initSettingPreferences: ${it.toString()}")
                setPreferenceValues(it)
            }
        }
    }


    private fun updatePreferenceSetting() {
        viewModelScope.launch(Dispatchers.IO) {
            val settingPreference = SettingPreference(
                defaultScreen = getOrdinalFromDefaultScreen(_defaultScreen.value),
                appTheme = getOrdinalFromAppTheme(_appTheme.value),
                fontSize = getOrdinalFromFontSize(_fontSize.value),
                listItemHeight = getOrdinalFromListItemHeight(_listHeight.value)
            )
            appStoreRepository.updateSettingPreferences(settingPreference)
        }
    }

    private fun updateListSize(@Suppress("UNUSED_PARAMETER") value: AppListHeight) {

    }

    private fun setPreferenceValues(settingPreferences: SettingPreference) {
        Log.i("SCREEN", "setPreferenceValues: ${settingPreferences}")
        _defaultScreen.value = getDefaultScreenFromOrdinal(settingPreferences.defaultScreen)
        _appTheme.value = getAppThemeFromOrdinal(settingPreferences.appTheme)
        _fontSize.value = getFontSizeFromOrdinal(settingPreferences.fontSize)
        _listHeight.value = getListItemHeightFromOrdinal(settingPreferences.listItemHeight)
        updateAppTheme(_appTheme.value)
        updateAppFont(_fontSize.value)
//        updateListSize(_listHeight.value)
    }

    private fun updateAppFont(value: AppFontSize) {
        updateSystemFont(value)
    }

    private fun updateAppTheme(appTheme: AppTheme) {
        when (appTheme) {
            AppTheme.LIGHT -> updateSystemColor(false)
            AppTheme.DARK -> updateSystemColor(true)
        }
    }

    companion object {
        private val _darkModeStatus: MutableState<Boolean> = mutableStateOf(false)
        val darkModeStatus: State<Boolean> = _darkModeStatus

        fun updateDarkModeSetting(value: Boolean) {
            _darkModeStatus.value = value
        }

        fun getDefaultScreenFromOrdinal(ordinal: Int): ViewType {
            return when (ordinal) {
                0 -> ViewType.TASK
                1 -> ViewType.NOTE
                2 -> ViewType.DECK
                3 -> ViewType.SETTING
                else -> ViewType.TASK
            }
        }

        fun getAppThemeFromOrdinal(ordinal: Int): AppTheme {
            return when (ordinal) {
                0 -> AppTheme.LIGHT
                1 -> AppTheme.DARK
                else -> AppTheme.LIGHT
            }
        }

        fun getFontSizeFromOrdinal(ordinal: Int): AppFontSize {
            return when (ordinal) {
                0 -> AppFontSize.SMALL
                1 -> AppFontSize.MEDIUM
                2 -> AppFontSize.LARGE
                else -> AppFontSize.MEDIUM
            }
        }

        fun getListItemHeightFromOrdinal(ordinal: Int): AppListHeight {
            return when (ordinal) {
                0 -> AppListHeight.SMALL
                1 -> AppListHeight.MEDIUM
                2 -> AppListHeight.LARGE
                else -> AppListHeight.MEDIUM
            }
        }

    }

    private fun getOrdinalFromDefaultScreen(viewType: ViewType): Int {
        return when (viewType) {
            ViewType.TASK -> 0
            ViewType.NOTE -> 1
            ViewType.DECK -> 2
            ViewType.SETTING -> 3
            else -> 0
        }
    }

    private fun getOrdinalFromAppTheme(appTheme: AppTheme): Int {
        return when (appTheme) {
            AppTheme.LIGHT -> 0
            AppTheme.DARK -> 1
        }
    }

    private fun getOrdinalFromFontSize(fontSize: AppFontSize): Int {
        return when (fontSize) {
            AppFontSize.SMALL -> 0
            AppFontSize.MEDIUM -> 1
            AppFontSize.LARGE -> 2
        }
    }

    private fun getOrdinalFromListItemHeight(appListHeight: AppListHeight): Int {
        return when (appListHeight) {
            AppListHeight.SMALL -> 0
            AppListHeight.MEDIUM -> 1
            AppListHeight.LARGE -> 2
        }
    }

    private fun exportData() {
        _loaderVisibility.value = true
        _loaderStatus.value = _loaderStatus.value.copy(
            title = "Exporting Data", loaderState = LoaderState.PENDING,
            message = "Select a folder for a backup ..."
        )
        viewModelScope.launch(Dispatchers.IO) {
            HardStorage.backupExport()
            delay(400)
            withContext(Dispatchers.Main.immediate) {
                _isExportReady.value = true
                _loaderStatus.value = _loaderStatus.value.copy(
                    title = "Exported Data", loaderState = LoaderState.SUCCESS,
                    message = "Export Completed"
                )
                _loaderVisibility.value = false
                _loaderStatus.value = initLoaderStatus()
            }
        }
    }

    private fun importData(context: Context, data: String) {
        _loaderStatus.value = _loaderStatus.value.copy(
            title = "Importing Data",
            loaderState = LoaderState.PENDING,
            message = "Choose setwork import file"
        )
        viewModelScope.launch(Dispatchers.IO) {
            backupImport(context, data)
            withContext(Dispatchers.Main.immediate) {
                _loaderStatus.value = _loaderStatus.value.copy(
                    title = "Imported Data", loaderState = LoaderState.SUCCESS,
                    message = "Imported completed"
                )
            }
            delay(1000)
            withContext(Dispatchers.Main.immediate) {
                _loaderVisibility.value = false
                _loaderStatus.value = initLoaderStatus()
            }
        }
    }
}