package com.designlife.justdo.common.domain.converters
import com.designlife.justdo.common.domain.entities.FlashCard


object CardConverters {
    fun getFlashCardEntity(cards: List<FlashCard>) : List<com.designlife.justdo.common.data.entities.FlashCard>{
        return cards.map { card ->
            com.designlife.justdo.common.data.entities.FlashCard(
                frontContent = card.frontContent,
                backContent = card.backContent
            )
        }
    }

    fun getFlashCard(cards: List<com.designlife.justdo.common.data.entities.FlashCard>) : List<FlashCard> {
        return cards.map { card ->
            FlashCard(
                frontContent = card.frontContent,
                backContent = card.backContent
            )
        }
    }
}