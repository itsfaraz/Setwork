package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.domain.entities.Deck
import java.util.Date

object DeckConverters {
    fun getDeckEntity(deck: Deck): com.designlife.justdo.common.data.entities.Deck {
        val cards = CardConverters.getFlashCardEntity(deck.cards)
        return com.designlife.justdo.common.data.entities.Deck(
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            modifiedDate = deck.modifiedDate.time,
            cards = cards,
            categoryId = deck.categoryId
        )
    }

    fun getDeck(deck: com.designlife.justdo.common.data.entities.Deck): Deck {
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