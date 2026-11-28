package com.designlife.setwork.settings.presentation.events

import android.content.Context
import com.designlife.setwork.settings.presentation.enums.AppBackup
import com.designlife.setwork.settings.presentation.enums.GeneralSettingView

sealed class SettingEvents{
    data class OnPickerToggle(val toggleValue : Boolean) : SettingEvents()
    data class OnGeneralSettingViewChange(val generalSettingView: GeneralSettingView) : SettingEvents()
    data class OnPickerItemClick(val index: Int) : SettingEvents()
    data class OnLoaderToggle(val toggleValue : Boolean) : SettingEvents()
    data class OnImportEvent(val context: Context,val data : String) : SettingEvents()
    data object OnExportEvent : SettingEvents()
    data class OnLoaderVisibility(val state: Boolean) : SettingEvents()
    data class OnImportExportCompute(val state: Boolean) : SettingEvents()
}
