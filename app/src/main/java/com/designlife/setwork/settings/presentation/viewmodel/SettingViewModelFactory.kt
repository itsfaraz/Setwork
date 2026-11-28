package com.designlife.setwork.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.repositories.appstore.AppStoreRepository

@Suppress("UNCHECKED_CAST")
class SettingViewModelFactory(
    private val appStoreRepository: AppStoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(appStoreRepository) as T
    }
}