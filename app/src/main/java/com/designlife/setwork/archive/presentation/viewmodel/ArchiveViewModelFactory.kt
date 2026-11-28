package com.designlife.setwork.archive.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository

@Suppress("UNCHECKED_CAST")
class ArchiveViewModelFactory (
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository,
    private val deckRepository: DeckRepository,
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArchiveViewModel(todoRepository,categoryRepository,noteRepository,deckRepository) as T
    }
}