package com.designlife.justdo.container.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository

class ContainerViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val repeatRepository: RepeatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContainerViewModel(categoryRepository,repeatRepository) as T
    }
}