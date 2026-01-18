package com.designlife.justdo.task.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoCategoryRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository

class TaskViewModelFactory(
    private val repeatRepository: RepeatRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val todoCategoryRepository: TodoCategoryRepository,
    private val taskNotificationRepository : TaskNotificationRepository,
    private val shareViewModel: ContainerViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(repeatRepository,todoRepository,categoryRepository,todoCategoryRepository,taskNotificationRepository,shareViewModel) as T
    }
}