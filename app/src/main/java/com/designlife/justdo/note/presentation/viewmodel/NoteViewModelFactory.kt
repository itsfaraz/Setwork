package com.designlife.justdo.note.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.repeat.RepeatRepository
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.container.presentation.viewmodel.ContainerViewModel
import com.designlife.orchestrator.notification.repository.TaskNotificationRepository

class NoteViewModelFactory(
    private val noteRepository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository) as T
    }
}