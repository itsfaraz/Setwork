package com.designlife.justdo.note.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository

class NoteViewModelFactory(
    private val noteRepository: NoteRepository,
    private val categoryRepository: CategoryRepository,

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepository,categoryRepository) as T
    }
}