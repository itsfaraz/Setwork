package com.designlife.justdo.common.domain.repositories

import com.designlife.justdo.common.data.room.dao.DeckDao
import com.designlife.justdo.common.domain.converters.CardConverters
import com.designlife.justdo.common.domain.converters.DeckConverters
import com.designlife.justdo.common.domain.entities.Deck
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepository(private val deckDao: DeckDao) {

    suspend fun insertDeck(deck: Deck): Long {
        return deckDao.insertDeck(DeckConverters.getDeckEntity(deck))
    }

    suspend fun getAllDecks(): Flow<List<Deck>> {
        return deckDao.getAllDecks().map { rawDeckList ->
            rawDeckList.map { rawDeck ->
                DeckConverters.getDeck(rawDeck)
            }
        }
    }

    suspend fun getDeckById(deckId: Long): Deck {
        return DeckConverters.getDeck(deckDao.getDeckById(deckId))
    }

    suspend fun updateDeck(deckId: Long, deck: Deck): Unit {
        deckDao.updateDeckById(
            deckId = deckId,
            deckName = deck.deckName,
            totalCards = deck.totalCards,
            modifiedDate = deck.modifiedDate.time,
            cards = CardConverters.getFlashCardEntity(deck.cards)
        )
    }

    suspend fun deleteDeck(deckId: Long): Unit {
        deckDao.deleteDeckById(deckId)
    }

}