package com.designlife.setwork.container.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.repeat.RepeatRepository
import com.designlife.setwork.common.domain.repositories.CategoryRepository

@Suppress("UNCHECKED_CAST")
class ContainerViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val repeatRepository: RepeatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContainerViewModel(categoryRepository,repeatRepository) as T
    }
}