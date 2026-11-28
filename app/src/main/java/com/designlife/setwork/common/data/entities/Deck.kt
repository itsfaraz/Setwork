package com.designlife.setwork.common.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.designlife.setwork.common.data.converter.Converter
import com.designlife.setwork.common.utils.serializer.LongSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Entity
data class Deck(
    @PrimaryKey(autoGenerate = true)
    val deckId : Long = 0L,
    val deckName : String = "",
    val totalCards : Int = 0,
    @Serializable(with = LongSerializer::class)
    @SerializedName("modifiedDate")
    val modifiedDate : Long = 0,
    val categoryId : Long,
    val cards : List<FlashCard>,
    val isArchive : Boolean = false,
)
