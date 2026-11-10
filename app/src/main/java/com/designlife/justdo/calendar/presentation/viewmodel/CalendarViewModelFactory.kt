package com.designlife.justdo.calendar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.justdo.common.domain.calendar.DateGenerator
import com.designlife.justdo.common.domain.repositories.CategoryRepository
import com.designlife.justdo.common.domain.repositories.DeckRepository
import com.designlife.justdo.common.domain.repositories.NoteRepository
import com.designlife.justdo.common.domain.repositories.TodoRepository
import com.designlife.justdo.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.justdo.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.justdo.home.domain.usecase.LoadPreviousDatesSetUseCase

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory (
    private val dateGenerator: DateGenerator,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
    private val noteRepository: NoteRepository,
    private val deckRepository: DeckRepository,
    private val loadInitialDateUseCase : LoadIntialDatesUseCase,
    private val loadNextDatesSetUseCase: LoadNextDatesSetUseCase,
    private val loadPreviousDatesSetUseCase: LoadPreviousDatesSetUseCase
    ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CalendarViewModel(dateGenerator,todoRepository,categoryRepository,noteRepository,deckRepository,loadInitialDateUseCase,loadNextDatesSetUseCase, loadPreviousDatesSetUseCase) as T
    }
}