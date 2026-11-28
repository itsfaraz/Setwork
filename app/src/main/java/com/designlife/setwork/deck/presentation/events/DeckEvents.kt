package com.designlife.setwork.deck.presentation.events

import com.designlife.setwork.common.domain.entities.FlashCard

sealed class DeckEvents{
    data class OnHeaderChange(val value : String) : DeckEvents()
    data class OnCardRemove(val index : Int) : DeckEvents()
    object OnDeckToggle : DeckEvents()
    object OnCreateCard : DeckEvents()
    data class OnEditStateChange(val editState : Boolean) : DeckEvents()
    data class OnUpdateCardChange(val index : Int,val card : FlashCard) : DeckEvents()
    object OnPersistCardChanges : DeckEvents()
    object OnDeckDeleteEvent : DeckEvents()
    data object OnCategoryDemand : DeckEvents()
    data class OnDeckDemand(val id : Long) : DeckEvents()
    data class OnCategoryIndexChange(val value : Int) : DeckEvents()
    data class OnInsert(val isAutoSave : Boolean = false) : DeckEvents()
    data class OnUpdate(val isAutoSave : Boolean = false) : DeckEvents()
    data object OnClear : DeckEvents()
}
