package com.designlife.justdo.common.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.designlife.justdo.common.data.converter.Converter

@Entity
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val deckId : Long = 0L,
    val deckName : String = "",
    val totalCards : Int = 0,
    val modifiedDate : Long = 0,
    val cards : List<FlashCard>
)
