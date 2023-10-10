package com.designlife.justdo.common.domain.converters

import com.designlife.justdo.common.domain.entities.Deck
import java.util.Date

object DeckConverters {
    fun getDeckEntity(deck: Deck): com.designlife.justdo.common.data.entities.Deck {
        return com.designlife.justdo.common.data.entities.Deck(
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            modifiedDate = deck.modifiedDate.time,
            cards = CardConverters.getFlashCardEntity(deck.cards),
            categoryId = deck.categoryId
        )
    }

    fun getDeck(deck: com.designlife.justdo.common.data.entities.Deck): Deck {
        return Deck(
            deckId = deck.deckId,
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            cards = CardConverters.getFlashCard(deck.cards),
            modifiedDate = Date(deck.modifiedDate),
            categoryId = deck.categoryId
        )
    }
}