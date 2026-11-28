package com.designlife.setwork.common.domain.converters
import com.designlife.setwork.common.domain.entities.FlashCard


object CardConverters {
    fun getFlashCardEntity(cards: List<FlashCard>) : List<com.designlife.setwork.common.data.entities.FlashCard>{
        return cards.map { card ->
            com.designlife.setwork.common.data.entities.FlashCard(
                frontContent = card.frontContent,
                backContent = card.backContent
            )
        }
    }

    fun getFlashCard(cards: List<com.designlife.setwork.common.data.entities.FlashCard>) : List<FlashCard> {
        return cards.map { card ->
            FlashCard(
                frontContent = card.frontContent,
                backContent = card.backContent
            )
        }
    }
}