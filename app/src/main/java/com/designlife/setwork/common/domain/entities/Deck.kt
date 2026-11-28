package com.designlife.setwork.common.domain.entities

import com.designlife.setwork.common.data.entities.Deck
import java.util.Date
import kotlin.String


data class Deck(
    val deckId : Long = 0L,
    val deckName : String = "",
    val totalCards : Int = 0,
    val modifiedDate : Date,
    val categoryId : Long,
    val isArchive : Boolean,
    val cards : List<FlashCard> = emptyList()
) : SearchMarker(referenceId = deckId){
    fun toDeck() : Deck{
        return com.designlife.setwork.common.data.entities.Deck(
            deckId = this.deckId,
            deckName = this.deckName,
            totalCards = this.totalCards,
            modifiedDate = this.modifiedDate.time,
            categoryId = this.categoryId,
            isArchive = this.isArchive,
            cards = this.cards.map { it.toFlashCard() }
        )
    }
}
