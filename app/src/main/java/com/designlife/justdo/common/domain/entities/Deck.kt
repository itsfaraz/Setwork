package com.designlife.justdo.common.domain.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


data class Deck(
    val deckId : Long = 0L,
    val deckName : String = "",
    val totalCards : Int = 0,
    val modifiedDate : Date,
    val cards : List<FlashCard> = emptyList()
)
