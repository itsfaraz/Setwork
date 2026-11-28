package com.designlife.setwork.deck.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.designlife.setwork.common.domain.repositories.CategoryRepository
import com.designlife.setwork.common.domain.repositories.DeckRepository

@Suppress("UNCHECKED_CAST")
class DeckViewModelFactory(
    private val deckRepository: DeckRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeckViewModel(deckRepository, categoryRepository) as T
    }
}