package com.designlife.setwork.common.domain.converters

import com.designlife.setwork.common.domain.entities.Deck
import java.util.Date

object DeckConverters {
    fun getDeckEntity(deck: Deck): com.designlife.setwork.common.data.entities.Deck {
        val cards = CardConverters.getFlashCardEntity(deck.cards)
        return com.designlife.setwork.common.data.entities.Deck(
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            modifiedDate = deck.modifiedDate.time,
            cards = cards,
            categoryId = deck.categoryId
        )
    }

    fun getDeck(deck: com.designlife.setwork.common.data.entities.Deck): Deck {
        val cards = CardConverters.getFlashCard(deck.cards)
        return Deck(
            deckId = deck.deckId,
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            cards = cards,
            modifiedDate = Date(deck.modifiedDate),
            categoryId = deck.categoryId,
            isArchive = deck.isArchive
        )
    }
}