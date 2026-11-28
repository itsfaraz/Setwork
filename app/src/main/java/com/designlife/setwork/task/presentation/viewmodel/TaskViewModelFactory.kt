package com.designlife.setwork.task.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.repeat.RepeatRepository
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoCategoryRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.container.presentation.viewmodel.ContainerViewModel
import com.designlife.justdo_provider.SetworkProvider
import com.designlife.orchestrator.NotificationScheduler

@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(
    private val setworkProvider: SetworkProvider,
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    private val notificationScheduler: NotificationScheduler,
    private val shareViewModel: ContainerViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(setworkProvider,repeatRepository,todoRepository,categoryRepository,todoCategoryRepository,notificationScheduler,shareViewModel) as T
    }
}