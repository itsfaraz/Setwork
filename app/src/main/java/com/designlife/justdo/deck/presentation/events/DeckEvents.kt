package com.designlife.justdo.deck.presentation.events

import com.designlife.justdo.common.domain.entities.FlashCard
import com.designlife.justdo.note.presentation.events.NoteEvents

sealed class DeckEvents{
    data class OnHeaderChange(val value : String) : DeckEvents()
    data class OnCardRemove(val index : Int) : DeckEvents()
    object OnDeckToggle : DeckEvents()
    object OnCreateCard : DeckEvents()
    data class OnEditStateChange(val editState : Boolean) : DeckEvents()
    data class OnUpdateCardChange(val index : Int,val card : FlashCard) : DeckEvents()
    object OnPersistCardChanges : DeckEvents()
    object OnDeckDeleteEvent : DeckEvents()
    data class OnCategoryIndexChange(val value : Int) : DeckEvents()
}
