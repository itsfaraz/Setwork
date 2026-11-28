package com.designlife.setwork.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.calendar.DateGenerator
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository
import com.designlife.setwork.common.domain.repositories.NoteRepository
import com.designlife.setwork.common.domain.repositories.TodoRepository
import com.designlife.setwork.home.domain.usecase.LoadIntialDatesUseCase
import com.designlife.setwork.home.domain.usecase.LoadNextDatesSetUseCase
import com.designlife.setwork.home.domain.usecase.LoadPreviousDatesSetUseCase
import com.designlife.justdo_provider.SetworkProvider

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory (
    private val setworkProvider: SetworkProvider,
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
        return HomeViewModel(setworkProvider,dateGenerator,todoRepository,categoryRepository,noteRepository,deckRepository,loadInitialDateUseCase,loadNextDatesSetUseCase, loadPreviousDatesSetUseCase) as T
    }
}