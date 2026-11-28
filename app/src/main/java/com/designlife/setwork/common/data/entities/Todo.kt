package com.designlife.setwork.common.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.designlife.setwork.common.utils.serializer.LongSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val todoId : Long = 0L,
    val title : String,
    @Serializable(with = LongSerializer::class)
    @SerializedName("date")
    val date : Long,
    val note : String,
    val isRepeated : Boolean,
    val repeatIndex : Int,
    val isCompleted : Boolean,
    val categoryId : Long,
    val createdOn : Long,
    val isArchive : Boolean = false
)
