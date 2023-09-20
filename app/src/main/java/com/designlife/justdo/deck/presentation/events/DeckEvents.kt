package com.designlife.justdo.deck.presentation.events

sealed class DeckEvents{
    data class OnHeaderChange(val value : String) : DeckEvents()
    data class OnCardRemove(val index : Int) : DeckEvents()
    object OnDeckToggle : DeckEvents()
    object OnCreateCard : DeckEvents()
}
